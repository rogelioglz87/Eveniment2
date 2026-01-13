package ita.tech.eveniment.components

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import ita.tech.eveniment.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun RecursoCCTV(path: String, isOverlay: Boolean = false){
    val context = LocalContext.current
    val paths = path.split("|")
    val composableScope = rememberCoroutineScope()

    val players = remember {
        mutableListOf<ExoPlayer>()
    }
    paths.forEach { path ->
        if(path != "")
        {
            players.add( crearExoplayer(context, path, composableScope) )
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
                /* PlayerView(cont).apply {
                    player = players[0]
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    // (this.videoSurfaceView as? SurfaceView)?.setSecure(false)
                } */

                // Funciona con X98 mini Android 11 para el monitoreo
                val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                val playerView = view as PlayerView

                if( isOverlay ){
                    (playerView.videoSurfaceView as? SurfaceView)?.apply {
                        setZOrderMediaOverlay(true)
                    }
                }

                playerView.apply {
                    player = players[0]
                    clipToOutline = true
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
                        val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                        val playerView = view as PlayerView

                        if( isOverlay ){
                            (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                setZOrderMediaOverlay(true)
                            }
                        }

                        playerView.apply {
                            player = players[0]
                            clipToOutline = true
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
                        val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                        val playerView = view as PlayerView

                        if( isOverlay ){
                            (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                setZOrderMediaOverlay(true)
                            }
                        }

                        playerView.apply {
                            player = players[1]
                            clipToOutline = true
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
                            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                            val playerView = view as PlayerView

                            if( isOverlay ){
                                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                    setZOrderMediaOverlay(true)
                                }
                            }

                            playerView.apply {
                                player = players[0]
                                clipToOutline = true
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
                            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                            val playerView = view as PlayerView

                            if( isOverlay ){
                                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                    setZOrderMediaOverlay(true)
                                }
                            }

                            playerView.apply {
                                player = players[1]
                                clipToOutline = true
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
                            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                            val playerView = view as PlayerView

                            if( isOverlay ){
                                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                    setZOrderMediaOverlay(true)
                                }
                            }

                            playerView.apply {
                                player = players[2]
                                clipToOutline = true
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
                            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                            val playerView = view as PlayerView

                            if( isOverlay ){
                                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                    setZOrderMediaOverlay(true)
                                }
                            }

                            playerView.apply {
                                player = players[0]
                                clipToOutline = true
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
                            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                            val playerView = view as PlayerView

                            if( isOverlay ){
                                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                    setZOrderMediaOverlay(true)
                                }
                            }

                            playerView.apply {
                                player = players[1]
                                clipToOutline = true
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
                            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                            val playerView = view as PlayerView

                            if( isOverlay ){
                                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                    setZOrderMediaOverlay(true)
                                }
                            }

                            playerView.apply {
                                player = players[2]
                                clipToOutline = true
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
                            val view = LayoutInflater.from(cont).inflate(R.layout.reproductor, null, false)
                            val playerView = view as PlayerView

                            if( isOverlay ){
                                (playerView.videoSurfaceView as? SurfaceView)?.apply {
                                    setZOrderMediaOverlay(true)
                                }
                            }

                            playerView.apply {
                                player = players[3]
                                clipToOutline = true
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
            composableScope.cancel()
        }
    }
}

@OptIn(UnstableApi::class)
fun crearExoplayer(context: Context, path: String, parentScope: CoroutineScope): ExoPlayer{
    val renderersFactory =
        DefaultRenderersFactory(context).forceEnableMediaCodecAsynchronousQueueing()

    // val mediaSource: MediaSource = RtspMediaSource.Factory().setForceUseRtpTcp(true).createMediaSource(MediaItem.fromUri(path))
    val mediaSource: MediaSource = RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(path))

    val mediaItem = MediaItem.Builder()
        .setUri(path)
        .build()

    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            15000, // minBufferMs: Duración mínima del búfer
            50000, // maxBufferMs: Duración máxima del búfer
            500,   // bufferForPlaybackMs: ¡LA CLAVE! Búfer necesario para INICIAR la reproducción
            500    // bufferForPlaybackAfterRebufferMs: Búfer necesario después de una pausa
        )
        .build()

    // setLoadControl( loadControl )
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

    val listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            println("Error Play: ${error}")
            // Usamos un Coroutine para esperar y luego reintentar
            // Espera 5 segundos antes de reintentar
            parentScope.launch {
                delay(5000)

                // Prepara el reproductor de nuevo para reconectar
                exoPlayer.prepare()

                // Asegúrate de que intente reproducir si la página sigue visible
                exoPlayer.playWhenReady = true
            }
        }
    }

    exoPlayer.addListener( listener )

    return exoPlayer
}