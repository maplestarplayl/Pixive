package dev.lifeng.pixive.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.data.model.response.PixivSpotlightResponse
import dev.lifeng.pixive.data.repo.repo
import dev.lifeng.pixive.databinding.FragmentHomeBinding
import dev.lifeng.pixive.infra.extension.collectIn
import dev.lifeng.pixive.ui.home.artistInterface.RecommendArtistFragment
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
//        lifecycleScope.launch(Dispatchers.IO) {
//            for(it in 1 ..100) {
//                delay(100)
//                progressChannel.send(it)
//                Log.d("HomeFragment", "progressChannel send $it")
//            }
//        }
        Log.d("HomeFragment", "savedInstance is $savedInstanceState")
        val recommendArtistsLayout = binding!!.recmomendArtists.recommendArtistsLayout
//        val progressBar = binding!!.progressBar as ProgressBar
//        progressBar.setOnClickListener {
//            lifecycleScope.launch {
//                for (i in progressChannel) {
//                    progressBar.setProgress(i)
//                    Log.d("HomeFragment", "progressChannel consume $it")
//                }
//            }
//        }
        recommendArtistsLayout.setOnClickListener(View.OnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecommendArtistFragment())
                .addToBackStack(null)
                .commit()
        })
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter
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
        PixiveApplication.TOKEN = "Bearer " + repo.auth().getOrNull()
        Log.d("HomeFragment", "Token is ${PixiveApplication.TOKEN}")
        if (!viewModel.back) {
            viewModel.updateRecommendArtists()
        }
        Log.d("HomeFragment", "Update Recommend Artists")
        viewModel.recommendArtistsFlow.collectIn(viewLifecycleOwner) {
            if (it.nextUrl == "") {
                showErrorMsg("加载用户头像时网络错误", it.nextUrl)
            } else {
                addRecommendArtist(it, recommendArtistsLayout)
            }
        }
        viewModel.spotLightsFlow.collectIn(viewLifecycleOwner) {
            if (it.articles.isEmpty()) {
                showErrorMsg("加载特辑时网络错误")
            } else {
                addCardView(it, binding!!.highlightLayout)
            }
        }
        viewModel.getPixivIllustPagingData.collectIn(viewLifecycleOwner) {
            adapter.submitData(it)
        }

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
            cardView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.animate().translationZ(20f).duration = 150
                        v.animate().rotationY(5f).setDuration(500)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
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