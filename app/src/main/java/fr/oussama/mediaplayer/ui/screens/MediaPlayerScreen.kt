package fr.oussama.mediaplayer.ui.screens

import java.lang.Float
import android.net.Uri
import android.view.ViewGroup
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import fr.oussama.mediaplayer.R
import fr.oussama.mediaplayer.viewmodels.MediaPlayerViewModel


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
    var duration = remember {
        mutableStateOf(1)
    }
    var videoAspectRatio = remember {
        mutableStateOf(1f)
    }
    var videoWidth = remember {
        mutableStateOf(0)
    }
    var videoHeight = remember {
        mutableStateOf(0)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        AndroidView(
            factory = {
                videoView.value.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                videoView.value.setOnPreparedListener {
                    videoWidth.value = mediaPlayer.value.videoWidth
                    videoHeight.value = mediaPlayer.value.videoHeight
                    videoAspectRatio.value = videoWidth.value.toFloat() / videoHeight.value.toFloat()
                    videoView.value.layoutParams = ViewGroup.LayoutParams(
                        videoView.value.width,
                        (videoView.value.width / videoAspectRatio.value).toInt()
                    )
                    duration.value = mediaPlayer.value.duration
                    viewModel.saveIsPrepared(true)
                    if (isPlaying.value) {
                        mediaPlayer.value.start()
                        mediaPlayer.value.seekTo(currentPosition.value)
                    }
                }
                videoView.value
            },
            modifier  = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
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

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = (videoWidth.value / videoAspectRatio.value).dp)
        ) {
            LinearProgressIndicator(
                progress = currentPosition.value.toFloat() / duration.value,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red
            )
        }

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
                        if (isPlaying.value) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                    ),
                    contentDescription = if (isPlaying.value) "Pause" else "Play"
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
            }
        }
    }
}
