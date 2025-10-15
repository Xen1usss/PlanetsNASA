package ks.planetsnasa.data

import ks.planetsnasa.data.remote.NasaImageApi
import ks.planetsnasa.domain.model.Planet
import ks.planetsnasa.domain.model.PlanetDetail
import ks.planetsnasa.domain.repository.PlanetRepository
import javax.inject.Inject

class PlanetRepositoryImpl @Inject constructor(
    private val api: NasaImageApi
) : PlanetRepository {

    override suspend fun getPlanets(page: Int): List<Planet> {
        val resp = api.searchImages(query = "planet", page = page)
        val items = resp.collection?.items.orEmpty()
        return items.mapNotNull { item ->
            val data = item.data?.firstOrNull() ?: return@mapNotNull null
            val title = data.title?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val imageUrl =
                item.links?.firstOrNull { !it.href.isNullOrBlank() }?.href ?: return@mapNotNull null
            val id = data.nasa_id ?: title
            Planet(id = id, title = title, imageUrl = imageUrl)
        }
    }

    override suspend fun getPlanetById(nasaId: String): PlanetDetail? {
        val resp = api.searchByNasaId(nasaId)
        val item = resp.collection?.items.orEmpty().firstOrNull() ?: return null
        val data = item.data?.firstOrNull() ?: return null
        val imageUrl = item.links?.firstOrNull { !it.href.isNullOrBlank() }?.href ?: return null

        val title = data.title ?: "Untitled"
        val rawDesc = when {
            !data.description.isNullOrBlank() -> data.description
            !data.description_508.isNullOrBlank() -> data.description_508
            else -> null
        }

        val desc = rawDesc
            ?.trim()
            ?.takeIf { !it.equals(title, ignoreCase = true) }

        return PlanetDetail(
            id = data.nasa_id ?: return null,
            title = title,
            imageUrl = imageUrl,
            description = desc,
            dateIso = data.date_created
        )
    }
}
