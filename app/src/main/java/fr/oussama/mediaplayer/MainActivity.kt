package fr.oussama.mediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import fr.oussama.mediaplayer.ui.screens.MediaPlayerScreen
import fr.oussama.mediaplayer.ui.theme.MediaplayerTheme
import fr.oussama.mediaplayer.viewmodels.MediaPlayerViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaplayerTheme {
                val mediaPlayerViewModel = ViewModelProvider(this)[MediaPlayerViewModel::class.java]
                // A surface container using the 'background' color from the theme
                MediaPlayerScreen(
                    videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    viewModel = mediaPlayerViewModel,
                )
            }
        }
    }
}
