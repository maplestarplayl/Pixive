package dev.lifeng.pixive.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PixivSpotlightResponse(@SerialName("spotlight_articles")val articles: List<SpotArticle>){

}
@Serializable
data class SpotArticle(val id: Int,
                       val title: String,
                       val pureTitle: String,
                       val thumbnail: String,
                       val articleUrl: String,
                       val publishDate: String,
                       val category: String,
                       @SerialName("subcategory_label")val subCategory: String)

fun SpotArticle(): SpotArticle {
    return SpotArticle(0,"","","","","","","")
}
