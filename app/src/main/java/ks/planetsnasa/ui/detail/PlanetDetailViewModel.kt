package ks.planetsnasa.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ks.planetsnasa.data.PlanetRepository
import ks.planetsnasa.ui.model.PlanetDetailUiModel
import javax.inject.Inject

sealed interface PlanetDetailState {
    data object Loading : PlanetDetailState
    data class Content(val item: PlanetDetailUiModel) : PlanetDetailState
    data class Error(val message: String) : PlanetDetailState
}

@HiltViewModel
class PlanetDetailViewModel @Inject constructor(
    private val repo: PlanetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<PlanetDetailState>(PlanetDetailState.Loading)
    val state: StateFlow<PlanetDetailState> = _state

    private val nasaId: String = checkNotNull(savedStateHandle["nasaId"])

    init {
        refresh()
    }

    fun refresh() {
        _state.value = PlanetDetailState.Loading
        viewModelScope.launch {
            runCatching { repo.getById(nasaId) }
                .onSuccess { item ->
                    if (item == null) _state.value = PlanetDetailState.Error("Not found")
                    else _state.value = PlanetDetailState.Content(item)
                }
                .onFailure { e ->
                    _state.value = PlanetDetailState.Error(e.message ?: "Unknown error")
                }
        }
    }
}
