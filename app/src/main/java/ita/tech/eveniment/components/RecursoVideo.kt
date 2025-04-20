package ita.tech.eveniment.components

import androidx.annotation.OptIn
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import ita.tech.eveniment.viewModels.RecursoVideoModel

@OptIn(UnstableApi::class)
@Composable
fun RecursoVideo(path: String) {

    val context = LocalContext.current
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

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        factory = { cont ->
            PlayerView(cont).apply {
                player = exoPlayer
                useController = false
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