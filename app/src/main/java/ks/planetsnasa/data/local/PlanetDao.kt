package ks.planetsnasa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PlanetDao {

    @androidx.room.Query("SELECT * FROM planets WHERE page = :page ORDER BY indexInPage")
    suspend fun getByPage(page: Int): List<PlanetEntity>

    @androidx.room.Query("SELECT COUNT(*) FROM planets")
    suspend fun countAll(): Int

    @androidx.room.Query("DELETE FROM planets")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PlanetEntity>)
}
