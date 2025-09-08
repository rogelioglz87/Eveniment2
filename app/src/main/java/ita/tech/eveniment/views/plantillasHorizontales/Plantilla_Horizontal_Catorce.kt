package ita.tech.eveniment.views.plantillasHorizontales

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import ita.tech.eveniment.components.PHBarraLateralTres
import ita.tech.eveniment.components.PHBarraLateralUno
import ita.tech.eveniment.components.RecursoWeb
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun Plantilla_Horizontal_Catorce(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel
) {
    val imgDefault = procesoVM.stateInformacionPantalla.nombreArchivo
    val timeZone = procesoVM.stateInformacionPantalla.time_zone
    var recargarPaginaWeb by remember {
        mutableStateOf(false)
    }

    // Recarga el componente d ela pagina web
    LaunchedEffect(procesoVM.stateInformacionPantalla.url_pagina_web) {
        recargarPaginaWeb = true
        delay(1000)
        recargarPaginaWeb = false
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                if(!recargarPaginaWeb){
                    RecursoWeb(url = procesoVM.stateInformacionPantalla.url_pagina_web)
                }
                else{
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black)
                    ) { }
                }
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