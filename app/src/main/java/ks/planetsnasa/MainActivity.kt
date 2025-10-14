package ks.planetsnasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ks.planetsnasa.ui.detail.PlanetDetailScreen
import ks.planetsnasa.ui.list.PlanetListScreen
import ks.planetsnasa.ui.list.PlanetListViewModel
import ks.planetsnasa.ui.theme.PlanetsNASATheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanetsNASATheme {
                val nav = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = nav,
                        startDestination = "list",
                        modifier = Modifier.padding(innerPadding)
                    )
                    {
                        composable("list") {
                            val vm: PlanetListViewModel = hiltViewModel()
                            PlanetListScreen(
                                viewModel = vm,
                                onPlanetClick = { nasaId -> nav.navigate("detail/$nasaId") }
                            )
                        }
                        composable(
                            route = "detail/{nasaId}",
                            arguments = listOf(navArgument("nasaId") { type = NavType.StringType })
                        ) {
                            PlanetDetailScreen(
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
