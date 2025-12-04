package ita.tech.eveniment.views.plantillasHorizontales

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.components.Clima
import ita.tech.eveniment.components.PHBarraLateralTres
import ita.tech.eveniment.components.RecursoListaVideos
import ita.tech.eveniment.components.RecursoWeb
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.delay

@Composable
fun Plantilla_Horizontal_Diez(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
){
    val imgDefault = procesoVM.stateInformacionPantalla.nombreArchivo
    val timeZone = procesoVM.stateInformacionPantalla.time_zone
    val estatusInternet = procesoVM.stateEveniment.estatusInternet

    // Variables para mostrar recursos NAS
    val id_evento = procesoVM.stateInformacionPantalla.id_evento
    val tiempo_sin_internet = procesoVM.stateInformacionPantalla.tiempo_sin_internet
    val recursos_nas = procesoVM.stateInformacionPantalla.recursos_nas
    var contador by remember { mutableIntStateOf(0) }
    var showNAS by remember { mutableStateOf(false) }

    // Obtenemos los datos del clima
    val clima by procesoVM.clima.collectAsState()


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

    LaunchedEffect(true) {
        procesoVM.activarClima()
    }

    DisposableEffect(true) {
        onDispose {
            procesoVM.detenerClima()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .weight(0.20f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.70f)
                    .background(Color.White)
            ) {
                PHBarraLateralTres(procesoVM)
            }
            // Componente de Clima
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(procesoVM.stateEveniment.color_primario)
            ) {
                Clima(clima)
            }

        }
        Column(
            modifier = Modifier
                .weight(0.80f)
                .fillMaxHeight()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .background(Color.Black)
            ) {
                if (procesoVM.stateEveniment.mostrarCarrucel) {
                    if(showNAS){
                        RecursoListaVideos(
                            procesoVM.stateInformacionPantalla.url_slide,
                            recursos_nas,
                            isCurrentlyVisible = true,
                            1
                        )
                    }
                    else{
                        Carrucel(recursos, imgDefault, timeZone, onTipoSlideChange = {})
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(100.dp))
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(procesoVM.stateEveniment.color_primario),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = procesoVM.noticias_rss,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        // .padding(start = 10.dp, end = 10.dp)
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            velocity = 60.dp, // Adjust scrolling speed
                        )
                )
            }
        }
    }
}