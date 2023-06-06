package fr.oussama.mediaplayer.viewmodels

import android.content.Context
import android.media.MediaPlayer
import android.widget.VideoView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.SavedStateHandle
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

    private val _isPrepared = MutableStateFlow( false)
    val isPrepared: StateFlow<Boolean>
        get() = _isPrepared

    private val _mediaPlayer = MutableStateFlow(MediaPlayer())
    val mediaPlayer: StateFlow<MediaPlayer>
        get() = _mediaPlayer

    private val _videoView = MutableStateFlow(VideoView(context))
    val videoView: StateFlow<VideoView>
        get() = _videoView

    init {
        _isPlaying.value = true
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
}
