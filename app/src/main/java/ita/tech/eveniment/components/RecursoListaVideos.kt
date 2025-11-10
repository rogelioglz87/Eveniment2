package ita.tech.eveniment.components

import android.net.Uri
import android.view.LayoutInflater
import androidx.annotation.OptIn
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
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import ita.tech.eveniment.R
import ita.tech.eveniment.util.obtener_path
import ita.tech.eveniment.util.parseObjectToArray
import ita.tech.eveniment.util.parseStringToObject

@OptIn(UnstableApi::class)
@Composable
fun RecursoListaVideos(
    path: String,
    rutasDeVideos: String,
    isCurrentlyVisible: Boolean,
    totalRecursos: Int = 0
){
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(isCurrentlyVisible) {
        val listaVideos = parseStringToObject(rutasDeVideos)
        val tipoReproduccion = listaVideos.getString("tipo_reproduccion")
        val path_origin = obtener_path( path )
        val rutasDeVideos = parseObjectToArray(parseStringToObject(listaVideos.getString("data")), path_origin)

        val mediaItems = rutasDeVideos.map { recurso ->
            if(recurso.esVideo){
                MediaItem.fromUri(Uri.parse(recurso.path))
            }
            else{
                // Para las imágenes, añadimos '?duration=' a la URI
                MediaItem.Builder().setUri(recurso.path).setImageDurationMs(recurso.duracion*1000).build()
            }
        }

        if(isCurrentlyVisible)
        {
            exoPlayer.setMediaItems(mediaItems)
            if(tipoReproduccion == "aleatorio"){
                exoPlayer.shuffleModeEnabled = true
            }
            exoPlayer.prepare()
            exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
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
            println("*** Finaliza lista de videos")
            exoPlayer.playWhenReady = false
            exoPlayer.release()
        }
    }

}