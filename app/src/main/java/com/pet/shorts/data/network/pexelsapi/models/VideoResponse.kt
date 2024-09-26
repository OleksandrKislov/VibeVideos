package com.pet.shorts.data.network.pexelsapi.models

import com.google.gson.annotations.SerializedName

data class VideoResponse(

    @SerializedName("id") var id: Int,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("duration") var duration: Int? = null,
    @SerializedName("full_res") var fullRes: String? = null,
    @SerializedName("tags") var tags: List<String> = emptyList(),
    @SerializedName("url") var url: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("avg_color") var avgColor: String? = null,
    @SerializedName("user") var user: User? = User(),
    @SerializedName("video_files") var videoFiles: List<VideoFiles> = emptyList(),
    @SerializedName("video_pictures") var videoPictures: List<VideoPictures> = emptyList()

)