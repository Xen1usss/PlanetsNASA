package ks.planetsnasa.data.di

import android.util.Log
import ks.planetsnasa.data.PlanetRepository
import ks.planetsnasa.data.remote.NasaImageApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {

    private val client: OkHttpClient by lazy {
        val logger = HttpLoggingInterceptor { msg -> Log.d("HTTP", msg) }
        logger.level = HttpLoggingInterceptor.Level.BASIC
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://images-api.nasa.gov/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val nasaApi: NasaImageApi by lazy {
        retrofit.create(NasaImageApi::class.java)
    }

    val planetRepository: PlanetRepository by lazy {
        PlanetRepository(nasaApi)
    }
}