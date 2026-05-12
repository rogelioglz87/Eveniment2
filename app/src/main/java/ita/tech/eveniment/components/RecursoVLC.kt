package ita.tech.eveniment.components

import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

@Composable
fun RecursoVLC(
    path: String,
    isOverlay: Boolean = false,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    var retryJob by remember { mutableStateOf<Job?>(null) }


    val vlcObjects = remember() {
        val opciones = arrayListOf(
            "--rtsp-tcp",
            "--file-caching=1000",
            "--network-caching=1000",
            "--clock-jitter=0",
            "--clock-synchro=0",
            "--no-stats",            // Desactivar estadísticas ahorra ciclos de CPU
            "--no-video-title-show", // Evita overlays
            "--avcodec-hw=none",
            "--no-osd",
            "--ipv4",                // Forzar IPv4 para evitar el error de "invalid IP 0.0.0.0"
        )

        // Verificamos si es Android 12 (API 31), 12L (API 32) o 13 (API 33)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S ) { // && Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU
            opciones.add("--vout=android-display") // SE UTILIZA PARA ANDROID 12 (X98H)
        }

        val libVLC = LibVLC(context, opciones)
        val mediaPlayer = MediaPlayer(libVLC)

        // Retornamos un holder para limpiar ambos después
        object {
            val lib = libVLC
            val player = mediaPlayer
        }
    }

    DisposableEffect(path) {
        val listener = MediaPlayer.EventListener { event ->
            // VLC lanza EncounteredError o EndReached cuando se cae el stream RTSP
            if (event.type == MediaPlayer.Event.EncounteredError || event.type == MediaPlayer.Event.EndReached) {
                println("VLC Error de red detectado. Reintentando en 5s... ($path)")

                retryJob?.cancel() // Cancelamos si ya había un reintento en curso
                retryJob = composableScope.launch {
                    delay(5000)

                    // Detenemos el player actual por seguridad
                    vlcObjects.player.stop()

                    // Recreamos el objeto Media. VLC lo exige después de un error.
                    val media = Media(vlcObjects.lib, Uri.parse(path)).apply {
                        addOption(":network-caching=300")
                        addOption(":clock-jitter=0")
                        addOption(":clock-synchro=0")
                        addOption(":codec=all")
                        addOption(":rtsp-close-tcp")
                    }

                    // Asignamos y reproducimos
                    vlcObjects.player.media = media
                    media.release()
                    vlcObjects.player.play()
                }
            }
        }

        // Agregamos el listener al reproductor
        vlcObjects.player.setEventListener(listener)

        onDispose {
            try {
                retryJob?.cancel()
                if( vlcObjects.player.isPlaying ){
                    vlcObjects.player.stop()
                }
                vlcObjects.player.setEventListener(null) // Limpiar listeners para evitar fugas
                vlcObjects.player.detachViews()
                vlcObjects.player.release()
                vlcObjects.lib.release()
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            VLCVideoLayout(ctx).also { layout ->
                vlcObjects.player.attachViews(layout, null, false, false)

                val media = Media(vlcObjects.lib, Uri.parse(path)).apply {
                    addOption(":network-caching=1500")
                    addOption(":clock-jitter=0")
                    addOption(":clock-synchro=0")
                    addOption(":codec=all") // Permite que VLC use cualquier decodificador disponible
                    addOption(":rtsp-close-tcp")
                    addOption(":no-video-title-show")
                    addOption(":rtsp-tcp") // Forzar TCP para estabilidad
                    addOption(":no-drop-late-frames")
                    addOption(":no-skip-frames")
                }
                vlcObjects.player.media = media
                media.release() // Media se libera inmediatamente después de asignarse al player
                vlcObjects.player.play()

            }
        },
        update = {  layout ->
            if (layout.layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT) {
                layout.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // 2. Forzar a VLC a recalcular el aspecto de la superficie de video
            vlcObjects.player.videoScale = MediaPlayer.ScaleType.SURFACE_FILL

            vlcObjects.player.aspectRatio = null
        }
    )
}