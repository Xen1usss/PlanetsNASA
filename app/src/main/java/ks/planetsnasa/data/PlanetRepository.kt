package ks.planetsnasa.data

import ks.planetsnasa.data.remote.NasaImageApi
import ks.planetsnasa.ui.model.PlanetUiModel

class PlanetRepository(private val api: NasaImageApi) {

    suspend fun loadPlanetsFirstPage(): List<PlanetUiModel> {
        val resp = api.searchImages(query = "planet", page = 1)
        val items = resp.collection?.items.orEmpty()

        return items.mapNotNull { item ->
            val title = item.data?.firstOrNull()?.title?.ifBlank { null } ?: return@mapNotNull null
            val image = item.links?.firstOrNull { it.href?.isNotBlank() == true }?.href ?: return@mapNotNull null
            val id = item.data?.firstOrNull()?.nasa_id ?: title
            PlanetUiModel(
                id = id,
                name = title,
                imageUrl = image
            )
        }
        // .distinctBy { it.id } // при желании
        // .take(30)
    }
}
