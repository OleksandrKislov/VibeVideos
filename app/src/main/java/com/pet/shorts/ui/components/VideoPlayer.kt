package com.pet.shorts.ui.components

import android.content.Intent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.pet.shorts.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    isVideoPlaying: Boolean,
    videoUrl: String,
    isFavorite: Boolean,
    backgroundImageUrl: String?,
    toggleFavorite: () -> Unit,
    toggleVideoPlaying: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    var isCPIVisible by remember { mutableStateOf(false) }
    val playerListener = remember {
        object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                isCPIVisible = playbackState == ExoPlayer.STATE_BUFFERING
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.removeListener(playerListener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(isVideoPlaying) {
        if (isVideoPlaying)
            exoPlayer.play()
        else
            exoPlayer.pause()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        exoPlayer.pause()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (isVideoPlaying)
            exoPlayer.play()
    }

    var isFavoriteIconVisible by remember { mutableStateOf(false) }
    val favoriteIconAlpha by animateFloatAsState(
        targetValue = if (isFavoriteIconVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "repeatable spec",
        finishedListener = {
            isFavoriteIconVisible = false
        }
    )

    Box(
        modifier = modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onDoubleClick = {
                if (!isFavorite) {
                    toggleFavorite()
                    isFavoriteIconVisible = true
                }
            },
            onClick = { toggleVideoPlaying(!exoPlayer.isPlaying) }
        )
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .blur(15.dp),
            model = backgroundImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        AndroidView(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
            factory = {
                PlayerView(context).apply {
                    useController = false
                    player = exoPlayer.apply {
                        addListener(playerListener)
                    }
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            },
            update = {
                if (isVideoPlaying)
                    it.player?.playWhenReady = true

                if (it.player?.currentMediaItem?.localConfiguration?.uri.toString() != videoUrl) {
                    it.player?.clearMediaItems()
                    it.player?.addMediaItem(MediaItem.fromUri(videoUrl))
                }
            }
        )
        if (isCPIVisible)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Bottom),
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    toggleFavorite()
                    if (!isFavorite) {
                        isFavoriteIconVisible = true
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite_button),
                    tint = Color.White
                )
            }

            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, videoUrl)
                    }
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            context.getString(R.string.share_via)
                        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    )
                }
            ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share_button),
                    tint = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = if (isVideoPlaying)
                        ImageVector.vectorResource(R.drawable.baseline_pause_24)
                    else
                        Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.play_pause_button),
                    tint = Color.White
                )

                CustomProgressBar(
                    modifier = Modifier.fillMaxWidth(),
                    player = exoPlayer
                )
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
                .alpha(favoriteIconAlpha),
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.White
        )
    }
}