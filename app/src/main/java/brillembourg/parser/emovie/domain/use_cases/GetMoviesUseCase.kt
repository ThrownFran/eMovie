package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.Movie
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MovieRepository
) {

    operator fun invoke(category: Category): Flow<List<Movie>> {
        return repository.getMovies(category)
            .flowOn(schedulers.ioDispatcher())
    }

}

