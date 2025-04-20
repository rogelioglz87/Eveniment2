package ita.tech.eveniment.views.plantillasHorizontales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.RecursoVideoModel

@Composable
fun Plantilla_Horizontal_Cinco(
    carrucelVM: CarrucelViewModel,
    recursos: List<InformacionRecursoModel>
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black)
    ) {
        Carrucel(carrucelVM, recursos)
    }
}