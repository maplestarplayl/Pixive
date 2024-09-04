package dev.lifeng.pixive.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PixivRecommendArtistsResponse(
    val userPreviews: List<UserPreview>,
    val nextUrl: String
){
@Serializable
data class UserPreview(
    val user: User,
    val illusts: List<Illust>,
    val isMuted: Boolean
)
@Serializable
data class User(
    val id: Int,
    val name: String,
    val account: String,
    val profileImageUrls: ProfileImageUrls,
    val isFollowed: Boolean
)
@Serializable
data class Illust(
    val id: Int,
    val title: String,
    val type: String,
    val imageUrls: ImageUrls,
    val caption: String,
)
}