package brillembourg.parser.emovie.presentation

import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.data.network.IMAGE_PATH
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun ImageView.setMovieDbImageUrl (url : String) {
    Glide
        .with(context)
        .load(IMAGE_PATH+url)
        .centerCrop()
//        .placeholder(R.drawable.loading_spinner)
        .into(this);
}

fun ComponentActivity.safeUiLaunch(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke(this)
        }
    }
}

fun Fragment.safeUiLaunch(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke(this)
        }
    }
}