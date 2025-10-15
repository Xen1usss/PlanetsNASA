package ks.planetsnasa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlanetEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planetDao(): PlanetDao
}