package ita.tech.eveniment.views.plantillasHorizontales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.components.PHBarraLateralDos
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel

@Composable
fun Plantilla_Horizontal_Tres(
    carrucelVM: CarrucelViewModel,
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .weight(0.25f) // 25% del espacio vertical disponible
                .fillMaxWidth()
                .background(color = procesoVM.stateEveniment.color_secundario) // Generan el borde al final de la barra
                .padding(bottom = 10.dp)
        ) {
            PHBarraLateralDos(procesoVM = procesoVM)
        }
        Column(
            modifier = Modifier
                .weight(0.75f) // 75% del espacio vertical disponible
                .fillMaxWidth()
        ) {
            if(carrucelVM.stateCarrucel.mostrarCarrucel){
                Carrucel(carrucelVM, recursos)
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