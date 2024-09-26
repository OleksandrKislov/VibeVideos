package com.pet.shorts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomProgressBar(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    var currentPosition by remember { mutableLongStateOf(0) }
    var duration by remember { mutableLongStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (isActive) {
            if (!isDragging) {
                duration = player.duration.coerceAtLeast(0)
                currentPosition = player.currentPosition.coerceAtLeast(0)
            }
            delay(17)
        }
    }

    Slider(
        modifier = modifier.fillMaxWidth(),
        value = currentPosition.toFloat(),
        onValueChange = { value ->
            isDragging = true
            currentPosition = value.toLong()
        },
        onValueChangeFinished = {
            isDragging = false
            player.seekTo(currentPosition)
        },
        valueRange = 0f..duration.toFloat(),
        thumb = {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        },
        track = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(100))
                    .background(Color.White)
            )
        }
    )
}