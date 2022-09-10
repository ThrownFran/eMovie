package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshMovieDetailUseCase @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MovieRepository
) {

    suspend operator fun invoke(movieId: Long) = withContext(schedulers.ioDispatcher()) {
        repository.refreshMovieDetail(movieId)
    }

}

