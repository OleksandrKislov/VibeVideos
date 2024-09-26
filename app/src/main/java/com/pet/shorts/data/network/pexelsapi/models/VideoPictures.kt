package com.pet.shorts.data.network.pexelsapi.models

import com.google.gson.annotations.SerializedName


data class VideoPictures(

    @SerializedName("id") var id: Int,
    @SerializedName("nr") var nr: Int,
    @SerializedName("picture") var picture: String

)