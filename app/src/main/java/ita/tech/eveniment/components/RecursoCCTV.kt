package ita.tech.eveniment.components

import android.net.Uri
import android.view.LayoutInflater
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import ita.tech.eveniment.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun RecursoCCTV(
    path: String,
    isCurrentlyVisible: Boolean,
    isOverlay: Boolean = false,
    uniqueKey: Any,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    var exoPlayer by remember(uniqueKey) { mutableStateOf<ExoPlayer?>(null) }

    //-- En caso de error de conexión, intentamos reconectar
    val reintentoJob = remember { mutableStateOf<Job?>(null) }
    val listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            println("Error Play: $error")

            //-- Cancelamos intentos previos
            reintentoJob?.value?.cancel()

            //-- Intento de reconexion
            reintentoJob?.value = composableScope.launch {
                delay(5000)
                if(isCurrentlyVisible) {
                    exoPlayer?.prepare()
                    exoPlayer?.playWhenReady = true
                }
            }
        }
    }

    LaunchedEffect(isCurrentlyVisible, uniqueKey) {
        if(isCurrentlyVisible)
        {
            val newPlayer = ExoPlayer.Builder(context)
                .setRenderersFactory( DefaultRenderersFactory(context).forceEnableMediaCodecAsynchronousQueueing() )
                .build().apply {
                    stop()
                    clearMediaItems()
                    addListener( listener )
                    setMediaItem(MediaItem.fromUri(Uri.parse(path)))
                    prepare()
                    playWhenReady = true
            }
            exoPlayer = newPlayer
        }
        else
        {
            reintentoJob?.value?.cancel()
            exoPlayer?.removeListener(listener)
            exoPlayer?.playWhenReady = false
            exoPlayer?.stop()
            exoPlayer?.clearMediaItems()
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { cont ->
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

    DisposableEffect(uniqueKey) {
        onDispose {
            reintentoJob?.value?.cancel()
            exoPlayer?.playWhenReady = false
            exoPlayer?.let {
                it.removeListener(listener)
                it.stop()
                it.clearMediaItems()
                it.release()
            }
            exoPlayer = null
        }
    }
}