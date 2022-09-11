package brillembourg.parser.emovie.presentation

import androidx.activity.ComponentActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun showMessage(coordinator: CoordinatorLayout ,message: String, onMessageShown: (() -> Unit)? = null) {
    Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT).apply {

        //Snackbar Widget still not styleable in Material3 (Could not style text color)
//        setTextColor(resolveAttribute(com.google.android.material.R.attr.colorOnSurface))
//        setBackgroundTint(resolveAttribute(com.google.android.material.R.attr.colorSecondaryContainer))

        addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                super.onShown(transientBottomBar)
//                binding.homeFab.shrink()
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
//                binding.homeFab.extend()
                onMessageShown?.invoke()
            }
        })
        show()
    }
}

fun Fragment.safeUiLaunch(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke(this)
        }
    }
}