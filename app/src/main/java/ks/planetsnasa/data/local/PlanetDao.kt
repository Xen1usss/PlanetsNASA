package ks.planetsnasa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlanetDao {

    @Query("SELECT * FROM planets WHERE page = :page ORDER BY rowid")
    suspend fun getByPage(page: Int): List<PlanetEntity>

    @Query("SELECT COUNT(*) FROM planets")
    suspend fun countAll(): Int

    @Query("DELETE FROM planets")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PlanetEntity>)
}
