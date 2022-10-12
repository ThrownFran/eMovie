package brillembourg.parser.emovie.domain.models

sealed class Category(
    val key: String
) {
    object Upcoming : Category("upcoming")
    object TopRated : Category("top_rated")
}



