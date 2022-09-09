package brillembourg.parser.emovie.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshMoviesUseCase @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MovieRepository
) {

    suspend operator fun invoke(category: Category) = withContext(schedulers.ioDispatcher()) {
        repository.refreshData(category)
    }

}

