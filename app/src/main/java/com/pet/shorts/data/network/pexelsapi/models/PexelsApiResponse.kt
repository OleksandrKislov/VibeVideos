package com.pet.shorts.data.network.pexelsapi.models

import com.google.gson.annotations.SerializedName

data class PexelsApiResponse(
    @SerializedName("page") var page: Int,
    @SerializedName("per_page") var perPage: Int,
    @SerializedName("videos") var videos: List<VideoResponse> = emptyList(),
    @SerializedName("total_results") var totalResults: Int,
    @SerializedName("next_page") var nextPage: String? = null,
    @SerializedName("url") var url: String? = null
)
