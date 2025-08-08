package ita.tech.eveniment.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun RecursoYouTube(
    videoId: String
){

    val lifecycleOwner = LocalLifecycleOwner.current

    val playerViewInstance = remember { mutableStateOf<YouTubePlayerView?>(null) }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        factory = { context ->
            YouTubePlayerView(context).apply {
                Log.d("ID VIDEO 1", videoId)
                this.enableAutomaticInitialization = false

                // Guardamos la instancia para el DisposableEffect
                playerViewInstance.value = this

                // Añadimos el observador del ciclo de vida
                lifecycleOwner.lifecycle.addObserver(this)

                // Inicializar el reproductor
                /*
                addYouTubePlayerListener(object: AbstractYouTubePlayerListener(){
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        super.onReady(youTubePlayer)
                        youTubePlayer.loadVideo(videoId, 0f)
                        youTubePlayer.play()

                    }
                })
                */

                // Opciones del reproductor (opcional, pero útil)
                val playerOptions = IFramePlayerOptions.Builder()
                    .controls(1) // Mostrar controles del reproductor
                    .build()

                this.initialize(object : AbstractYouTubePlayerListener(){
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        super.onReady(youTubePlayer)
                        youTubePlayer.loadVideo(videoId, 0f)
                        youTubePlayer.play()
                    }
                }, playerOptions)
                // youTubePlayerView = this
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
/*
            youTubePlayerView?.release()
            youTubePlayerView = null
*/

            playerViewInstance.value?.apply {
                lifecycleOwner.lifecycle.removeObserver(this)
                release()
            }
        }
    }
}