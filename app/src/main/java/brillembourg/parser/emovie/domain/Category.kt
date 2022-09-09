package brillembourg.parser.emovie.domain

sealed class Category() {
    class Upcoming() : Category()
    class TopRated() : Category()
}