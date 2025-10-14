package ks.planetsnasa.data.remote

data class NasaSearchResponse(
    val collection: NasaCollection?
)

data class NasaCollection(
    val href: String?,
    val items: List<NasaItem>?
)

data class NasaItem(
    val data: List<NasaData>?,
    val links: List<NasaLink>?
)

data class NasaData(
    val title: String?,
    val nasa_id: String?,
    val description: String?,
    val date_created: String?
)

data class NasaLink(
    val href: String?,    // ссылка на превью/картинку
    val render: String?   // "image"
)
