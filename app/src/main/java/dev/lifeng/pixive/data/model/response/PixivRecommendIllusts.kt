package dev.lifeng.pixive.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PixivRecommendIllusts(
    val illusts: List<Illust>
) {
    @Serializable
    data class Illust(
        val id: Int,
        val title: String,
        val type: String,
        val imageUrls: ImageUrls,
        val caption: String,
        val user: User,
        //val tags: List<Tag>,
        val totalView: Int,
        val totalBookmarks: Int,
        val isBookmarked: Boolean,
        val width: Int,
        val height: Int,
    )
    @Serializable
    data class User(
        val id: Int,
        val name: String,
        val account: String,
        val profileImageUrls: ProfileImageUrls,
        val isFollowed: Boolean
    )
}