package ita.tech.eveniment.views.plantillasVerticales

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
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel

@Composable
fun Plantilla_Vertical_Nueve(
    carrucelVM: CarrucelViewModel,
    recursos: List<InformacionRecursoModel>
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black)
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