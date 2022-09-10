package brillembourg.parser.emovie.domain.use_cases

import brillembourg.parser.emovie.domain.Category
import brillembourg.parser.emovie.domain.MovieRepository
import brillembourg.parser.emovie.domain.Schedulers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshMovieDetailUseCase @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MovieRepository
) {

    suspend operator fun invoke(category: Category) = withContext(schedulers.ioDispatcher()) {
        repository.refreshData(category)
    }

}

