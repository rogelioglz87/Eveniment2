package ita.tech.eveniment.components

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RecursoWeb(url: String) {

    var webViewInstance: WebView? = null // Para guardar la instancia del WebView

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black),
        factory = { contexto ->
            WebView(contexto).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                setInitialScale(90)
                // clearCache(true)
                // settings.textZoom = 100
                settings.blockNetworkImage = false
                settings.loadsImagesAutomatically = true

                //settings.pluginState = WebSettings.PluginState.ON
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.mediaPlaybackRequiresUserGesture = false

                // WebView settings
                fitsSystemWindows = true
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                webViewInstance = this
            }
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        },
        /*
        onRelease = { webView ->

            webView.stopLoading()
            webView.loadUrl("about:blank") // Limpia la página actual
            webView.clearHistory()
            webView.clearCache(true)

            // Es crucial remover la vista de su padre antes de destruirla
            (webView.parent as? ViewGroup)?.removeView(webView)

            webView.destroy()
        }*/
    )

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.apply {
                loadUrl("about:blank")
                stopLoading()
                clearCache(true)
                clearHistory()
                destroy()
            }
            webViewInstance = null
        }
    }

}