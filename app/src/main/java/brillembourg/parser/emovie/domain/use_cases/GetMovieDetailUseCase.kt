package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.models.Movie
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import brillembourg.parser.emovie.domain.models.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MovieRepository
) {

    operator fun invoke(id: Long): Flow<MovieDetail> {
        return repository.getMovie(id)
            .flowOn(schedulers.ioDispatcher())
    }

}

