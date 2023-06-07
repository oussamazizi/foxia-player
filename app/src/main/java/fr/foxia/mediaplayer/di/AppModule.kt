package fr.foxia.mediaplayer.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.foxia.mediaplayer.viewmodels.MediaPlayerViewModel


@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context.applicationContext
    }

    @Provides
    fun provideMediaPlayerViewModel(context: Context): MediaPlayerViewModel {
        return MediaPlayerViewModel(context)
    }

}