package dev.lifeng.pixive.ui.home

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import dev.lifeng.pixive.R
import dev.lifeng.pixive.data.model.response.PixivRecommendIllusts


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
        //val title: TextView = itemView.findViewById(R.id.illust_title)
        //val artistName: TextView = itemView.findViewById(R.id.illust_author)
        //val isFollowed: TextView = itemView.findViewById(R.id.)
        val image : ImageView = itemView.findViewById(R.id.illust_image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.illust_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = holder.image.layoutParams
        val illust = getItem(position)
        if (illust != null) {
            //holder.title.text = illust.title
            //holder.artistName.text = illust.user.name
            //holder.text = illust.totalBookMarks.toString()
            val imageView = holder.image
            imageView.load(illust.imageUrls.medium){
                transformations(RoundedCornersTransformation(40f))
                addHeader("Referer", "https://www.pixiv.net/")
            }
            imageView.setOnClickListener(View.OnClickListener {
                //val intent = Intent(parent.context, IllustActivity::class.java).apply {
                //    putExtra("illust_id", illust.id)
                //}
                //parent.context.startActivity(intent)
                Log.d("PixivIllustAdapter", "click on illust: ${illust.id}")
            })
        }
    }

}

class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // 为每个子项设置偏移量（边距）
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        // 如果是第一个项目，也设置顶部间距
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space
        }
    }
}
