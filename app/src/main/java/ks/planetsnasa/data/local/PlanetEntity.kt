package ks.planetsnasa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planets")
data class PlanetEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val page: Int,
    val cachedAt: Long
)