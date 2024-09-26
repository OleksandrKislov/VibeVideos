package com.pet.shorts.data.mappers

import com.pet.shorts.data.database.entities.FavoriteVideoEntity
import com.pet.shorts.data.database.entities.FavoriteVideoWithPictures
import com.pet.shorts.data.network.pexelsapi.models.VideoResponse
import com.pet.shorts.domain.models.Video

fun FavoriteVideoWithPictures.toVideo(): Video {
    return Video(
        id = this.favoriteVideo.id,
        url = this.favoriteVideo.url,
        pictures = this.pictures.map { it.picture }
    )
}

fun VideoResponse.toVideo(): Video {
    return Video(
        id = this.id,
        url = this.videoFiles.last().link, //todo: think about different qualities
        pictures = this.videoPictures.map { it.picture }
    )
}

fun Video.toFavoriteVideoEntity(): FavoriteVideoEntity {
    return FavoriteVideoEntity(
        id = this.id,
        url = this.url
    )
}
