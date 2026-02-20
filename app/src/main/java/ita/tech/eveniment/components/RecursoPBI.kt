package ita.tech.eveniment.components

import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RecursoPBI(
    embedUrl: String,
    embedToken: String,
    reportId: String
){
    // var webViewInstance: WebView? = null
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black),
        factory = { context ->
            WebView(context).apply {
                // Forzamos al WebView nativo a expandirse
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.evaluateJavascript("preloadReport()", null)

                        // Cuando el HTML carga, inyectamos los datos
                        view?.evaluateJavascript(
                            "loadReport('$embedUrl', '$embedToken', '$reportId')",
                            null
                        )
                    }
                }

                webViewInstance = this
                loadUrl("file:///android_asset/pbi_bridge.html")
            }
        },
        update = { webView ->
            if( webView.url != "file:///android_asset/pbi_bridge.html" ){
                webView.evaluateJavascript(
                    "loadReport('$embedUrl', '$embedToken', '$reportId')",
                    null
                )
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            println("***Limpiar pagina web")
            webViewInstance?.apply {
                evaluateJavascript("resetReport()", null)
                stopLoading()
                loadUrl("about:blank")
                clearCache(true)
                clearHistory()
                removeAllViews()
                destroy()
            }
            webViewInstance = null
        }
    }

}