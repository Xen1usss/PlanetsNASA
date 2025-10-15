package ks.planetsnasa.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ks.planetsnasa.domain.repository.PlanetRepository
import ks.planetsnasa.domain.usecase.GetPlanetByIdUseCase
import ks.planetsnasa.domain.usecase.GetPlanetsPageUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetPlanetsPage(repo: PlanetRepository) = GetPlanetsPageUseCase(repo)

    @Provides
    @Singleton
    fun provideGetPlanetById(repo: PlanetRepository) = GetPlanetByIdUseCase(repo)
}