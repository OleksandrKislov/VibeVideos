package com.pet.shorts.data.database

import androidx.room.Dao
import androidx.room.Insert
import com.pet.shorts.data.database.entities.PicturesEntity

@Dao
interface PicturesDao {
    @Insert
    suspend fun insert(picturesEntity: PicturesEntity)
}