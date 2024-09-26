package com.pet.shorts.ui.screen.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pet.shorts.domain.models.Video
import com.pet.shorts.domain.repository.VideoRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModel(
    private val videoRepo: VideoRepo
) : ViewModel() {
    data class ScreenState(
        val videosList: Flow<PagingData<Video>>,
        val videoToShow: Video? = null,
        val isVideoToShowFavorite: Boolean? = null,
        val isVideoPlaying: Boolean = true,
        val lastShowedVideoId: Int = -1
    )

    sealed class UiEvent {
        data class ToggleFavorite(val video: Video) : UiEvent()
        data class OpenVideo(val video: Video) : UiEvent()
        data object CloseVideo : UiEvent()
        data class SetIsVideoPlaying(val value: Boolean) : UiEvent()
        data class SubscribeIsFavorite(val videoId: Int) : UiEvent()
    }

    private val _state = MutableStateFlow(
        ScreenState(
            videosList = videoRepo.getFavoriteVideos().cachedIn(viewModelScope)
        )
    )
    val state = _state.asStateFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ToggleFavorite -> toggleFavorite(event.video)
            is UiEvent.OpenVideo -> openVideo(event.video)
            is UiEvent.CloseVideo -> closeVideo()
            is UiEvent.SetIsVideoPlaying -> setIsVideoPlaying(event.value)
            is UiEvent.SubscribeIsFavorite -> subscribeIsFavorite(event.videoId)
        }
    }

    private fun subscribeIsFavorite(videoId: Int) {
        viewModelScope.launch {
            videoRepo.subscribeIsFavorite(videoId)
                .combine(_state.mapLatest { it.videoToShow }.distinctUntilChanged()) { isFav, video ->
                    if (video != null)
                        isFav
                    else
                        null
                }
                .takeWhile { it != null }
                .collectLatest { isFavorite ->
                    _state.update {
                        it.copy(isVideoToShowFavorite = isFavorite)
                    }
                }
        }
    }

    private fun toggleFavorite(video: Video) {
        viewModelScope.launch {
            _state.value.isVideoToShowFavorite?.let {
                if (it) {
                    videoRepo.deleteVideoFromFavorite(video)
                } else {
                    videoRepo.addVideoToFavorite(video)
                }
            }
        }
    }

    private fun openVideo(video: Video) {
        _state.update {
            it.copy(
                videoToShow = video,
                lastShowedVideoId = video.id
            )
        }
    }

    private fun closeVideo() {
        _state.update {
            it.copy(
                videoToShow = null,
                isVideoToShowFavorite = null,
                isVideoPlaying = true
            )
        }
    }

    private fun setIsVideoPlaying(value: Boolean) {
        _state.update { it.copy(isVideoPlaying = value) }
    }
}