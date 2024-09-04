package dev.lifeng.pixive.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrls(
    val squareMedium: String,
    val medium: String,
    val large: String
)
@Serializable
data class ProfileImageUrls(
    val medium: String
)