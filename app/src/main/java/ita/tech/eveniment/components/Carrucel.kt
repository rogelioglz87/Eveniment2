package ita.tech.eveniment.components


import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(UnstableApi::class)
@Composable
fun Carrucel(
    recursos: List<InformacionRecursoModel>,
    imgDefault: String,
    onTipoSlideChange: (String) -> Unit
){
    val carrucelVM: CarrucelViewModel = remember { CarrucelViewModel() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState( pageCount = { recursos.size }, initialPage = 0 )

    LaunchedEffect(pagerState.currentPage) {
        if( recursos.isNotEmpty() ) {
            val paginaActual = pagerState.currentPage
            val paginaSiguiente = paginaActual + 1

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

                        if (paginaSiguiente < recursos.size) {
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
        }
    }

    if( recursos.isNotEmpty() ){
        HorizontalPager(
            state = pagerState,
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = Color.Black)
                    // Aplica el efecto de fade con graphicsLayer
                    /*
                    .graphicsLayer {
                        // Calcula el desplazamiento de la página
                        val pageOffset = pagerState.currentPage - page + pagerState.currentPageOffsetFraction
                        // Calcula el valor alfa para el efecto de fade
                        val alpha = lerp(
                            start = 0f, // Opacidad mínima
                            stop = 1f,    // Opacidad máxima
                            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                        )

                        // Aplica la opacidad a la página
                        this.alpha = alpha
                    }
                    */
            ) {
                // Definimos el Tipo de recuso a mostrar (Imagen, Video, Youtube, Pagina Web etc...)
                val recurso = recursos[page].datos.toString()

                if (recursos[page].tipo_slide == "imagen") {
                    RecursoImagen(rutaImagen = recurso, context = context)
                } else if (recursos[page].tipo_slide == "video") {
                    RecursoVideo(recurso, isCurrentlyVisible = (pagerState.currentPage == page) )
                } else if (recursos[page].tipo_slide == "cctv") {
                    RecursoCCTV(path = recurso)
                } else if (recursos[page].tipo_slide == "pagina_web") {
                    RecursoWeb(url = recurso)
                } else if(recursos[page].tipo_slide == "youtube"){
                    Log.d("ID VIDEO", recurso)
                    RecursoYouTube(videoId = recurso)
                }

            }
        }
    }
    else{
        RecursoImagen(rutaImagen = imgDefault, context = context)
    }
}