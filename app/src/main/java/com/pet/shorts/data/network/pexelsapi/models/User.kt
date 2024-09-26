package com.pet.shorts.data.network.pexelsapi.models

import com.google.gson.annotations.SerializedName


data class User(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("url") var url: String? = null

)