package ks.planetsnasa.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ks.planetsnasa.data.PlanetRepository
import ks.planetsnasa.data.remote.NasaImageApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePlanetRepository(api: NasaImageApi): PlanetRepository =
        PlanetRepository(api)
}