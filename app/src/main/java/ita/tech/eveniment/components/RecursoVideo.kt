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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import ita.tech.eveniment.R
import ita.tech.eveniment.viewModels.RecursoVideoModel

@OptIn(UnstableApi::class)
@Composable
fun RecursoVideo(path: String, isCurrentlyVisible: Boolean, totalRecursos: Int = 0, isOverlay: Boolean = false) {

    val context = LocalContext.current

    // val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    LaunchedEffect(isCurrentlyVisible) {
        if(isCurrentlyVisible)
        {
            val newPlayer = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(path)))
                prepare()
                repeatMode = if (totalRecursos == 1) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                playWhenReady = true
            }
            exoPlayer = newPlayer
        }
        else
        {
            exoPlayer?.playWhenReady = false
            exoPlayer?.stop()
            exoPlayer?.clearMediaItems()
            exoPlayer?.release()
            exoPlayer = null
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
            val playerView = view as PlayerView

            if( isOverlay ){
                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                    setZOrderMediaOverlay(true)
                }
            }

            playerView.apply {
                player = exoPlayer
                clipToOutline = true
            }
        },
        update = { view ->
            view.player = exoPlayer
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            println("***----Salir del reproductor")
            exoPlayer?.playWhenReady = false
            exoPlayer?.let {
                it.stop()
                it.clearMediaItems()
                it.release()
            }
            exoPlayer = null
        }
    }
}