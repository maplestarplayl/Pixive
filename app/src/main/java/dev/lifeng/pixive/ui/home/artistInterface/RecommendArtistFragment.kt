package dev.lifeng.pixive.ui.home.artistInterface

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.Disposable
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.infra.extension.collectIn
import dev.lifeng.pixive.ui.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class RecommendArtistFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.back = true
        val recommendArtistsLayout = view.findViewById<LinearLayout>(R.id.artist_list_layout)
        viewModel.recommendArtistsFlow.collectIn(viewLifecycleOwner) {
            if (it.userPreviews.isNotEmpty()) {
                Log.d("RecommendArtistFragment", "addArtistsView ${it.userPreviews.size}")
                addArtistsView(it, recommendArtistsLayout)
            }else{
                showErrorMsg("加载用户头像时网络错误", it.nextUrl)
            }
        }
        val scrollView = view.findViewById<ScrollView>(R.id.artist_list_scroll_view)
        scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollY))
            if (diff == 0) {
                // 滚动到底部时执行的操作
                Log.d("ScrollView", "Scrolled to bottom")
                lifecycleScope.launch { viewModel.updateRecommendArtists() }
            }
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    private suspend fun addArtistsView(response: PixivRecommendArtistsResponse, recommendArtistsLayout: LinearLayout) {
        response.userPreviews.forEach {
            val cardView = LayoutInflater.from(this@RecommendArtistFragment.context).inflate(
                R.layout.recommend_artist_list_item,
                recommendArtistsLayout,
                false
            ) as CardView
            cardView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN-> {
                        v.animate().translationZ(20f).duration = 150
                        v.animate().rotationY(3f).setDuration(500)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.animate().translationZ(0f).duration = 150
                        v.animate().rotationY(0f).setDuration(500)
                    }
                }
                return@setOnTouchListener true
            }
            val imageView1 = cardView.findViewById<ImageView>(R.id.artist_image1)
            val imageView2 = cardView.findViewById<ImageView>(R.id.artist_image2)
            val imageView3 = cardView.findViewById<ImageView>(R.id.artist_image3)
            val textView = cardView.findViewById<TextView>(R.id.artist_name)
            val artistImageView = cardView.findViewById<ImageView>(R.id.artist_profile_image)
            textView.text = it.user.name
            artistImageView.load(it.user.profileImageUrls.medium) {
                transformations(CircleCropTransformation())
            }
            val res1 = imageView1.loadCard(it.illusts[0].imageUrls.medium)
            val res2 = imageView2.loadCard(it.illusts[1].imageUrls.medium)
            val res3 = imageView3.loadCard(it.illusts[2].imageUrls.medium)
            recommendArtistsLayout.addView(cardView)
            withTimeoutContinue(1000) {
                res1.job.await()
                res2.job.await()
                res3.job.await()
            }
        }
    }
    private fun showErrorMsg(msg: String){
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    }
    private fun showErrorMsg(msg: String,errorMsg: String){
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
        Log.d("HomeFragment","errorMsg: $errorMsg")
    }
    private fun ImageView.loadCard(url: String): Disposable{
        return this.load(url){
            crossfade(800)
            //size(350,350)
            addHeader("Referer", "https://www.pixiv.net/")
            placeholder(R.drawable.card_pic_placeholder)
            transformations(RoundedCornersTransformation(20f))
        }
    }
    private suspend fun <T> withTimeoutContinue(timeMillis:Long, block:  suspend CoroutineScope.() -> T){
        try {
            withTimeout(timeMillis) { block() }
        }catch (e: TimeoutCancellationException){
            return
        }
    }
}


