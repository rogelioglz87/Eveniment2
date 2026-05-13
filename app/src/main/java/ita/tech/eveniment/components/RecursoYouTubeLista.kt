package ita.tech.eveniment.components

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun RecursoYouTubeLista(
    playlistId: String,
    zoom_youtube: Boolean = false
){
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    // Creamos un WebViewClient personalizado para evitar que la app se cierre
    val safeWebViewClient = object : WebViewClient() {
        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
            Log.e("YouTubeError", "El proceso de renderizado de YouTube se perdió. ¿Cerró por crash?: ${detail?.didCrash()}")
            // Retornamos true para que no se cierre la app y tengamos el control de la excepcion
            return true
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
        factory = {

            YouTubePlayerView(context).apply {

                enableAutomaticInitialization = false

                lifecycleOwner.lifecycle.addObserver(this)

                // Accedemos al WebView interno para darle seguridad
                post {

                    val webView = findWebViewRecursivo()

                    if (webView != null) {
                        webView.webViewClient = safeWebViewClient
                        Log.e("YouTubeError", "WebView encontrado")
                    } else {
                        Log.e("YouTubeError", "No se encontró el WebView interno")
                    }

                }

                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Se ejecuta cuando el Composable entra en la composición
                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.play()
                    }
                }

                val options = IFramePlayerOptions.Builder(context)
                    .controls(0)           // Oculta los controles de reproducción
                    .ivLoadPolicy(3)   // Oculta las anotaciones
                    // .ccLoadPolicy(1)             // Oculta las anotaciones
                    .rel(0)                    // No muestra videos relacionados al final
                    .listType("playlist")
                    .list(playlistId)
                    .build()

                try {
                    initialize(listener, false, options)
                }catch (e: Exception){
                    Log.e("YouTubeError", "Ya estaba inicializado: ${e.message}")
                }

            }

        },
        onRelease = { view ->
            lifecycleOwner.lifecycle.removeObserver(view)
            view.release()
        }
    )
}

fun View.findWebViewRecursivo(): WebView? {
    if (this is WebView) return this
    if (this !is ViewGroup) return null
    for (i in 0 until childCount) {
        val resultado = getChildAt(i).findWebViewRecursivo()
        if (resultado != null) return resultado
    }
    return null
}