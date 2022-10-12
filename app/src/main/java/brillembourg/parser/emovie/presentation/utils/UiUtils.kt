package brillembourg.parser.emovie.presentation.utils

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch

fun Fragment.safeUiLaunch(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke(this)
        }
    }
}

fun showMessage(
    coordinator: CoordinatorLayout,
    message: String,
    onMessageShown: (() -> Unit)? = null,
) {
    Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG).apply {

        addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                super.onShown(transientBottomBar)
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                onMessageShown?.invoke()
            }
        })
        show()
    }
}

@ExperimentalCoroutinesApi
val RecyclerView.lastVisibleEvents: Flow<Int>
    get() = callbackFlow<Int> {
        val lm = layoutManager as LinearLayoutManager

        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val visibleItemCount = lm.childCount
                val firstVisibleItem: Int = lm.findFirstVisibleItemPosition()
                val totalItemCount = lm.itemCount

                if (dx > 0) {
                    if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                        //the list is in the end. Fetch new data.
                        trySend(lm.findLastVisibleItemPosition()).isSuccess
                    }
                }

            }
        }
        addOnScrollListener(listener)
        awaitClose { removeOnScrollListener(listener) }
    }.conflate()


