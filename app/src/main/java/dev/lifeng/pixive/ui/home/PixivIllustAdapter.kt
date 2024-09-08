package dev.lifeng.pixive.ui.home

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.lifeng.pixive.PixiveApplication
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts
import dev.lifeng.pixive.infra.extension.CustomRoundedCornersTransformation
import dev.lifeng.pixive.infra.extension.saveImage
import kotlinx.coroutines.launch


class PixivIllustAdapter : PagingDataAdapter<PixivRecommendIllusts.Illust, PixivIllustAdapter.ViewHolder>(COMPARATOR) {

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
        val illust = getItem(position)
        Log.d("PixivIllustAdapter", "onBindViewHolder: $illust")
        if (illust != null) {
            holder.title.text = illust.title
            holder.artistName.text = illust.user.name
            holder.heart.setOnClickListener {
                holder.heart.setImageResource(R.drawable.favorite_filled_red)
            }
            if (illust.isBookmarked){
                holder.heart.setImageResource(R.drawable.favorite_filled_red)
            }
            val imageView = holder.image

            imageView.load(illust.imageUrls.medium){
                transformations(CustomRoundedCornersTransformation(40f,40f,0f,0f))
                addHeader("Referer", "https://www.pixiv.net/")
            }
            imageView.setOnClickListener {
                Log.d("PixivIllustAdapter", "click on illust: ${illust.id}")
                it.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    illust.metaSinglePage.originalImageUrl?.let {
                        saveImage(PixiveApplication.context, it, illust.title)
                        ""
                    } ?: run { Log.d("PixivIllustAdapter", "originalImageUrl is null") }
                }
            }
        }
    }

}
