package brillembourg.parser.emovie.domain

sealed class Category(
    val key: String
) {
    class Upcoming : Category("upcoming")
    class TopRated : Category("top_rated")

//    class Recommended(
//        language: String,
//        releaseYear: String
//    ) : Category("recommended_{$language}_{$releaseYear}")
}



