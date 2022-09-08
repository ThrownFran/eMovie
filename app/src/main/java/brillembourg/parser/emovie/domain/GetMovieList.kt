package brillembourg.parser.emovie.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetMovieList(val schedulers: Schedulers, val repository: MovieRepository) {

    suspend operator fun invoke (category: Category): Flow<List<Movie>> {
        return repository.getMovies(category)
            .flowOn(schedulers.ioDispatcher())
    }

}

sealed class Category () {
    class Upcoming() : Category()
    class TopRated(): Category()
}