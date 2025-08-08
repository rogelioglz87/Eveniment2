package ita.tech.eveniment.views.plantillasHorizontales

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.components.PHBarraLateralUno
import ita.tech.eveniment.components.RecursoImagen
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel

@Composable
fun Plantilla_Horizontal_Uno(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .weight(0.18f)
                .fillMaxHeight()
                .background(color = procesoVM.stateEveniment.color_secundario) // Generan el borde al final de la barra
                .padding(end = 10.dp)
        ) {
            PHBarraLateralUno(procesoVM)
        }
        Column(
            modifier = Modifier
                .weight(0.82f)
                .fillMaxHeight()
                .background(Color.Black)
        ) {
            if(procesoVM.stateEveniment.mostrarCarrucel){
                Carrucel(recursos, procesoVM.stateInformacionPantalla.nombreArchivo, onTipoSlideChange = {})
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

}