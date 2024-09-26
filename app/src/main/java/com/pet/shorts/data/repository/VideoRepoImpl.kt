package com.pet.shorts.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.Transaction
import com.pet.shorts.data.database.FavoriteVideoDao
import com.pet.shorts.data.database.PicturesDao
import com.pet.shorts.data.database.entities.FavoriteVideoWithPictures
import com.pet.shorts.data.database.entities.PicturesEntity
import com.pet.shorts.data.mappers.toFavoriteVideoEntity
import com.pet.shorts.data.mappers.toVideo
import com.pet.shorts.data.network.pexelsapi.PexelsApiService
import com.pet.shorts.data.network.pexelsapi.models.VideoResponse
import com.pet.shorts.domain.models.Video
import com.pet.shorts.domain.repository.VideoRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class VideoRepoImpl(
    private val context: Context,
    private val pexelsApiService: PexelsApiService,
    private val favoriteVideoDao: FavoriteVideoDao,
    private val picturesDao: PicturesDao,
) : VideoRepo {

    override fun getVideoList(
        searchRequest: String
    ): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 20
            ),
            pagingSourceFactory = {
                VideoResponsePagingSource(
                    pexelsApiService = pexelsApiService,
                    searchRequest = searchRequest,
                    locale = context.resources.configuration.locales.get(0)
                )
            }
        ).flow.mapLatest { value: PagingData<VideoResponse> ->
            value.map { it.toVideo() }
        }
    }

    override fun getFavoriteVideos(): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 20
            ),
            pagingSourceFactory = { favoriteVideoDao.getFavoriteVideoWithPicturesPaged() }
        ).flow.mapLatest { value: PagingData<FavoriteVideoWithPictures> ->
            value.map { it.toVideo() }
        }
    }

    override fun subscribeIsFavorite(videoId: Int): Flow<Boolean> {
        return favoriteVideoDao.subscribeExistence(id = videoId)
    }

    @Transaction
    override suspend fun addVideoToFavorite(video: Video) {
        favoriteVideoDao.insert(favoriteVideoEntity = video.toFavoriteVideoEntity())
        video.pictures.forEach { picture ->
            picturesDao.insert(PicturesEntity(videoId = video.id, picture = picture))
        }
    }

    override suspend fun deleteVideoFromFavorite(video: Video) {
        favoriteVideoDao.delete(favoriteVideoEntity = video.toFavoriteVideoEntity())
    }
}

