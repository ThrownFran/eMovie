package brillembourg.parser.emovie.presentation.utils

import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import brillembourg.parser.emovie.R
import com.google.android.material.transition.MaterialSharedAxis

fun Fragment.prepareTransition(rootView: View) {
    postponeEnterTransition()
    rootView.doOnPreDraw { startPostponedEnterTransition() }
}

fun Fragment.setOriginSharedAxisTransition() {
    exitTransition = MaterialSharedAxis(
        MaterialSharedAxis.Z,
        true
    ).apply {
        duration = resources.getInteger(R.integer.motion_duration_large).toLong()
    }
    reenterTransition = MaterialSharedAxis(
        MaterialSharedAxis.Z,
        false
    ).apply {
        duration = resources.getInteger(R.integer.motion_duration_large).toLong()
    }
}

fun Fragment.setDestinationSharedAxisTransition() {
    enterTransition = MaterialSharedAxis(
        MaterialSharedAxis.Z,
        true
    ).apply {
        duration = resources.getInteger(R.integer.motion_duration_large).toLong()
    }
    returnTransition = MaterialSharedAxis(
        MaterialSharedAxis.Z,
        false
    ).apply {
        duration = resources.getInteger(R.integer.motion_duration_large).toLong()
    }
}




