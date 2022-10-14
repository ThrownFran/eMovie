package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.core.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshMoviesUseCase @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MovieRepository,
) {

    suspend operator fun invoke(
        category: Category,
        invalidateCache: Boolean = true,
    ): Result = withContext(schedulers.ioDispatcher()) {
        repository.refreshMovies(category,invalidateCache)
    }

    sealed interface Result {
        object HasMorePagesToLoad: Result
        object IsFirstAndLastPage: Result
    }

}

