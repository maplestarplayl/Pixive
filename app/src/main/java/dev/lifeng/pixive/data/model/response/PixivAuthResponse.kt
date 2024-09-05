package dev.lifeng.pixive.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PixivAuthResponse(
    val accessToken: String
)