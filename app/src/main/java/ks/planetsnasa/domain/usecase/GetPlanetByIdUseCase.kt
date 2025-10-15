package ks.planetsnasa.domain.usecase

import ks.planetsnasa.domain.model.PlanetDetail
import ks.planetsnasa.domain.repository.PlanetRepository

class GetPlanetByIdUseCase(private val repo: PlanetRepository) {
    suspend operator fun invoke(id: String): PlanetDetail? = repo.getPlanetById(id)
}