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
    data class Content(
        val items: List<PlanetUiModel>,
        val refreshing: Boolean = false,
        val loadingMore: Boolean = false
    ) : PlanetListState
    data object Empty : PlanetListState
    data class Error(val message: String) : PlanetListState
}

@HiltViewModel
class PlanetListViewModel @Inject constructor(
    private val getPlanetsPage: GetPlanetsPageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PlanetListState>(PlanetListState.Loading)
    val state: StateFlow<PlanetListState> = _state

    private var page = 1
    private var endReached = false
    private var loadingMore = false

    init { refresh() }

    fun refresh() {
        val current = state.value

        if (current is PlanetListState.Content) {
            viewModelScope.launch {
                _state.value = current.copy(refreshing = true)

                val started = System.currentTimeMillis()
                try {
                    page = 1
                    endReached = false
                    val domain = getPlanetsPage(page = page)
                    val ui = domain.map { PlanetUiModel(it.id, it.title, it.imageUrl) }

                    val elapsed = System.currentTimeMillis() - started
                    val minSpin = 400L
                    if (elapsed < minSpin) kotlinx.coroutines.delay(minSpin - elapsed)

                    _state.value = if (ui.isEmpty()) PlanetListState.Empty
                    else PlanetListState.Content(items = ui, refreshing = false)
                } catch (_: Throwable) {
                    _state.value = current.copy(refreshing = false) // оставляем старый контент
                }
            }
            return
        }

        // первая загрузка (когда ещё нет контента)
        _state.value = PlanetListState.Loading
        viewModelScope.launch {
            try {
                page = 1
                endReached = false
                val ui = getPlanetsPage(page).map { PlanetUiModel(it.id, it.title, it.imageUrl) }
                _state.value = if (ui.isEmpty()) PlanetListState.Empty
                else PlanetListState.Content(items = ui)
            } catch (e: Throwable) {
                _state.value = PlanetListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadNext() {
        val current = state.value as? PlanetListState.Content ?: return
        if (loadingMore || endReached) return

        loadingMore = true
        _state.value = current.copy(loadingMore = true)

        viewModelScope.launch {
            try {
                val next = getPlanetsPage(page = page + 1)
                if (next.isEmpty()) {
                    endReached = true
                    _state.value = current.copy(loadingMore = false)
                } else {
                    page += 1
                    val appended = next.map { PlanetUiModel(it.id, it.title, it.imageUrl) }
                    _state.value = current.copy(
                        items = current.items + appended,
                        loadingMore = false
                    )
                }
            } catch (_: Throwable) {
                _state.value = current.copy(loadingMore = false)
            } finally {
                loadingMore = false
            }
        }
    }
}
