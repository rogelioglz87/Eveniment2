package ita.tech.eveniment.components


import androidx.annotation.OptIn
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun Carrucel(
    carrucelVM: CarrucelViewModel,
    recursos: List<InformacionRecursoModel>
){

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState( pageCount = { recursos.size }, initialPage = 0 )

    LaunchedEffect(pagerState.currentPage) {
        val paginaActual =  pagerState.currentPage
        val paginaSiguiente = paginaActual + 1

        //-- Obtiene la duracion de la primera diapositiva
        carrucelVM.setDuracionRecursoActual(recursos[paginaActual].duracion.toLong())

        //-- Activa el carrucel
        carrucelVM.activarCarrucel(
            onDuracionFinalizada = {
                //-- Mostramos la siguiente diapositiva o regresamos al punto inicial.
                scope.launch {

                    if(paginaSiguiente < recursos.size){
                        // pagerState.animateScrollToPage(paginaSiguiente, animationSpec = tween(1500))
                        // pagerState.animateScrollToPage(paginaSiguiente)
                        pagerState.scrollToPage(paginaSiguiente)

                    }else{
                        pagerState.scrollToPage(0)
                    }
                }
            }
        )
    }

    DisposableEffect(true) {
        onDispose {
            carrucelVM.detener()
        }
    }

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
        ) {
            // Definimos el Tipo de recuso a mostrar (Imagen, Video, Youtube, Pagina Web etc...)
            val recurso = recursos[page].datos.toString()

            if (recursos[page].tipo_slide == "imagen") {
                RecursoImagen(rutaImagen = recurso, context = context)
            } else if (recursos[page].tipo_slide == "video") {
                RecursoVideo(recurso)
            } else if (recursos[page].tipo_slide == "cctv") {
                RecursoCCTV(path = recurso)
            } else if (recursos[page].tipo_slide == "pagina_web") {
                RecursoWeb(url = recurso)
            }

        }
    }

}

/**
 * Efecto Face para las diapositivas
 */
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}