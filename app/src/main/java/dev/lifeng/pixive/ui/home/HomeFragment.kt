package dev.lifeng.pixive.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import dev.lifeng.pixive.data.repo.repo
import dev.lifeng.pixive.databinding.FragmentHomeBinding
import dev.lifeng.pixive.infra.extension.collectIn
import dev.lifeng.pixive.infra.extension.withTimeoutAndCatch
import kotlinx.coroutines.launch

class HomeFragment: Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    private lateinit var adapter : PixivIllustAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding!!.root
        adapter = PixivIllustAdapter(this.requireContext())
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("HomeFragment", "savedInstance is $savedInstanceState")
        val navigationBar = requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)
        navigationBar.visibility = View.VISIBLE
        val recommendArtistsLayout = binding!!.recmomendArtists.recommendArtistsLayout
        recommendArtistsLayout.setOnClickListener{
            findNavController().navigate(R.id.action_from_home_to_recommend_artists)
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        // Postpone the transition until the RecyclerView has finished laying out and drawing the correct position
        postponeEnterTransition()
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        // Set up the adapter and postpone the enter transition
        recyclerView.adapter = adapter

        /** wired workaround **/
        recyclerView.viewTreeObserver.addOnScrollChangedListener(
            object : ViewTreeObserver.OnScrollChangedListener {
                override fun onScrollChanged() {
                    Log.d("TEST","chanfedddddd")
                    // Ensure we remove the listener after it triggers once
                    recyclerView.viewTreeObserver.removeOnScrollChangedListener(this)

                    // Start the postponed enter transition
                    startPostponedEnterTransition()

                    // Return true to continue with the draw
                   //return true
                }
            }
        )
        addScollListenerForrecyclerView(recyclerView,navigationBar)
        lifecycleScope.launch {
            loadData(viewModel, recommendArtistsLayout,savedInstanceState)
        }
        addLoadStateForAdapter(adapter)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    //添加adapter的加载状态监听
    private fun addLoadStateForAdapter(adapter: PixivIllustAdapter) {
        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    //progressBar.visibility = View.INVISIBLE
                }
                is LoadState.Loading -> {
                    //progressBar.visibility = View.VISIBLE
                }
                is LoadState.Error -> {
                    val state = it.refresh as LoadState.Error
                    //progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this.context, "Load Illusts Error: ${state.error.message}", Toast.LENGTH_SHORT).show()
                    Log.d("SubFragment", "Load Illusts Error: ${state.error.message}")
                }
            }
        }
    }
    //加载数据
    private suspend fun loadData(viewModel: HomeViewModel, recommendArtistsLayout: LinearLayout,savedInstanceState: Bundle?) {

        withTimeoutAndCatch(7000,
            block = {
            PixiveApplication.TOKEN = "Bearer " + repo.auth().getOrThrow()
        },  onErrorAction = {
            showErrorMsg("获取Token时网络错误")
        })
        Log.d("HomeFragment", "Token is ${PixiveApplication.TOKEN}")
        if (!viewModel.back) {
            viewModel.updateRecommendArtists()
        }
        Log.d("HomeFragment", "Update Recommend Artists")
        viewModel.recommendArtistsFlow.collectIn(viewLifecycleOwner) {
            if (it.nextUrl ==  "networkError") {
                showErrorMsg("加载用户头像时网络错误")
            } else {
                addRecommendArtist(it, recommendArtistsLayout)
            }
        }
        viewModel.spotLightsFlow.collectIn(viewLifecycleOwner) {
            if (it.articles.isEmpty()) {
                showErrorMsg("加载特辑时网络错误")
            } else {
                Log.d("HomeFragment", "Spotlights: ${it.articles}")
                addCardView(it, binding!!.highlightLayout)
            }
        }
        viewModel.getPixivIllustPagingData.collectIn(viewLifecycleOwner) {
            adapter.submitData(it)
        }

    }

    private fun addScollListenerForrecyclerView(recyclerView:RecyclerView,navigationBar:BottomNavigationView){
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when(dy > 0){
                    true ->{
//                        navigationBar.visibility = View.GONE
                        navigationBar.animate()
                            .translationY(navigationBar.height.toFloat()) // 向下移动到底部，隐藏
                            .setDuration(200) // 设置动画持续时间
                            .start()
                    }
                    false ->{
//                        navigationBar.visibility = View.VISIBLE
                        navigationBar.animate()
                            .translationY(0f) // 回到原位置，显示
                            .setDuration(200) // 设置动画持续时间
                            .start()
                    }
                }
            }
        })
    }

    private fun showErrorMsg(msg: String){
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    }
    private fun showErrorMsg(msg: String,errorMsg: String){
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
        Log.d("HomeFragment","errorMsg: $errorMsg")
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun addCardView(response: PixivSpotlightResponse, linearLayoutManager: LinearLayout) {
        response.articles.forEach { article ->
            if (article.thumbnail == "") {
                return@forEach
            }
            val cardView = LayoutInflater.from(this@HomeFragment.context).inflate(R.layout.card_item, linearLayoutManager, false) as CardView
            val textView = cardView.findViewById<TextView>(R.id.spotlight_title)
            val imageView = cardView.findViewById<ImageView>(R.id.image)
            imageView.load(article.thumbnail){
                diskCachePolicy(CachePolicy.ENABLED)
                crossfade(800)
                placeholder(R.drawable.white_background)
                addHeader("Referer", "https://www.pixiv.net/")
            }
            textView.text = article.title
            var downTime:Long = 0
            val CLICK_THRESHOLD = 200
            cardView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downTime = SystemClock.elapsedRealtime()
                        v.animate().translationZ(20f).duration = 150
                        v.animate().rotationY(5f).setDuration(500)
                    }
                    MotionEvent.ACTION_UP -> {
                        v.animate().translationZ(0f).duration = 150
                        v.animate().rotationY(0f).setDuration(500)
                        val uptime = SystemClock.elapsedRealtime()
                        val duration = uptime - downTime
                        if (duration < CLICK_THRESHOLD){
                            val bundle = Bundle()
                            bundle.putString("url",article.articleUrl)
                            findNavController().navigate(R.id.action_from_home_to_webview,bundle)
                        }
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        v.animate().translationZ(0f).duration = 150
                        v.animate().rotationY(0f).setDuration(500)
                    }
                }
                return@setOnTouchListener true
            }
            cardView.translationX = 500f  // 初始位置在屏幕外
            cardView.animate().translationX(0f).setDuration(1000).start()  // 向屏幕内滑动
            linearLayoutManager.addView(cardView)
        }
    }

    private fun addRecommendArtist(response: PixivRecommendArtistsResponse, layoutManager: LinearLayout) {
        response.userPreviews.forEach { userPreview ->
            //Log.d("HomeFragment", "userPreview: ${userPreview.user.id} ${userPreview.user.profileImageUrls.medium}")
            val cardView = LayoutInflater.from(this@HomeFragment.context).inflate(R.layout.recommend_artist_item, layoutManager, false) as CardView
            val imageView = cardView.findViewById<ImageView>(R.id.artist_image)
            imageView.setBackgroundColor(0xFFFFF7FF.toInt())
            imageView.load(userPreview.user.profileImageUrls.medium){
                addHeader("Referer", "https://www.pixiv.net/")
                crossfade(800)
                transformations(CircleCropTransformation())
            }
            layoutManager.addView(cardView)
        }
    }
}