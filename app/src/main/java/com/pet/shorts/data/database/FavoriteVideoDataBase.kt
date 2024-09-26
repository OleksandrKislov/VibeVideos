package com.pet.shorts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pet.shorts.data.database.entities.FavoriteVideoEntity
import com.pet.shorts.data.database.entities.PicturesEntity

@Database(entities = [FavoriteVideoEntity::class, PicturesEntity::class], version = 1)
abstract class FavoriteVideoDataBase : RoomDatabase() {
    abstract fun favoriteVideoDao(): FavoriteVideoDao
    abstract fun picturesDao(): PicturesDao
}