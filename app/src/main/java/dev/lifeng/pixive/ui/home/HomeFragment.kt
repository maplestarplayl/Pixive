package dev.lifeng.pixive.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
    private val adapter = PixivIllustAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recommendArtistsLayout = binding!!.recmomendArtists.recommendArtistsLayout
        recommendArtistsLayout.setOnClickListener(View.OnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecommendArtistFragment())
                .addToBackStack(null)
                .commit()
        })
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter
        lifecycleScope.launch {
            loadData(viewModel, recommendArtistsLayout)
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
    private suspend fun loadData(viewModel: HomeViewModel, recommendArtistsLayout: LinearLayout) {
        PixiveApplication.TOKEN = "Bearer " + repo.auth().getOrNull()
        viewModel.recommendArtistsFlow.collectIn(viewLifecycleOwner) {
            if (it.userPreviews.isEmpty()) {
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
    private fun addCardView(response: PixivSpotlightResponse, linearLayoutManager: LinearLayout) {
        response.articles.forEach { article ->
            val cardView = LayoutInflater.from(this@HomeFragment.context).inflate(R.layout.card_item, linearLayoutManager, false) as CardView
            val textView = cardView.findViewById<TextView>(R.id.spotlight_title)
            val imageView = cardView.findViewById<ImageView>(R.id.image)
            imageView.load(article.thumbnail){
                addHeader("Referer", "https://www.pixiv.net/")
            }
            textView.text = article.title
            Log.d("HomeFragment", "article: ${article.id}")
            linearLayoutManager.addView(cardView)
        }
    }

    private fun addRecommendArtist(response: PixivRecommendArtistsResponse, layoutManager: LinearLayout) {
        response.userPreviews.forEach { userPreview ->
            //Log.d("HomeFragment", "userPreview: ${userPreview.user.id} ${userPreview.user.profileImageUrls.medium}")
            val cardView = LayoutInflater.from(this@HomeFragment.context).inflate(R.layout.recommend_artist_item, layoutManager, false) as CardView
            val imageView = cardView.findViewById<ImageView>(R.id.artist_image)
            imageView.load(userPreview.user.profileImageUrls.medium){
                addHeader("Referer", "https://www.pixiv.net/")
                transformations(CircleCropTransformation())
            }
            layoutManager.addView(cardView)
            //Log.d("HomeFragment","addRecommendArtist")
        }
    }
}