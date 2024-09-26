package com.pet.shorts.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pet.shorts.domain.models.Video
import com.pet.shorts.domain.repository.VideoRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val videoRepo: VideoRepo
) : ViewModel() {
    data class ScreenState(
        val videosList: Flow<PagingData<Video>>? = null,
        val searchRequest: String = "",
        val isVideoPlaying: Boolean = true,
        val isFavorite: Map<Int, Boolean> = mapOf()
    )

    sealed class UiEvent {
        data class ToggleFavorite(val video: Video) : UiEvent()
        data class UpdateSearchRequest(val request: String) : UiEvent()
        data object FetchVideosByRequest : UiEvent()
        data class SetIsVideoPlaying(val value: Boolean) : UiEvent()
        data class SubscribeIsFavorite(val videoId: Int) : UiEvent()
        data class UnsubscribeIsFavorite(val videoId: Int) : UiEvent()
    }

    private val isFavoriteSubscribers = MutableStateFlow<Map<Int, Flow<Boolean>>>(mapOf()).also {
        viewModelScope.launch {
            it.collectLatest { map ->
                combine(map.values) {
                    map.keys.mapIndexed { index, id ->
                        id to it[index]
                    }.toMap()
                }.collectLatest { value ->
                    _state.update {
                        it.copy(isFavorite = value)
                    }
                }
            }
        }
    }

    private val _state: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState())
    val state: StateFlow<ScreenState> = _state.asStateFlow()
    /*val state: StateFlow<ScreenState>
        field = MutableStateFlow(ScreenState())*/

    init {
        fetchVideosByRequest()
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ToggleFavorite -> toggleFavorite(event.video)
            is UiEvent.UpdateSearchRequest -> updateSearchRequest(event.request)
            is UiEvent.FetchVideosByRequest -> fetchVideosByRequest()
            is UiEvent.SetIsVideoPlaying -> setIsVideoPlaying(event.value)
            is UiEvent.SubscribeIsFavorite -> subscribeIsFavorite(event.videoId)
            is UiEvent.UnsubscribeIsFavorite -> unsubscribeIsFavorite(event.videoId)
        }
    }

    private fun subscribeIsFavorite(videoId: Int) {
        isFavoriteSubscribers.update {
            it + (videoId to videoRepo.subscribeIsFavorite(videoId))
        }
    }

    private fun unsubscribeIsFavorite(videoId: Int) {
        isFavoriteSubscribers.update { map ->
            map.filterNot { it.key == videoId }
        }
    }

    private fun toggleFavorite(video: Video) {
        viewModelScope.launch {
            _state.value.isFavorite[video.id]?.let {
                if (it) {
                    videoRepo.deleteVideoFromFavorite(video)
                } else {
                    videoRepo.addVideoToFavorite(video)
                }
            }
        }

    }

    private fun updateSearchRequest(request: String) {
        _state.update { it.copy(searchRequest = request) }
    }

    private fun fetchVideosByRequest() {
        _state.update {
            it.copy(
                videosList = videoRepo.getVideoList(searchRequest = _state.value.searchRequest)
                    .cachedIn(viewModelScope)
            )
        }
    }

    private fun setIsVideoPlaying(value: Boolean) {
        _state.update { it.copy(isVideoPlaying = value) }
    }
}