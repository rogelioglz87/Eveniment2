package ita.tech.eveniment.components

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView

@Composable
fun RecursoCCTV(path: String){
    val context = LocalContext.current
    val paths = path.split("|");

    val players = remember {
        mutableListOf<ExoPlayer>()
    }
    paths.forEach { path ->
        if(path != "")
        {
            players.add( crearExoplayer(context, path) )
        }
    }

    //-- Diseño
    if(players.size == 1)
    {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            factory = { cont ->
                PlayerView(cont).apply {
                    player = players[0]
                    useController = false
                }
            }
        )
    }
    else if(players.size == 2)
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    factory = { cont ->
                        PlayerView(cont).apply {
                            player = players[0]
                            useController = false
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    factory = { cont ->
                        PlayerView(cont).apply {
                            player = players[1]
                            useController = false
                        }
                    }
                )
            }
        }
    }
    else if (players.size == 3){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        factory = { cont ->
                            PlayerView(cont).apply {
                                player = players[0]
                                useController = false
                                keepScreenOn = true
                            }
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        factory = { cont ->
                            PlayerView(cont).apply {
                                player = players[1]
                                useController = false
                                keepScreenOn = true
                            }
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        factory = { cont ->
                            PlayerView(cont).apply {
                                player = players[2]
                                useController = false
                                keepScreenOn = true
                            }
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {  }
            }
        }
    }
    else if (players.size == 4){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        factory = { cont ->
                            PlayerView(cont).apply {
                                player = players[0]
                                useController = false
                                keepScreenOn = true
                            }
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        factory = { cont ->
                            PlayerView(cont).apply {
                                player = players[1]
                                useController = false
                                keepScreenOn = true
                            }
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        factory = { cont ->
                            PlayerView(cont).apply {
                                player = players[2]
                                useController = false
                                keepScreenOn = true
                            }
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        factory = { cont ->
                            PlayerView(cont).apply {
                                player = players[3]
                                useController = false
                                keepScreenOn = true
                            }
                        }
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            players.forEach { exoplayer ->
                exoplayer.playWhenReady = false
                exoplayer.release()
            }
        }
    }
}

@OptIn(UnstableApi::class)
fun crearExoplayer(context: Context, path: String): ExoPlayer{
    val renderersFactory =
        DefaultRenderersFactory(context).forceEnableMediaCodecAsynchronousQueueing()
    val mediaSource: MediaSource =
        RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(path))
    val exoPlayer =
        ExoPlayer.Builder(context, renderersFactory).build().apply {
            stop()
            clearMediaItems()
            setMediaItem( MediaItem.fromUri(Uri.parse(path)) )
            // setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
            play()
        }
    return exoPlayer
}