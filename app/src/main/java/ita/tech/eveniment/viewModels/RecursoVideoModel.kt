package ita.tech.eveniment.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.annotation.OptIn
import androidx.appcompat.widget.ListPopupWindow.MATCH_PARENT
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecursoVideoModel: ViewModel() {

    var exoPlayer: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    fun inicializePlayer(context: Context){
        if(exoPlayer == null){
            // val rendersFactory = DefaultRenderersFactory(context).forceEnableMediaCodecAsynchronousQueueing()
            exoPlayer = ExoPlayer.Builder(context).build()
        }
    }

    fun releasePlayer(){
        exoPlayer?.playWhenReady = false
        exoPlayer?.release()
        exoPlayer = null
    }

    fun playVideo(path: String){
        println("***Play vide: $path")
        exoPlayer?.let { player ->
            player.apply {
                stop()
                clearMediaItems()
                setMediaItem( MediaItem.fromUri(Uri.parse(path)) )
                prepare()
                playWhenReady = true
                play()
            }

        }
    }

    @OptIn(UnstableApi::class)
    fun playerViewBuild(context: Context): PlayerView {
        val playerView = PlayerView(context).apply {
            player = exoPlayer
            useController = false
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
        return playerView
    }


}
