package com.pet.shorts.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.pet.shorts.R
import com.pet.shorts.ui.components.PagerErrorHandler
import com.pet.shorts.ui.components.SearchBar
import com.pet.shorts.ui.components.VideoPlayer
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun HomeScreen(
    state: State<HomeViewModel.ScreenState>,
    onEvent: (HomeViewModel.UiEvent) -> Unit,
) {
    val videoLazyPagingItems = state.value.videosList?.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(pageCount = { videoLazyPagingItems?.itemCount ?: 0 })
    LaunchedEffect(videoLazyPagingItems) {
        if (videoLazyPagingItems?.loadState?.isIdle == false)
            pagerState.scrollToPage(0)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 5.dp,
                    horizontal = 15.dp
                ),
            value = state.value.searchRequest,
            onValueChange = { value ->
                onEvent(HomeViewModel.UiEvent.UpdateSearchRequest(value))
            },
            onSearch = { onEvent(HomeViewModel.UiEvent.FetchVideosByRequest) }
        )

        var isError by remember { mutableStateOf(false) }
        when {
            videoLazyPagingItems?.loadState?.refresh is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            videoLazyPagingItems?.loadState?.refresh is LoadState.Error -> {
                val error = videoLazyPagingItems.loadState.refresh as LoadState.Error
                isError = true
                PagerErrorHandler(
                    modifier = Modifier.fillMaxSize(),
                    error = error,
                    retryLoading = videoLazyPagingItems::retry
                )
            }

            videoLazyPagingItems?.loadState?.append is LoadState.Error -> {
                val error = videoLazyPagingItems.loadState.append as LoadState.Error
                isError = true
                PagerErrorHandler(
                    modifier = Modifier.fillMaxSize(),
                    error = error,
                    retryLoading = videoLazyPagingItems::retry
                )
            }

            videoLazyPagingItems?.itemCount == 0 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.no_videos_found_matching_your_request))
                }
            }

            !isError -> {
                VerticalPager(state = pagerState) { page ->
                    videoLazyPagingItems?.get(page)?.let { video ->
                        val exoPlayer: ExoPlayer =
                            koinInject(parameters = { parametersOf(video.url) })
                        val isCurrentPageActive by remember {
                            derivedStateOf {
                                pagerState.settledPage == page
                            }
                        }
                        DisposableEffect(Unit) {
                            onEvent(HomeViewModel.UiEvent.SubscribeIsFavorite(video.id))
                            onDispose {
                                onEvent(HomeViewModel.UiEvent.UnsubscribeIsFavorite(video.id))
                            }
                        }
                        VideoPlayer(
                            modifier = Modifier.fillMaxSize(),
                            exoPlayer = exoPlayer,
                            isVideoPlaying = state.value.isVideoPlaying && isCurrentPageActive,
                            videoUrl = video.url,
                            isFavorite = state.value.isFavorite[video.id] ?: false,
                            backgroundImageUrl = video.pictures.firstOrNull(),
                            toggleFavorite = {
                                onEvent(HomeViewModel.UiEvent.ToggleFavorite(video))
                            },
                            toggleVideoPlaying = {
                                onEvent(HomeViewModel.UiEvent.SetIsVideoPlaying(it))
                            }
                        )
                    }
                }
            }

            videoLazyPagingItems?.loadState?.isIdle == true -> {
                isError = false
            }
        }
    }
}