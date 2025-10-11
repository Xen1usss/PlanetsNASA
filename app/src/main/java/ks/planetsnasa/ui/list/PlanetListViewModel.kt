package ks.planetsnasa.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ks.planetsnasa.ui.fake.fakePlanets
import ks.planetsnasa.ui.model.PlanetUiModel

sealed interface PlanetListState {
    data object Loading : PlanetListState
    data class Content(val items: List<PlanetUiModel>) : PlanetListState
    data object Empty : PlanetListState
    data class Error(val message: String) : PlanetListState
}

class PlanetListViewModel : ViewModel() {
    private val _state = MutableStateFlow<PlanetListState>(PlanetListState.Loading)
    val state: StateFlow<PlanetListState> = _state

    init {
        // Имитация загрузки
        viewModelScope.launch {
            delay(600)
            val items = fakePlanets
            _state.value = if (items.isEmpty()) PlanetListState.Empty else PlanetListState.Content(items)
        }
    }

    fun onRetry() {
        _state.value = PlanetListState.Loading
        viewModelScope.launch {
            delay(400)
            _state.value = PlanetListState.Content(fakePlanets)
        }
    }
}