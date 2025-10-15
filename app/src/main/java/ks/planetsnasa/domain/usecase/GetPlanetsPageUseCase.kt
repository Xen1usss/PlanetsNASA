package ks.planetsnasa.domain.usecase

import ks.planetsnasa.domain.model.Planet
import ks.planetsnasa.domain.repository.PlanetRepository

class GetPlanetsPageUseCase(private val repo: PlanetRepository) {
    suspend operator fun invoke(page: Int): List<Planet> = repo.getPlanets(page)
}