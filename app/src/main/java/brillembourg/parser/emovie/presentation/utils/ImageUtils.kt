package brillembourg.parser.emovie.presentation.utils

import android.widget.ImageView
import brillembourg.parser.emovie.data.network.IMAGE_PATH
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun ImageView.setMovieDbImageUrl(url: String, imageType: ImageType) {
    val size = when(imageType) {
        ImageType.Backdrop -> getBackdropSize()
        ImageType.Poster -> getPosterSize()
    }
    Glide
        .with(context)
        .load(IMAGE_PATH + url.replace("original",size))
        .transition(DrawableTransitionOptions.withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .centerCrop()
//        .placeholder(R.drawable.loading_spinner)
        .into(this);
}

enum class ImageType {
    Backdrop, Poster
}

fun ImageView.getPosterSize() : String {
    return if (width <= 92) {
        "w92"
    } else if (width <= 154) {
        "w154"
    } else if (width <= 185) {
        "w185"
    } else if (width <= 342) {
        "w342"
    } else if (width <= 500) {
        "w500"
    } else {
        "w780"
    }
}

fun ImageView.getBackdropSize(): String {
    return if (width <= 300) {
        "w300"
    } else if (width <= 780) {
        "w780"
    } else {
        "w1280"
    }
}