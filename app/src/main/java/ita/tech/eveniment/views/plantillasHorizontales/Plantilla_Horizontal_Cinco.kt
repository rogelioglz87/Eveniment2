package ita.tech.eveniment.views.plantillasHorizontales

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.components.RecursoImagen
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import ita.tech.eveniment.viewModels.RecursoVideoModel

@Composable
fun Plantilla_Horizontal_Cinco(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
){
    val imgDefault = procesoVM.stateInformacionPantalla.nombreArchivo
    val timeZone = procesoVM.stateInformacionPantalla.time_zone

    Column(
        modifier = Modifier
            /* Medidas: Normal */

            .fillMaxHeight()
            .fillMaxWidth()

            /* Medidas: Mundo E */
            /*
            .fillMaxHeight(0.38f)
            .fillMaxWidth(0.75f)
            */

            /* Medidas: Publicidad mini */
            /*
            .fillMaxHeight(0.12f)
            .fillMaxWidth(0.20f)
            */
            .background(Color.Black)
    ) {
        if(procesoVM.stateEveniment.mostrarCarrucel){
            Carrucel(recursos, imgDefault, timeZone, onTipoSlideChange = {})
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
}