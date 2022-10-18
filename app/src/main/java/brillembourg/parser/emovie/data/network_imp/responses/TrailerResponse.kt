package brillembourg.parser.emovie.data.network_imp.responses

import brillembourg.parser.emovie.domain.models.Trailer
import com.google.gson.annotations.SerializedName

class GetTrailersResponse {
    @SerializedName("results")
    var results: List<TrailerResponse> = listOf()
}

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

fun TrailerResponse.toDomain(movieId: Long): Trailer {
    return Trailer(
        id,
        key,
        name,
        site,
        movieId
    )
}

