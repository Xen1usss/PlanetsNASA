package ks.planetsnasa.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlanetListScreen(
    viewModel: PlanetListViewModel,
    onPlanetClick: (String) -> Unit, // id
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is PlanetListState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is PlanetListState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Не удалось загрузить данные")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.refresh() }) { Text("Повторить") }
                }
            }
        }

        is PlanetListState.Empty -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет данных")
            }
        }

        is PlanetListState.Content -> {

            // Пока VM переключает состояние на Loading при refresh(),
            // индикатор сверху будет показываться, но контент пропадать.
            // Это ок как первый шаг.
            val refreshing = state is PlanetListState.Loading
            val refreshState = rememberPullRefreshState(
                refreshing = refreshing,
                onRefresh = { viewModel.refresh() }
            )

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.items, key = { it.id }) { item ->
                        PlanetCard(item = item, onClick = { onPlanetClick(item.id) })
                    }
                }

                // индикатор сверху по центру
                PullRefreshIndicator(
                    refreshing = refreshing,
                    state = refreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
