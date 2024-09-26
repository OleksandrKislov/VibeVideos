package com.pet.shorts.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(entity = FavoriteVideoEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("videoId"),
        onDelete = ForeignKey.CASCADE)]
)
data class PicturesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: Int,
    @ColumnInfo(name = "picture") val picture: String,
)