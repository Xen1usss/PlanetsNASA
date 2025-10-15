package ks.planetsnasa.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ks.planetsnasa.data.PlanetRepositoryImpl
import ks.planetsnasa.domain.repository.PlanetRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPlanetRepository(impl: PlanetRepositoryImpl): PlanetRepository
}