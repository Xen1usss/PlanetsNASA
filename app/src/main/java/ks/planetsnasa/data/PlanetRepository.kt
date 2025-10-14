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
        val data = item.data?.firstOrNull() ?: return null

        val imageUrl = item.links?.firstOrNull { !it.href.isNullOrBlank() }?.href ?: return null
        val id = data.nasa_id ?: return null
        val title = data.title ?: "Untitled"

        // PlanetRepository.kt (в getById)
        val rawDesc = when {
            !data.description.isNullOrBlank() -> data.description
            !data.description_508.isNullOrBlank() -> data.description_508
            else -> null
        }
        val desc = rawDesc
            ?.trim()
            ?.takeIf { !it.equals(title, ignoreCase = true) } // ← не дублируем заголовок


        return PlanetDetailUiModel(
            id = id,
            title = title,
            imageUrl = imageUrl,
            description = desc,
            date = data.date_created
        )
    }

}
