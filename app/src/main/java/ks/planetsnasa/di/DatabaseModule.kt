package ks.planetsnasa.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ks.planetsnasa.data.local.AppDatabase
import ks.planetsnasa.data.local.PlanetDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "planets.db")
            .fallbackToDestructiveMigration() // быстро и надёжно для ТЗ
            .build()

    @Provides @Singleton
    fun providePlanetDao(db: AppDatabase): PlanetDao = db.planetDao()
}