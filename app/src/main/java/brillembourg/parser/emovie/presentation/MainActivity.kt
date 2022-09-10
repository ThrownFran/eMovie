package brillembourg.parser.emovie.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import brillembourg.parser.emovie.R
import brillembourg.parser.emovie.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        forceDarkMode()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.mainToolbar)
//        val navController = findMainNavController()
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            binding.mainToolbar.isVisible = destination.id != R.id.DetailFragment
//
////            if(destination.id == R.id.DetailFragment) {
////                lifecycleScope.launch {
////                    delay(200)
////                    binding.mainToolbar.isVisible = false
////                }
////            } else {
////                binding.mainToolbar.isVisible = true
////            }
//        }
    }

    private fun forceDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun findMainNavController() =
        (supportFragmentManager.findFragmentById(R.id.navhost_fragment_container_view) as NavHostFragment).navController

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navhost_fragment_container_view)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }



}