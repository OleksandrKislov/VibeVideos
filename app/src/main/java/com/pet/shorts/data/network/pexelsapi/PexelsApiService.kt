package com.pet.shorts.data.network.pexelsapi

import com.pet.shorts.BuildConfig
import com.pet.shorts.data.network.pexelsapi.models.PexelsApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PexelsApiService {
    @GET("videos/search")
    @Headers("Authorization: ${BuildConfig.pexelsApiKey}")
    suspend fun searchVideos(
        @Query("query") searchRequest: String,
        @Query("page") page: Int,
        @Query("locale") locale: String = "en-US",
        @Query("per_page") perPage: Int = 10
    ): PexelsApiResponse

    @GET("videos/popular")
    @Headers("Authorization: ${BuildConfig.pexelsApiKey}")
    suspend fun getVideos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 10
    ): PexelsApiResponse
}