package dev.lifeng.pixive.ui.common.illustration

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import coil.load
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts


class IllustDetailFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        return inflater.inflate(R.layout.fragment_illust_detail, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()

        val imageView = requireView().findViewById<ImageView>(R.id.illust_detail_image)
        val illust = arguments?.getParcelable("illust_detail", PixivRecommendIllusts.Illust::class.java)
        ViewCompat.setTransitionName(imageView,"shared_image${illust!!.id}")
        imageView.load(illust?.imageUrls?.large){
            //stretch the image's height when it spans the whole screen width
            target(onSuccess = {result ->
                imageView.setImageDrawable(result)
                startPostponedEnterTransition()
                Log.d("TEST","load success")

            }, onError = {
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
    }
}