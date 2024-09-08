package dev.lifeng.pixive.data.model.savedData

import kotlinx.serialization.Serializable

@Serializable
data class UserSetting(val refreshToken: String,
                       val homeIllustSpan: Int )