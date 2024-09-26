package com.pet.shorts.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pet.shorts.data.database.entities.FavoriteVideoEntity
import com.pet.shorts.data.database.entities.FavoriteVideoWithPictures
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteVideoDao {

    @Transaction
    @Query("SELECT * FROM favoritevideoentity ORDER BY created_at DESC")
    fun getFavoriteVideoWithPicturesPaged(): PagingSource<Int, FavoriteVideoWithPictures>

    @Query("SELECT EXISTS (SELECT id FROM favoritevideoentity WHERE id = :id)")
    fun subscribeExistence(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteVideoEntity: FavoriteVideoEntity)

    @Delete
    suspend fun delete(favoriteVideoEntity: FavoriteVideoEntity)
}