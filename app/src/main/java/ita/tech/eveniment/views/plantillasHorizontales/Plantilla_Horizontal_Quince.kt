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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.components.PHBarraLateralCuatro
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.ProcesoViewModel

@Composable
fun Plantilla_Horizontal_Quince(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
){
    val imgDefault = procesoVM.stateInformacionPantalla.nombreArchivo
    val timeZone = procesoVM.stateInformacionPantalla.time_zone

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .weight(0.20f)
                .fillMaxHeight()
                .background(Color.Red)
        ) {
            PHBarraLateralCuatro(procesoVM)
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
                    Carrucel(recursos, imgDefault, timeZone, onTipoSlideChange = {})
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