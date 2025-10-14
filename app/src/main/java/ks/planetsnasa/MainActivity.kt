package ks.planetsnasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ks.planetsnasa.data.di.ServiceLocator
import ks.planetsnasa.ui.detail.PlanetDetailScreen
import ks.planetsnasa.ui.list.PlanetListScreen
import ks.planetsnasa.ui.list.PlanetListViewModel
import ks.planetsnasa.ui.list.PlanetListVmFactory
import ks.planetsnasa.ui.theme.PlanetsNASATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanetsNASATheme {
                val factory = PlanetListVmFactory(ServiceLocator.planetRepository)
                val vm: PlanetListViewModel = viewModel(factory = factory)
                val nav = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = nav,
                        startDestination = "list",
                        modifier = Modifier.padding(innerPadding)
                    )
                    {
                        composable("list") {
                            PlanetListScreen(
                                viewModel = vm,
                                onPlanetClick = { nasaId -> nav.navigate("detail/$nasaId") }
                            )
                        }
                        composable("detail/{nasaId}") { backStackEntry ->
                            val nasaId =
                                backStackEntry.arguments?.getString("nasaId") ?: return@composable
                            PlanetDetailScreen(nasaId = nasaId, onBack = { nav.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
