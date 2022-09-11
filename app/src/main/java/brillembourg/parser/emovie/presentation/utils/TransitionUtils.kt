package brillembourg.parser.emovie.presentation.utils

import android.graphics.Color
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.presentation.detail.DetailFragment
import brillembourg.parser.emovie.presentation.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale

fun Fragment.prepareTransition(rootView: View) {
    postponeEnterTransition()
    rootView.doOnPreDraw { startPostponedEnterTransition() }
}


fun Fragment.setTransitionToMovie() {
    exitTransition = MaterialElevationScale(false).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
    }
    reenterTransition = MaterialElevationScale(true).apply {
        duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
    }
}

fun setupExtrasToDetail(view: View?): FragmentNavigator.Extras {
    if(view == null) return FragmentNavigatorExtras()

    return FragmentNavigatorExtras(
        //View from item list
        view
                //String mapping detail view (transition_name)
                to view.context.getString(R.string.home_shared_detail_container),
    )
}

fun DetailFragment.setMovieEnteringTransition() {
    sharedElementEnterTransition = MaterialContainerTransform().apply {
        drawingViewId = R.id.navhost_fragment_container_view
        duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
//        scrimColor = Color.TRANSPARENT
//        setAllContainerColors(requireContext().themeColor(com.google.android.material.R.attr.colorSurface))
    }
}



