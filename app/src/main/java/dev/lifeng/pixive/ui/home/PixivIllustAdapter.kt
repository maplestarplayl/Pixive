package dev.lifeng.pixive.ui.home

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.data.repo.repo
import dev.lifeng.pixive.infra.extension.CustomRoundedCornersTransformation
import dev.lifeng.pixive.infra.extension.saveImage
import dev.lifeng.pixive.infra.extension.tryAndCatchChannelClosed
import dev.lifeng.pixive.ui.home.views.ProgressBar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PixivIllustAdapter(val context: Context) : PagingDataAdapter<PixivRecommendIllusts.Illust, PixivIllustAdapter.ViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<PixivRecommendIllusts.Illust>() {
            override fun areItemsTheSame(oldItem: PixivRecommendIllusts.Illust, newItem: PixivRecommendIllusts.Illust): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: PixivRecommendIllusts.Illust, newItem: PixivRecommendIllusts.Illust): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.illust_title_text)
        val artistName: TextView = itemView.findViewById(R.id.illust_artist_name)
        val heart: ImageButton = itemView.findViewById(R.id.favorite_button)
        val image : ImageView = itemView.findViewById(R.id.illust_image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.illust_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val progressChannel = Channel<Int>()
        val illust = getItem(position)
        if (illust != null) {
            holder.title.text = illust.title
            holder.artistName.text = illust.user.name

            addClickListenerForHeart(holder.heart,illust.id)
            if (illust.isBookmarked){
                holder.heart.setImageResource(R.drawable.favorite_empty)
                holder.heart.tag = "Favorite"
            }

            val imageView = holder.image
            addClickListenerForImage(imageView,illust,progressChannel)
            imageView.load(illust.imageUrls.medium){
                transformations(CustomRoundedCornersTransformation(40f,40f,0f,0f))
                addHeader("Referer", "https://www.pixiv.net/")
                crossfade(800)
            }
        }
    }



    //Mainly used for addBookMark and delete Bookmark
    private fun addClickListenerForHeart(heartButton: ImageButton,illustId: Int){
        heartButton.setOnClickListener {
            when (heartButton.tag){
                "notFavorite" -> {
                    val increaseHeartColorAnime = ContextCompat.getDrawable(PixiveApplication.context,R.drawable.heart_color_animation) as AnimatedVectorDrawable
                    heartButton.setImageDrawable(increaseHeartColorAnime)
                    increaseHeartColorAnime.start()
                    heartButton.tag = "Favorite"
                    //heartButton.setImageResource(R.drawable.favorite_filled)
                    it.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                        Log.d("PixivIllustAdapter", "ready to add bookmark: $illustId")
                        repo.addBookMark(illustId)
                    }
                }
                "Favorite" -> {
                    heartButton.tag = "notFavorite"
                    val decreaseHeartColorAnime = ContextCompat.getDrawable(PixiveApplication.context,R.drawable.heart_decrease_animation) as AnimatedVectorDrawable
                    heartButton.setImageDrawable(decreaseHeartColorAnime)
                    decreaseHeartColorAnime.start()
                    //heartButton.setImageResource(R.drawable.favorite_empty)
                    it.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                        Log.d("PixivIllustAdapter", "ready to delete bookmark: $illustId")
                        repo.deleteBookMark(illustId)
                    }
                }
            }
        }
    }
    //Mainly used for download image
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addClickListenerForImage(imageView: ImageView, illust: PixivRecommendIllusts.Illust, progressChannel: Channel<Int>){
        imageView.setOnClickListener {
            val testChannel = Channel<Int>()
            Log.d("PixivIllustAdapter", "click on illust: ${illust.id}")
            it.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {

                illust.metaSinglePage.originalImageUrl?.let {
                    launch {
                        repeat(100){
                            testChannel.send(it)
                            delay(200)
                        }
                    }
                    launch{ saveImage(PixiveApplication.context, it, illust.title, progressChannel,
                        onSuccess = {
                            delay(1000)
                            Toast.makeText(PixiveApplication.context,"Download Success",Toast.LENGTH_SHORT).show()
                                    },
                        onFailure = {Toast.makeText(PixiveApplication.context,"Download Failed",Toast.LENGTH_SHORT).show()}) }
                    launch{
                        val progressBar = ProgressBar(PixiveApplication.context)
                        addProgressBar(progressBar, context = this@PixivIllustAdapter.context)
                        //receive progress from channel
                        tryAndCatchChannelClosed(block = {
                            for (progress in progressChannel) {
                                //Log.d("PixivIllustAdapter", "progress is : $progress")
                                withContext(Dispatchers.Main) {
                                    progressBar.setProgress(progress)
                                }
                            }
                        })
                        //after channel is closed, hide progress bar
                        withContext(Dispatchers.Main){
                            progressBar.setProgress(100)
                            delay(700)
                            progressBar.visibility = View.GONE
                            progressBar.setProgress(0)
                        }
                    }
                    ""
                } ?: run {
                    Toast.makeText(PixiveApplication.context, "原图链接为空", Toast.LENGTH_SHORT).show()
                    Log.d("PixivIllustAdapter", "originalImageUrl is null") }
            }
        }
    }
    private fun addProgressBar(progressBar: ProgressBar,context: Context){
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = android.view.Gravity.BOTTOM
        progressBar.alpha = 0f
        windowManager.addView(progressBar, layoutParams)
        val fadeIn = ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f)
        fadeIn.duration = 500
        fadeIn.start()
    }

}
