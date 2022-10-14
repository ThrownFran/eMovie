package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.models.Category
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.core.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RequestNextMoviePageUseCase @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MovieRepository
) {

    suspend operator fun invoke(category: Category, lastItemVisible: Int): Result = withContext(schedulers.ioDispatcher()) {
        repository.requestNextMoviePage(category,lastItemVisible)
    }

    sealed interface Result {
        object RequestSuccess: Result
        object LastPageAlreadyReached: Result
        object LastItemInPageNotReachedYet: Result
    }

}

