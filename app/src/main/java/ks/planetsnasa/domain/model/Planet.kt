package ks.planetsnasa.domain.model

data class Planet(
    val id: String,
    val title: String,
    val imageUrl: String
)

data class PlanetDetail(
    val id: String,
    val title: String,
    val imageUrl: String,
    val description: String?,
    val dateIso: String?
)