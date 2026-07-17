package com.androiddev.snsappwithcompose.feature.mediaviewer.videoviewer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.androiddev.snsappwithcompose.BuildConfig

class VideoPlayerState(
    context: Context
) {

    val player = ExoPlayer.Builder(context).build().apply {
        repeatMode = Player.REPEAT_MODE_ONE
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

            player.stop()

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


    fun release(){
        player.release()
    }
}