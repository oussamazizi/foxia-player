package fr.foxia.mediaplayer.viewmodels

import android.content.Context
import android.media.MediaPlayer
import android.view.ViewGroup
import android.widget.VideoView
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MediaPlayerViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {
    // State variables
    private val _isPlaying = MutableStateFlow( false)
    val isPlaying: StateFlow<Boolean>
        get() = _isPlaying

    private val _currentPosition = MutableStateFlow( 0)
    val currentPosition: StateFlow<Int>
        get() = _currentPosition

    private val _duration = MutableStateFlow( 1)
    val duration: StateFlow<Int>
        get() = _duration

    private val _isPrepared = MutableStateFlow( false)
    val isPrepared: StateFlow<Boolean>
        get() = _isPrepared

    private val _mediaPlayer = MutableStateFlow(MediaPlayer())
    val mediaPlayer: StateFlow<MediaPlayer>
        get() = _mediaPlayer

    private val _videoView = MutableStateFlow(VideoView(context))
    val videoView: StateFlow<VideoView>
        get() = _videoView

    private var mediaPlayerInitialized = false

    init {
        _isPlaying.value = true
        setupMediaPlayerCallbacks()
    }


    fun togglePlayback() {
        _isPlaying.value = !isPlaying.value
    }

    fun saveCurrentPosition(position: Int) {
        _currentPosition.value = position
        //savedStateHandle.set("currentPosition", position)
    }

    fun saveIsPrepared(isPrepared: Boolean) {
        _isPrepared.value = isPrepared
    }

    fun setupMediaPlayerCallbacks() {
        val mediaPlayer = _mediaPlayer.value

        videoView.value.setOnPreparedListener {
            val videoWidth = mediaPlayer.videoWidth
            val videoHeight = mediaPlayer.videoHeight
            val videoAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()

            val margin = 16 // Adjust the margin value as needed

            val layoutParams = ViewGroup.MarginLayoutParams(
                videoView.value.width,
                (videoView.value.width / videoAspectRatio).toInt()
            )

            layoutParams.setMargins(margin, margin, margin, margin)

            videoView.value.layoutParams = layoutParams

            _duration.value = mediaPlayer.duration
            _isPrepared.value = true
            if (_isPlaying.value) {
                mediaPlayer.start()
                mediaPlayer.seekTo(_currentPosition.value)
            }
        }

        mediaPlayer.setOnCompletionListener {
            _isPlaying.value = false
        }

        mediaPlayer.setOnErrorListener { _, _, _ ->
            _isPlaying.value = false
            false
        }

        mediaPlayer.setOnInfoListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    if (!mediaPlayerInitialized) {
                        _currentPosition.value = mediaPlayer.currentPosition
                        mediaPlayerInitialized = true
                    }
                }
            }
            true
        }

        mediaPlayer.setOnSeekCompleteListener {
            if (_isPlaying.value) {
                mediaPlayer.start()
            }
        }

        /*mediaPlayer.setOnVideoSizeChangedListener { _, _, _ ->
            // Reset the VideoView to trigger layout recomposition with the new video dimensions
            val videoView = _videoView.value
            videoView.layoutParams = videoView.layoutParams.apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            _videoView.value = videoView
        }

         */
    }
}
