package ita.tech.eveniment.views.plantillasHorizontales

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.components.PHBarraLateralDos
import ita.tech.eveniment.components.RecursoImagen
import ita.tech.eveniment.components.RecursoListaVideos
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.delay

@Composable
fun Plantilla_Horizontal_Dos(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
){
    val imgDefault = procesoVM.stateInformacionPantalla.nombreArchivo
    val timeZone = procesoVM.stateInformacionPantalla.time_zone
    val estatusInternetNAS = procesoVM.stateEveniment.estatusInternetNAS

    // Variables para mostrar recursos NAS
    val id_evento = procesoVM.stateInformacionPantalla.id_evento
    val tiempo_sin_internet = procesoVM.stateInformacionPantalla.tiempo_sin_internet
    val recursos_nas = procesoVM.stateInformacionPantalla.recursos_nas
    var contador by remember { mutableIntStateOf(0) }
    var showNAS by remember { mutableStateOf(false) }

    //-- Detectamos si el estatus del Internet
    LaunchedEffect(estatusInternetNAS) {
        // Validamos si es necesario mostrar un recurso de la NAS
        if( id_evento > 0 && recursos_nas.isNotEmpty() ){
            if( !estatusInternetNAS ){
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .weight(0.75f) // 80% del espacio vertical disponible
                .fillMaxWidth()
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
                    Carrucel(
                        recursos,
                        imgDefault,
                        timeZone,
                        onTipoSlideChange = {},
                        isOverlay = false,
                        colorSecundario = procesoVM.stateEveniment.color_secundario,
                        textoAgrupado = procesoVM.stateInformacionPantalla.eventos_texto_agrupado,
                        plantilla = 2,
                        zoom_youtube = procesoVM.stateInformacionPantalla.zoom_youtube
                    )
                }

            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    // CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(100.dp))
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(0.25f) // 80% del espacio vertical disponible
                .fillMaxWidth()
                .background(color = procesoVM.stateEveniment.color_secundario) // Generan el borde al final de la barra
                .padding(top = 10.dp)
        ) {
            PHBarraLateralDos(procesoVM = procesoVM)
        }
    }
}