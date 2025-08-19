package ita.tech.eveniment.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun RecursoYouTube(
    videoId: String
){
    val context = LocalContext.current

    val youTubePlayerView = remember { YouTubePlayerView(context).apply {
        this.enableAutomaticInitialization = false
    } }
    // val youTubePlayerView = remember { YouTubePlayerView(context) }

    DisposableEffect(videoId) {
        // Se ejecuta cuando el Composable entra en la composición
/*
        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
*/
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        }

        val options = IFramePlayerOptions.Builder()
            .controls(0)          // Oculta los controles de reproducción
            .ivLoadPolicy(3)      // Oculta las anotaciones
            .ccLoadPolicy(1)      // Oculta las anotaciones
            .rel(0)               // No muestra videos relacionados al final
            .build()

        youTubePlayerView.initialize(listener, false, options)

        onDispose {
            youTubePlayerView.release()
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        factory = { youTubePlayerView }
    )
}