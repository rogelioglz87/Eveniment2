package ita.tech.eveniment.views.plantillasVerticales

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

@Composable
fun Plantilla_Vertical_Nueve(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
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