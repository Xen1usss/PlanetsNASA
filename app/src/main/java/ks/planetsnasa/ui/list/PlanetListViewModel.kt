package ks.planetsnasa.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ks.planetsnasa.domain.usecase.GetPlanetsPageUseCase
import ks.planetsnasa.ui.model.PlanetUiModel
import javax.inject.Inject

sealed interface PlanetListState {
    data object Loading : PlanetListState
    data class Content(val items: List<PlanetUiModel>) : PlanetListState
    data object Empty : PlanetListState
    data class Error(val message: String) : PlanetListState
}
@HiltViewModel
class PlanetListViewModel @Inject constructor(
    private val getPlanetsPage: GetPlanetsPageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PlanetListState>(PlanetListState.Loading)
    val state: StateFlow<PlanetListState> = _state

    init { refresh() }

    fun refresh() {
        // первый старт — Loading, дальше можно сделать «мягкий рефреш»
        _state.value = PlanetListState.Loading
        viewModelScope.launch {
            runCatching { getPlanetsPage(page = 1) }
                .map { list ->
                    list.map { d ->
                        PlanetUiModel(
                            id = d.id,
                            name = d.title,
                            imageUrl = d.imageUrl
                        )
                    }
                }
                .onSuccess { ui ->
                    _state.value = if (ui.isEmpty()) PlanetListState.Empty
                    else PlanetListState.Content(ui)
                }
                .onFailure { e ->
                    _state.value = PlanetListState.Error(e.message ?: "Unknown error")
                }
        }
    }
}