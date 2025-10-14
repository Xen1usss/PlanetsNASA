package ks.planetsnasa.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ks.planetsnasa.data.PlanetRepository

class PlanetDetailVmFactory(
    private val repo: PlanetRepository,
    private val nasaId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanetDetailViewModel::class.java)) {
            return PlanetDetailViewModel(repo, nasaId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}