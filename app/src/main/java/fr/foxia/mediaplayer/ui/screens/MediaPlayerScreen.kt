package fr.foxia.mediaplayer.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import fr.foxia.mediaplayer.R
import fr.foxia.mediaplayer.viewmodels.MediaPlayerViewModel


@Composable
fun MediaPlayerScreen(
    videoUrl: String,
    viewModel: MediaPlayerViewModel
) {
    val isPlaying = viewModel.isPlaying.collectAsState()
    val currentPosition = viewModel.currentPosition.collectAsState()
    val isPrepared = viewModel.isPrepared.collectAsState()
    val mediaPlayer = viewModel.mediaPlayer.collectAsState()
    val videoView = viewModel.videoView.collectAsState()
    val duration = viewModel.duration.collectAsState()
    val videoViewPosition = remember { mutableStateOf(IntOffset.Zero) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black)
            .padding(bottom = 10.dp)
    ) {

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    videoViewPosition.value = IntOffset(
                        x = coordinates.positionInParent().x.toInt(),
                        y = coordinates.positionInParent().y.toInt()
                    )
                },
            factory = {
                videoView.value
            },
            update = { view ->
                if (isPrepared.value) {
                    if (isPlaying.value) {
                        view.start()
                        mediaPlayer.value.start()
                        view.seekTo(currentPosition.value)
                        mediaPlayer.value.seekTo(currentPosition.value)
                    } else {
                        view.pause()
                        mediaPlayer.value.pause()
                    }
                } else {
                    mediaPlayer.value.apply {
                        reset()
                        setDataSource(videoUrl)
                        setOnPreparedListener(null)
                        prepareAsync()
                    }
                    view.setVideoURI(Uri.parse(videoUrl))
                    view.requestFocus()
                }
            }
        )


        LinearProgressIndicator(
            progress = currentPosition.value.toFloat() / duration.value,
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(constraints.maxWidth, placeable.height) {
                        println("y " + videoViewPosition.value.y)
                        println("height " + videoView.value.height)
                        placeable.placeRelative(0,
                            videoViewPosition.value.y + videoView.value.height
                        )
                    }
                },
            color = Color.Red
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    viewModel.togglePlayback()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying.value) fr.foxia.mediaplayer.R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                    ),
                    contentDescription = if (isPlaying.value) "Pause" else "Play",
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                )
            }
        }
    }

    LaunchedEffect(isPlaying.value) {
        if (!isPlaying.value) {
            viewModel.saveCurrentPosition(mediaPlayer.value.currentPosition)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Complete the video playback on screen rotation
            if (mediaPlayer.value.isPlaying) {
                viewModel.saveCurrentPosition(mediaPlayer.value.currentPosition)
                viewModel.setupMediaPlayerCallbacks();
            }
        }
    }
}
