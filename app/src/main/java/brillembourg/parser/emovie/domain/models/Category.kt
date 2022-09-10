package brillembourg.parser.emovie.domain.models

sealed class Category(
    val key: String
) {
    class Upcoming : Category("upcoming")
    class TopRated : Category("top_rated")
}



