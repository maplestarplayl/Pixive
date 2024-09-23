package dev.lifeng.pixive.ui.common.illustration

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import coil.load
import coil.transform.CircleCropTransformation
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.databinding.FragmentIllustDetailBinding


class IllustDetailFragment: Fragment() {
    private var binding: FragmentIllustDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        binding = FragmentIllustDetailBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        val imageView = binding!!.illustDetailImage
        val illust = arguments?.getParcelable("illust_detail", PixivRecommendIllusts.Illust::class.java)
        ViewCompat.setTransitionName(imageView,"shared_image${illust!!.id}")
        imageView.load(illust.imageUrls.large){
            //stretch the image's height when it spans the whole screen width
            target(onSuccess = {result ->
                imageView.setImageDrawable(result)
                startPostponedEnterTransition()
                Log.d("TEST","load success")
            },     onError = {
                //startPostponedEnterTransition()
                Log.d("TEST","load failed")
            })
            val screenWidth = requireContext().resources.displayMetrics.widthPixels
            val imageWidth = screenWidth
            val imageHeight = (imageWidth.toFloat() / illust?.width?.toFloat()!! * illust.height.toFloat()).toInt()
            val layoutParams = imageView.layoutParams
            layoutParams.width = imageWidth
            layoutParams.height = imageHeight
            addHeader("Referer", "https://www.pixiv.net/")
        }
        val titleText = binding!!.illustDetailTitle
        val viewsText = binding!!.views
        val starsText = binding!!.stars
        val dateText  = binding!!.date
        titleText.text = illust.title
        viewsText.text = illust.totalView.toString()
        starsText.text = illust.totalBookmarks.toString()
        dateText.text = illust.createDate
        val artistImage = binding!!.illustDetailArtistImage
        val artistName  = binding!!.illustDetailArtistName
        artistImage.load(illust.user.profileImageUrls.medium){
            addHeader("Referer", "https://www.pixiv.net/")
            crossfade(800)
            transformations(CircleCropTransformation())
        }
        artistName.text = illust.user.name
    }
}