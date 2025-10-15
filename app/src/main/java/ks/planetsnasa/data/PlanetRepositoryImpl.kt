package ks.planetsnasa.data

import ks.planetsnasa.data.local.PlanetDao
import ks.planetsnasa.data.local.PlanetEntity
import ks.planetsnasa.data.remote.NasaImageApi
import ks.planetsnasa.domain.model.Planet
import ks.planetsnasa.domain.model.PlanetDetail
import ks.planetsnasa.domain.repository.PlanetRepository
import javax.inject.Inject

class PlanetRepositoryImpl @Inject constructor(
    private val api: NasaImageApi,
    private val dao: PlanetDao
) : PlanetRepository {

    override suspend fun getPlanets(page: Int): List<Planet> {

        val cached = dao.getByPage(page)
        if (cached.isNotEmpty()) {
            return cached.map { it.toDomain() }
        }

        val resp = api.searchImages(query = "planet", page = page)
        val items = resp.collection?.items.orEmpty()

        val now = System.currentTimeMillis()

        val toInsert = items.mapIndexed { idx, item ->
            val data = item.data?.firstOrNull() ?: return@mapIndexed null
            val title = data.title?.takeIf { it.isNotBlank() } ?: return@mapIndexed null
            val imageUrl = item.links?.firstOrNull { !it.href.isNullOrBlank() }?.href ?: return@mapIndexed null
            val id = data.nasa_id ?: title
            PlanetEntity(
                id = id,
                title = title,
                imageUrl = imageUrl,
                page = page,
                indexInPage = idx,
                cachedAt = now
            )
        }.filterNotNull()

        if (page == 1) {
            dao.clearAll()
        }
        if (toInsert.isNotEmpty()) {
            dao.insertAll(toInsert)
        }

        return toInsert.map { it.toDomain() }
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
        val desc = rawDesc?.trim()?.takeIf { !it.equals(title, ignoreCase = true) }

        return PlanetDetail(
            id = data.nasa_id ?: return null,
            title = title,
            imageUrl = imageUrl,
            description = desc,
            dateIso = data.date_created
        )
    }

    private fun PlanetEntity.toDomain() = Planet(
        id = id,
        title = title,
        imageUrl = imageUrl
    )
}