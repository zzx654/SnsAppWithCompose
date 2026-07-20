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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VideoPlayerState(
    context: Context
) {
    var currentPosition by mutableStateOf(0L)
        private set

    var duration by mutableStateOf(0L)
        private set
    var wasPlayingBeforeLifecyclePause = false
        private set
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
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
        startProgressUpdate()
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
            currentPosition = 0L
            duration = 0L

            player.setMediaItem(
                MediaItem.fromUri(newUrl)
            )

            player.prepare()

        }

        player.play()
    }
    private fun startProgressUpdate() {

        scope.launch {

            while (isActive) {

                val safeDuration =
                    if (player.duration > 0)
                        player.duration
                    else
                        0L

                duration = safeDuration

                currentPosition =
                    player.currentPosition.coerceAtMost(safeDuration)

                delay(200)
            }
        }
    }

    fun setPlaying(isPlaying:Boolean){

        if(isPlaying){
            player.play()
        }else{
            player.pause()
        }

    }
    fun seekTo(position: Long) {
        player.seekTo(position)
        currentPosition = position

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


    fun pauseByLifecycle(){

        wasPlayingBeforeLifecyclePause = player.isPlaying

        player.pause()
    }


    fun resumeByLifecycle(){

        if(wasPlayingBeforeLifecyclePause){
            player.play()
        }
    }
    fun release() {

        scope.cancel()

        player.removeListener(listener)
        player.release()

    }
}