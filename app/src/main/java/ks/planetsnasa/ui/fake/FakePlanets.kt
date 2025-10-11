package ks.planetsnasa.ui.fake

import ks.planetsnasa.ui.model.PlanetUiModel


// Взял условные "планеты" + примеры NASA APOD изображений/заглушек.
// Для старта можно оставить любые валидные https-ссылки на картинки.
val fakePlanets = listOf(
    PlanetUiModel(
        id = "mercury",
        name = "Mercury",
        imageUrl = "https://apod.nasa.gov/apod/image/1901/Mercury2018_Messenger_960.jpg"
    ),
    PlanetUiModel(
        id = "venus",
        name = "Venus",
        imageUrl = "https://apod.nasa.gov/apod/image/2409/VenusClouds_gerger_960.jpg"
    ),
    PlanetUiModel(
        id = "earth",
        name = "Earth",
        imageUrl = "https://apod.nasa.gov/apod/image/1905/EarthRiseApollo8_960.jpg"
    ),
    PlanetUiModel(
        id = "mars",
        name = "Mars",
        imageUrl = "https://apod.nasa.gov/apod/image/2002/MarsMosaic_960.jpg"
    ),
    PlanetUiModel(
        id = "jupiter",
        name = "Jupiter",
        imageUrl = "https://apod.nasa.gov/apod/image/1807/JupiterSouth_Juno_960.jpg"
    ),
    PlanetUiModel(
        id = "saturn",
        name = "Saturn",
        imageUrl = "https://apod.nasa.gov/apod/image/1909/SaturnRings_Cassini_960.jpg"
    ),
    PlanetUiModel(
        id = "uranus",
        name = "Uranus",
        imageUrl = "https://apod.nasa.gov/apod/image/1901/Uranus_Voyager2_960.jpg"
    ),
    PlanetUiModel(
        id = "neptune",
        name = "Neptune",
        imageUrl = "https://apod.nasa.gov/apod/image/1909/Neptune_Voyager2_960.jpg"
    )
)
