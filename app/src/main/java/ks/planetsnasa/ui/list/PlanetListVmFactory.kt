package ks.planetsnasa.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ks.planetsnasa.data.PlanetRepository

class PlanetListVmFactory(
    private val repo: PlanetRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanetListViewModel::class.java)) {
            return PlanetListViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}