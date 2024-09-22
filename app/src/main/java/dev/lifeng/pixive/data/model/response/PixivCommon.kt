package dev.lifeng.pixive.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ImageUrls(
    val squareMedium: String,
    val medium: String,
    val large: String
) : Parcelable

@Serializable
@Parcelize
data class ProfileImageUrls(
    val medium: String
): Parcelable