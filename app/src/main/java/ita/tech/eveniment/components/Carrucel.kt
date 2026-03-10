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
    zoom_youtube: Boolean = false,
    tokensPBI: Map<Long, String> = emptyMap(),
    pbi_configuracion: String = "user"
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
                var recurso = recursos[page].obtenerDatosComoString()

                // Determinamos si utilizamos el id_usuario o idEvento para obtener el token
                val clave_power_bi = if (pbi_configuracion == "user") recursos[page].id_usuario.toLong() else recursos[page].idEvento.toLong()

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
                        embedToken = tokensPBI[clave_power_bi].toString(),
                        reportId = recursos[page].id_reporte_powerbi,
                        paginaPowerBI = recursos[page].pagina_powerbi,
                        pbi_configuracion = pbi_configuracion
                    )
                }

            }
        }
    } else {
        RecursoImagen(rutaImagen = imgDefault, context = context)
    }
}