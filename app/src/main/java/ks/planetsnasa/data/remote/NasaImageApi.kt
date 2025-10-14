package ks.planetsnasa.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NasaImageApi {
    // Пример: https://images-api.nasa.gov/search?q=planet&media_type=image&page=1
    // Базовый URL: https://images-api.nasa.gov/
    @GET("search")
    suspend fun searchImages(
        @Query("q") query: String,
        @Query("media_type") mediaType: String = "image",
        @Query("page") page: Int = 1
    ): NasaSearchResponse
}