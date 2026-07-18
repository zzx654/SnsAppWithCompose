package com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.androiddev.snsappwithcompose.BuildConfig
import com.androiddev.snsappwithcompose.common.util.Constants.SEEK_TIME

class VideoPlayerState(
    context: Context
) {

    var isBuffering by mutableStateOf(false)
        private set
    val player = ExoPlayer.Builder(context).build().apply {
        repeatMode = Player.REPEAT_MODE_ONE
    }
    private val listener =
        object : Player.Listener {

            override fun onPlaybackStateChanged(
                state: Int
            ) {

                isBuffering =
                    state == Player.STATE_BUFFERING

            }
        }

    init {
        player.addListener(listener)
    }




    fun play(
        url:String
    ){

        val currentUrl =
            player.currentMediaItem
                ?.localConfiguration
                ?.uri
                ?.toString()


        val newUrl = BuildConfig.BASE_URL + url


        if(currentUrl != newUrl){


            player.setMediaItem(
                MediaItem.fromUri(newUrl)
            )

            player.prepare()

        }

        player.play()
    }


    fun setPlaying(isPlaying:Boolean){

        if(isPlaying){
            player.play()
        }else{
            player.pause()
        }

    }

    fun skipForward() {

        val newPosition =
            (player.currentPosition + SEEK_TIME)
                .coerceAtMost(player.duration)

        player.seekTo(newPosition)
        player.play()
    }

    fun skipBackward() {

        val newPosition =
            (player.currentPosition - SEEK_TIME)
                .coerceAtLeast(0L)

        player.seekTo(newPosition)
        player.play()
    }

    fun release(){

        player.removeListener(listener)
        player.release()

    }
}