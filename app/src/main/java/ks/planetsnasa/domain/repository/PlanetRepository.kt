package ks.planetsnasa.domain.repository

import ks.planetsnasa.domain.model.Planet
import ks.planetsnasa.domain.model.PlanetDetail

interface PlanetRepository {
    suspend fun getPlanets(page: Int = 1): List<Planet>
    suspend fun getPlanetById(nasaId: String): PlanetDetail?
}