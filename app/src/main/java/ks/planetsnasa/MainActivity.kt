package ks.planetsnasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ks.planetsnasa.ui.list.PlanetListScreen
import ks.planetsnasa.ui.list.PlanetListViewModel
import ks.planetsnasa.ui.theme.PlanetsNASATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanetsNASATheme {
                val vm: PlanetListViewModel = viewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlanetListScreen(
                        viewModel = vm,
                        onPlanetClick = { planetId ->
                            // TODO: навигация на детальный экран (пока можно просто логировать)
                            // Log.d("Main", "Clicked: $planetId")
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
