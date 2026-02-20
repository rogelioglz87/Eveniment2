package ita.tech.eveniment.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun RecursoYouTube(
    videoId: String,
    zoom_youtube: Boolean = false
){
    val context = LocalContext.current

    val youTubePlayerView = remember(videoId)
    {
        YouTubePlayerView(context).apply {
            this.enableAutomaticInitialization = false

            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    DisposableEffect(videoId) {
        // Se ejecuta cuando el Composable entra en la composición
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        }

        val options = IFramePlayerOptions.Builder(context)
            .controls(0)          // Oculta los controles de reproducción
            .ivLoadPolicy(3)      // Oculta las anotaciones
            .rel(0)               // No muestra videos relacionados al final
            .build()

        try{
            youTubePlayerView.initialize(listener, false, options)
        }catch (e: Exception){
            Log.e("YouTubeError", "Ya estaba inicializado: ${e.message}")
        }

        onDispose {
            youTubePlayerView.release()
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer {
                val escala = if (zoom_youtube) 1.22f else 1.0f
                scaleX = escala
                scaleY = escala
            },
        factory = { youTubePlayerView }
    )
}
