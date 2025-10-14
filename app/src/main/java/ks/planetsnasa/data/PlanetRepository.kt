package ks.planetsnasa.data

import ks.planetsnasa.data.remote.NasaImageApi
import ks.planetsnasa.ui.model.PlanetDetailUiModel
import ks.planetsnasa.ui.model.PlanetUiModel

class PlanetRepository(private val api: NasaImageApi) {

    suspend fun loadPlanetsFirstPage(): List<PlanetUiModel> {
        val resp = api.searchImages(query = "planet", page = 1)
        val items = resp.collection?.items.orEmpty()

        return items.mapNotNull { item ->
            val data = item.data?.firstOrNull()
            val title = data?.title?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val imageUrl = item.links
                ?.firstOrNull { it.href?.isNotBlank() == true }
                ?.href ?: return@mapNotNull null
            val id = data.nasa_id ?: title

            PlanetUiModel(
                id = id,
                name = title,
                imageUrl = imageUrl
            )
        }
        // при желании:
        // .distinctBy { it.id }
        // .take(30)
    }


    suspend fun getById(nasaId: String): PlanetDetailUiModel? {
        val resp = api.searchByNasaId(nasaId)
        val item = resp.collection?.items.orEmpty().firstOrNull() ?: return null
        val data = item.data?.firstOrNull()
        val link = item.links?.firstOrNull { it.href?.isNotBlank() == true }?.href
        val id = data?.nasa_id ?: return null
        val title = data.title ?: "Untitled"
        val imageUrl = link ?: return null
        return PlanetDetailUiModel(
            id = id,
            title = title,
            imageUrl = imageUrl,
            description = data.description,
            date = data.date_created
        )
    }
}
