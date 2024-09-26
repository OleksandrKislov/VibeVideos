package com.pet.shorts.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteVideoEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

