package com.pet.shorts.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pet.shorts.data.network.pexelsapi.PexelsApiService
import com.pet.shorts.data.network.pexelsapi.models.VideoResponse
import com.pet.shorts.domain.errors.NetworkError
import java.net.UnknownHostException
import java.util.Locale

class VideoResponsePagingSource(
    private val pexelsApiService: PexelsApiService,
    private val searchRequest: String,
    private val locale: Locale
) : PagingSource<Int, VideoResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoResponse> {
        val page = params.key ?: 1

        val response = try {
            if (searchRequest.isEmpty() || searchRequest.isBlank()) {
                pexelsApiService.getVideos(page = page, perPage = 10)
            } else {
                pexelsApiService.searchVideos(
                    searchRequest = searchRequest,
                    page = page,
                    locale = locale.toString().replace("_", "-"),
                    perPage = 10
                )
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                return LoadResult.Error(NetworkError.NoInternetConnection())
            }
            else if (e.cause is NetworkError.HttpTooManyRequests)
                return LoadResult.Error(NetworkError.HttpTooManyRequests())
            else
                return LoadResult.Error(e)
        }

        return LoadResult.Page(
            data = response.videos,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (response.videos.isNotEmpty()) page + 1 else null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, VideoResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}