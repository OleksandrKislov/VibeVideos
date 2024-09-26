package com.pet.shorts.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteVideoWithPictures(
    @Embedded val favoriteVideo: FavoriteVideoEntity,
    @Relation(
          parentColumn = "id",
          entityColumn = "videoId"
    )
    val pictures: List<PicturesEntity>
)