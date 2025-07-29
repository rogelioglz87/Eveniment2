package ita.tech.eveniment.components

import androidx.annotation.OptIn
import android.net.Uri
import android.view.LayoutInflater
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import ita.tech.eveniment.R
import ita.tech.eveniment.viewModels.RecursoVideoModel

@OptIn(UnstableApi::class)
@Composable
fun RecursoVideo(path: String, isCurrentlyVisible: Boolean) {

    val context = LocalContext.current

    /*
    val exoPlayer = remember {
        val renderersFactory =
            DefaultRenderersFactory(context).forceEnableMediaCodecAsynchronousQueueing()
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            stop()
            clearMediaItems()
            setMediaItem( MediaItem.fromUri(Uri.parse(path)) )
            prepare()
            playWhenReady = true
            play()
        }
    }
    */
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(isCurrentlyVisible) {
        if(isCurrentlyVisible)
        {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(path)))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        }
        else
        {
            exoPlayer.playWhenReady = false
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }
    }


    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        factory = { cont ->
            /*
            PlayerView(cont).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                // (this.videoSurfaceView as? SurfaceView)?.setSecure(false)
            }
            */
            // Funciona con X98 mini Android 11 para el monitoreo
            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
            (view as PlayerView).apply {
                player = exoPlayer
                clipToOutline = true
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            println("***----Salir del reproductor")
            exoPlayer.playWhenReady = false
            exoPlayer.release()
        }
    }
}