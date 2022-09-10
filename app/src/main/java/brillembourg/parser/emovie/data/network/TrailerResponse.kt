package brillembourg.parser.emovie.data.network

import com.google.gson.annotations.SerializedName

class TrailerResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("site")
    val site: String,
)

class GetTrailersResponse {
    @SerializedName("results")
    var results: List<TrailerResponse> = listOf()
}