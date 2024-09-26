package com.pet.shorts.domain.repository

import androidx.paging.PagingData
import com.pet.shorts.domain.models.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepo {
    fun getVideoList(searchRequest: String): Flow<PagingData<Video>>

    fun getFavoriteVideos(): Flow<PagingData<Video>>

    fun subscribeIsFavorite(videoId: Int): Flow<Boolean>

    suspend fun addVideoToFavorite(video: Video)

    suspend fun deleteVideoFromFavorite(video: Video)
}