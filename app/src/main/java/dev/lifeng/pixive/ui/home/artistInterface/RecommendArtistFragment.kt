package dev.lifeng.pixive.ui.home.artistInterface

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
import coil.load
import coil.transform.CircleCropTransformation
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendArtistsResponse
import dev.lifeng.pixive.infra.extension.collectIn
import dev.lifeng.pixive.ui.home.HomeViewModel

class RecommendArtistFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recommend_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView = view.findViewById<TextView>(R.id.test)
        textView.text = "RecommendArtistFragment"
        val recommendArtistsLayout = view.findViewById<LinearLayout>(R.id.artist_list_layout)
        viewModel.recommendArtistsFlow.collectIn(viewLifecycleOwner) {
            if (it.userPreviews.isNotEmpty()) {
                addArtistsView(it, recommendArtistsLayout)
            }else{
                showErrorMsg("加载用户头像时网络错误", it.nextUrl)
            }
        }
    }



    private fun addArtistsView(response: PixivRecommendArtistsResponse, recommendArtistsLayout: LinearLayout) {
        response.userPreviews.forEach {
            Log.d("RecommendArtistFragment", "addArtistsView: ${it.illusts}")
            val cardView = LayoutInflater.from(this@RecommendArtistFragment.context).inflate(R.layout.recommend_artist_list_item, recommendArtistsLayout, false) as CardView
            val imageView1 = cardView.findViewById<ImageView>(R.id.artist_image1)
            val imageView2 = cardView.findViewById<ImageView>(R.id.artist_image2)
            val imageView3 = cardView.findViewById<ImageView>(R.id.artist_image3)
            val textView = cardView.findViewById<TextView>(R.id.artist_name)
            val artistImageView = cardView.findViewById<ImageView>(R.id.artist_profile_image)
            textView.text = it.user.name
            artistImageView.load(it.user.profileImageUrls.medium) {
                transformations(CircleCropTransformation())
            }
            imageView1.load(it.illusts[0].imageUrls.squareMedium) {
                crossfade(true)
                size(400, 400)
                addHeader("Referer", "https://www.pixiv.net/")
            }
            imageView2.load(it.illusts[1].imageUrls.squareMedium) {
                crossfade(true)
                size(400, 400)
                addHeader("Referer", "https://www.pixiv.net/")
            }
            imageView3.load(it.illusts[2].imageUrls.squareMedium) {
                crossfade(true)
                size(400, 400)
                addHeader("Referer", "https://www.pixiv.net/")
            }
            recommendArtistsLayout.addView(cardView)
        }

    }
    private fun showErrorMsg(msg: String){
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    }
    private fun showErrorMsg(msg: String,errorMsg: String){
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
        Log.d("HomeFragment","errorMsg: $errorMsg")
    }
}


