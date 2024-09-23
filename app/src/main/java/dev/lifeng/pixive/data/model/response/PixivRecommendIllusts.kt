package dev.lifeng.pixive.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivRecommendIllusts(
    val illusts: List<Illust>
) {
    @Serializable
    @Parcelize
    data class Illust(
        val id: Int,
        val title: String,
        val type: String,
        val imageUrls: ImageUrls,
        val caption: String,
        val user: User,
        val tags: List<Tag>,
        val createDate: String,
        val pageCount: Int,
        val totalView: Int,
        val totalBookmarks: Int,
        val isBookmarked: Boolean,
        val width: Int,
        val height: Int,
        @SerialName("meta_single_page")val metaSinglePage: MetaSinglePage
    ) : Parcelable
    @Parcelize
    @Serializable
    data class User(
        val id: Int,
        val name: String,
        val account: String,
        val profileImageUrls: ProfileImageUrls,
        val isFollowed: Boolean
    ): Parcelable
    @Serializable
    @Parcelize
    data class MetaSinglePage(
        @SerialName("original_image_url")val originalImageUrl: String? = null
    ): Parcelable
    @Serializable
    @Parcelize
    data class Tag(
        val name: String,
        val translatedName: String?,

    ) : Parcelable
}