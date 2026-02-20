package ita.tech.eveniment.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.util.parseStringToObject
import ita.tech.eveniment.viewModels.CarrucelViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(UnstableApi::class)
@Composable
fun Carrucel(
    recursosOrigin: List<InformacionRecursoModel>,
    imgDefault: String,
    timeZone: String,
    onTipoSlideChange: (String) -> Unit,
    isOverlay: Boolean = false,
    colorSecundario: Color = Color.Black,
    textoAgrupado: String = "si",
    plantilla: Int = 1,
    zoom_youtube: Boolean = false
){
    val context = LocalContext.current

    //-- Instanciamos el ViewModel
    val carrucelVM: CarrucelViewModel = remember { CarrucelViewModel() }

    //-- Comenzamos con el filtado de recursos
    LaunchedEffect(recursosOrigin) {
        carrucelVM.iniciarFiltrado(recursosOrigin, timeZone)
    }

    //-- Observamos la lista filtrada del ViewModel
    val recursos by carrucelVM.listaFiltrada.collectAsState()

    val scope = rememberCoroutineScope()
    // val context = LocalContext.current

    val pagerState = rememberPagerState( pageCount = { recursos.size }, initialPage = 0 )

    //-- Validamos si el elemento que se esta mostrando es Eliminado de pantalla por fecha de vencimiento
    //-- reiniciamos el contador 'tiempoTranscurrido' del viewModel
    LaunchedEffect(recursos.getOrNull(pagerState.currentPage)?.idEvento) {
        val recursoindex = recursos.getOrNull(pagerState.currentPage)
        if( recursoindex != null ){
            carrucelVM.detener()
        }
    }

    LaunchedEffect(pagerState.currentPage, recursos) {
        if( recursos.isNotEmpty() ) {
            val paginaActual = pagerState.currentPage
            val paginaSiguiente = (paginaActual + 1) % recursos.size

            //-- Almacena el tipo de Slide
            carrucelVM.setTiposlide(recursos[paginaActual].tipo_slide)
            onTipoSlideChange( recursos[paginaActual].tipo_slide )

            //-- Obtiene la duracion de la primera diapositiva
            carrucelVM.setDuracionRecursoActual(recursos[paginaActual].duracion.toLong())

            //-- Activa el carrucel
            carrucelVM.activarCarrucel(
                onDuracionFinalizada = {
                    //-- Mostramos la siguiente diapositiva o regresamos al punto inicial.
                    scope.launch {
                        if (paginaSiguiente > 0) {
                            // pagerState.animateScrollToPage(paginaSiguiente, animationSpec = tween(1500))
                            // pagerState.animateScrollToPage(paginaSiguiente)
                            pagerState.scrollToPage(paginaSiguiente)
                        } else {
                            pagerState.scrollToPage(0)
                        }
                    }
                }
            )
        }
    }

    DisposableEffect(true) {
        onDispose {
            carrucelVM.detener()
            carrucelVM.detenerFiltroLista()
        }
    }

    if (recursos.isNotEmpty()) {
        HorizontalPager(
            state = pagerState,
            key = { page -> recursos[page].idEvento }
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = Color.Black)
            ) {
                // Definimos el Tipo de recuso a mostrar (Imagen, Video, Youtube, Pagina Web etc...)
                // val recurso = recursos[page].datos.toString()
                var recurso = recursos[page].obtenerDatosComoString()

                if (recursos[page].tipo_slide == "imagen") {
                    RecursoImagen(rutaImagen = recurso, context = context)
                } else if (recursos[page].tipo_slide == "video") {
                    RecursoVideo(
                        recurso,
                        isCurrentlyVisible = (pagerState.currentPage == page),
                        recursos.size,
                        isOverlay = isOverlay
                    )
                } else if (recursos[page].tipo_slide == "cctv") {
                    RecursoCCTV(path = recurso, isOverlay = isOverlay)
                } else if (recursos[page].tipo_slide == "pagina_web") {
                    RecursoWeb(url = recurso)
                } else if (recursos[page].tipo_slide == "youtube" && (recursos[page].tipo_video_youtube == "video" || recursos[page].tipo_video_youtube == "en_directo")) {
                    RecursoYouTube(videoId = recurso, zoom_youtube)
                } else if (recursos[page].tipo_slide == "youtube" && recursos[page].tipo_video_youtube == "lista_reproduccion") {
                    RecursoYouTubeLista(recurso, zoom_youtube)
                } else if (recursos[page].tipo_slide == "nas") {
                    RecursoListaVideos(
                        recurso,
                        recursos[page].recursos_nas,
                        isCurrentlyVisible = (pagerState.currentPage == page),
                        recursos.size,
                        isOverlay = isOverlay
                    )
                } else if(recursos[page].tipo_slide == "texto"){
                    RecursoTexto(
                        recursos = recursos[page].obtenerDatosComoListaAgenda(),
                        colorSecundario = colorSecundario,
                        textoAgrupado = textoAgrupado,
                        plantilla = plantilla
                    )
                } else if(recursos[page].tipo_slide == "powerbi"){
                    RecursoPBI(
                        embedUrl = recurso,
                        embedToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6InNNMV95QXhWOEdWNHlOLUI2ajJ4em1pazVBbyIsImtpZCI6InNNMV95QXhWOEdWNHlOLUI2ajJ4em1pazVBbyJ9.eyJhdWQiOiJodHRwczovL2FuYWx5c2lzLndpbmRvd3MubmV0L3Bvd2VyYmkvYXBpIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvM2NjMzBmNzktZTg0YS00YzQyLTk5NTQtOWMxZjczZDIxNjU3LyIsImlhdCI6MTc3MTYwNjg4NywibmJmIjoxNzcxNjA2ODg3LCJleHAiOjE3NzE2MTIwNTcsImFjY3QiOjAsImFjciI6IjEiLCJhaW8iOiJBWlFBYS84YkFBQUFQUUJORlVFSGozNlJ5ZVl2bWpIbmRHT3F6cU9tZDc2MEpyRHgyK054T09wMmVuL3lzbmNLUUxucXZtZ1ZVOTd0Y0RyMkMzTHE4R3c4Q0U1cFpGL3cwZVQ3YUZHMUhHK0l0SCsxSXd3aDI0Uk5kMUFuQW1TTFdFSDlnajR5MG8vYWVXa1htaDd2NTZYeHNCWitNenZxeFJ2VXhXRm9KUlNqUmpiRklrRDFURWJTcDcvM2xZNTNpTkZyd0t1ZE4wL1giLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMGZiMGI0ZTgtMjg2ZC00M2E5LWJhOGUtYzc5ODc1ZDE4ZmU3IiwiYXBwaWRhY3IiOiIxIiwiZmFtaWx5X25hbWUiOiJHb256YWxleiBSb21lcm8iLCJnaXZlbl9uYW1lIjoiUm9nZWxpbyIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjE4OS4yMzUuMTg4Ljk1IiwibmFtZSI6IlJvZ2VsaW8gR29uemFsZXogUm9tZXJvIiwib2lkIjoiZjdmNjYwMzYtMjAxYi00ZGU5LTlhZDQtNTNmYWE0N2RhZTEzIiwicHVpZCI6IjEwMDMyMDA1OEIwRjcwMDUiLCJyaCI6IjEuQVN3QWVRX0RQRXJvUWt5WlZKd2ZjOUlXVndrQUFBQUFBQUFBd0FBQUFBQUFBQUFCQU5jc0FBLiIsInNjcCI6IlBhZ2luYXRlZFJlcG9ydC5SZWFkLkFsbCBSZXBvcnQuUmVhZC5BbGwgV29ya3NwYWNlLlJlYWQuQWxsIiwic2lkIjoiMDAxZmViZmEtNWY4MS0xYWNiLTY4OTQtM2I0NjgwNWM5M2QxIiwic2lnbmluX3N0YXRlIjpbImttc2kiXSwic3ViIjoiVVFuVDFaV1BHZWdOREUyRmF4RlE0bG9ycDZlY01iZ3Y1dnh1dEY5SE1hYyIsInRpZCI6IjNjYzMwZjc5LWU4NGEtNGM0Mi05OTU0LTljMWY3M2QyMTY1NyIsInVuaXF1ZV9uYW1lIjoicmdvbnphbGV6QGl0YXZhbnphZGEub25taWNyb3NvZnQuY29tIiwidXBuIjoicmdvbnphbGV6QGl0YXZhbnphZGEub25taWNyb3NvZnQuY29tIiwidXRpIjoiT3o3amp4Vkk1MFNGUHEtTV9MSWFBQSIsInZlciI6IjEuMCIsIndpZHMiOlsiNjJlOTAzOTQtNjlmNS00MjM3LTkxOTAtMDEyMTc3MTQ1ZTEwIiwiYjc5ZmJmNGQtM2VmOS00Njg5LTgxNDMtNzZiMTk0ZTg1NTA5Il0sInhtc19hY3RfZmN0IjoiMyA5IiwieG1zX2Z0ZCI6InNBeHM0aDVXRW5LcFBVSzV2NVVUUlJBTk1Ka0pWZ2lyNmpHelBvd1RIQThCZFhOdWIzSjBhQzFrYzIxeiIsInhtc19pZHJlbCI6IjEgMiIsInhtc19zdWJfZmN0IjoiOCAzIn0.eaTIJHO7iG2XuoDLUqo4nB-bHtPUBFIPCdYDarSE5zWDNa38TLCm3KkyANz43am3yuZLHZruzfrPzODrQ-wuDgNuFi9K3ofFhorMnOOZOlf7bvQNY2LQkX93RRRZDEEANFprEuraXhR5xKUjrzHugG6ef8bU5_T-eCn9cGtH5xxeu7BbXsfXUTVrFYAZncHpZvWY7Sa4gNQAJ-NsU-Ggay-G2q4uuA8HO5MXz2F8_ICloTrCfxoT2hlzxzdCjgPAxLVTPgEKzlJfkQYJvT9z0rl_1vHY5Vf8ebiw7Nf22pZI8R28P2T94tv5mtDziLltKH2p1WJY1fgykzzzu9QnQw",
                        reportId = recursos[page].id_reporte_powerbi,
                    )
                }

            }
        }
    } else {
        RecursoImagen(rutaImagen = imgDefault, context = context)
    }
}