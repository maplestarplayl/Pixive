package dev.lifeng.pixive.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.IDN

@Serializable
data class PixivSpotlightResponse(@SerialName("spotlight_articles")val articles: List<SpotArticle>){

}
@Serializable
data class SpotArticle(val id: Int, val title: String, val pureTitle: String, val thumbnail: String )