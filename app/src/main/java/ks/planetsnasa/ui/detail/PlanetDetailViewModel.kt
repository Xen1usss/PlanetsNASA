package ks.planetsnasa.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ks.planetsnasa.domain.usecase.GetPlanetByIdUseCase
import ks.planetsnasa.ui.model.PlanetDetailUiModel
import javax.inject.Inject

sealed interface PlanetDetailState {
    data object Loading : PlanetDetailState
    data class Content(val item: PlanetDetailUiModel) : PlanetDetailState
    data class Error(val message: String) : PlanetDetailState
}

@HiltViewModel
class PlanetDetailViewModel @Inject constructor(
    private val getPlanetById: GetPlanetByIdUseCase,
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
            runCatching { getPlanetById(nasaId) }
                .map { d ->
                    d?.let {
                        PlanetDetailUiModel(
                            id = it.id,
                            title = it.title,
                            imageUrl = it.imageUrl,
                            description = it.description,
                            date = it.dateIso
                        )
                    }
                }
                .onSuccess { ui ->
                    if (ui == null) _state.value = PlanetDetailState.Error("Not found")
                    else _state.value = PlanetDetailState.Content(ui)
                }
                .onFailure { e ->
                    _state.value = PlanetDetailState.Error(e.message ?: "Unknown error")
                }
        }
    }
}