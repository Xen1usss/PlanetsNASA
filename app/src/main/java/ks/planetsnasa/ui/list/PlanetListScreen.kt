package ks.planetsnasa.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlanetListScreen(
    viewModel: PlanetListViewModel,
    onPlanetClick: (String) -> Unit, // id
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlanetListState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PlanetListState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Не удалось загрузить данные")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.onRetry() }) { Text("Повторить") }
                }
            }
        }
        is PlanetListState.Empty -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет данных")
            }
        }
        is PlanetListState.Content -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(s.items, key = { it.id }) { item ->
                    PlanetCard(
                        item = item,
                        onClick = { onPlanetClick(item.id) }
                    )
                }
            }
        }
    }
}