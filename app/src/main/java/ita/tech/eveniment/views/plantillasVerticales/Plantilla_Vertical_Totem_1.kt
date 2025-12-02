package ita.tech.eveniment.views.plantillasVerticales

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.compose.ConstraintLayout
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.components.RecursoListaVideos
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.delay

@Composable
fun Plantilla_Vertical_Totem_1(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel,
    recursosPlantilla: List<InformacionRecursoModel>
){
    var columnWidth by remember { mutableStateOf(1.0f) }
    var tipoSlideActualPrincipal by remember { mutableStateOf("") }
    val estatusInternet = procesoVM.stateEveniment.estatusInternet

    // Variables para mostrar recursos NAS
    val id_evento = procesoVM.stateInformacionPantalla.id_evento
    val tiempo_sin_internet = procesoVM.stateInformacionPantalla.tiempo_sin_internet
    val recursos_nas = procesoVM.stateInformacionPantalla.recursos_nas
    var contador by remember { mutableIntStateOf(0) }
    var showNAS by remember { mutableStateOf(false) }

    LaunchedEffect(tipoSlideActualPrincipal) {
        Log.d("*** TIPO SLIDE", tipoSlideActualPrincipal);
        if( tipoSlideActualPrincipal == "sin_publicidad" ){
            columnWidth = 1.0f
        }else{
            columnWidth = 0.70f
        }
    }

    //-- Detectamos si el estatus del Internet
    LaunchedEffect(estatusInternet) {
        // Validamos si es necesario mostrar un recurso de la NAS
        if( id_evento > 0 && recursos_nas.isNotEmpty() ){
            if( !estatusInternet ){
                // Si el tiempo de desconexion es mayor al indicado por el usuario en la pantalla,
                // mostrar el recurso (NAS) guardado en la pantalla
                while (contador < tiempo_sin_internet){
                    delay(1000L)
                    contador++
                }
                showNAS = true
            }
            else{
                showNAS = false
                if( contador > 0 ){
                    contador = 0;
                }
            }
        }
    }

    val animatedColumnWidth by animateFloatAsState(
        targetValue = columnWidth,
        animationSpec = tween(durationMillis = 900)
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.95f)
    ) {
        val (contenidoPrincipal, contenidoAnuncios) = createRefs()
        val imgDefault = procesoVM.stateInformacionPantalla.nombreArchivo
        val timeZone = procesoVM.stateInformacionPantalla.time_zone

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.Black)
                .constrainAs(contenidoPrincipal) {}
        ) {
            if(procesoVM.stateEveniment.mostrarCarrucel){
                Carrucel(recursos, imgDefault, timeZone, onTipoSlideChange = { tipoSlide ->
                    // Solo capturamos el tipo de slide en caso de que el carrucel sea el PRINCIPAL
                    tipoSlideActualPrincipal = tipoSlide
                })
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){  }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(animatedColumnWidth)
                .fillMaxWidth()
                .background(Color.White)
                .constrainAs(contenidoAnuncios) {
                    // end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
        ) {
            if(procesoVM.stateEveniment.mostrarCarrucel){
                if(showNAS){
                    RecursoListaVideos(
                        procesoVM.stateInformacionPantalla.url_slide,
                        recursos_nas,
                        isCurrentlyVisible = true,
                        1
                    )
                }
                else{
                    Carrucel(recursosPlantilla, imgDefault, timeZone, onTipoSlideChange = {})
                }
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){}
            }
        }
    }
}