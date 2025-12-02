package ita.tech.eveniment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import ita.tech.eveniment.util.Constants.Companion.P14_SIZE_FECHA
import ita.tech.eveniment.util.Constants.Companion.P1_SIZE_FECHA
import ita.tech.eveniment.util.Constants.Companion.P1_SIZE_TITULO
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.delay

@Composable
fun PHBarraLateralCuatro(procesoVM: ProcesoViewModel){
    val stateInformacionPantalla = procesoVM.stateInformacionPantalla
    val stateEveniment = procesoVM.stateEveniment
    val context = LocalContext.current

    var recargarPaginaWeb by remember {
        mutableStateOf(false)
    }

    // Recarga el componente d ela pagina web
    LaunchedEffect(procesoVM.stateInformacionPantalla.url_pagina_web) {
        recargarPaginaWeb = true
        delay(1000)
        recargarPaginaWeb = false
    }

    LaunchedEffect(true) {
        // Inicia el cron para obtener la Fecha Actual
        procesoVM.activarTime()
    }

    DisposableEffect(true) {
        onDispose {
            procesoVM.detenerTime()
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.21f)
                .background(stateEveniment.color_logo),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(stateInformacionPantalla.logo_app)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.50f)
                .background(Color.White)
        ) {
            if (!recargarPaginaWeb) {
                RecursoWeb(url = procesoVM.stateInformacionPantalla.url_pagina_web)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)
                ) { }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.29f)
                .background(stateEveniment.color_primario),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.size(7.dp)) // 20.dp
            Text(
                text = stateInformacionPantalla.textoLibre,
                color = stateEveniment.color_texto,
                fontWeight = FontWeight.Bold,
                fontSize = P1_SIZE_TITULO.sp
            )

            Spacer(modifier = Modifier.height(13.dp)) // 15.dp
            Text(
                text = stateInformacionPantalla.tituloCentro,
                color = stateEveniment.color_texto,
                fontWeight = FontWeight.Bold,
                fontSize = P1_SIZE_TITULO.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stateInformacionPantalla.centro,
                color = stateEveniment.color_texto,
                fontWeight = FontWeight.Bold,
                fontSize = P1_SIZE_TITULO.sp
            )
            Spacer(modifier = Modifier.height(18.dp)) // 60.dp
            Text(
                text = procesoVM.horaActual,
                color = stateEveniment.color_texto,
                fontWeight = FontWeight.Bold,
                fontSize = P1_SIZE_TITULO.sp
            )
            Spacer(modifier = Modifier.height(4.dp)) // 15.dp
            Text(
                text = procesoVM.fechaActualEspaniol,
                color = stateEveniment.color_texto,
                fontWeight = FontWeight.Bold,
                fontSize = P14_SIZE_FECHA.sp
            )
            /*Text(
                text = procesoVM.fechaActualIngles,
                color = stateEveniment.color_texto,
                fontWeight = FontWeight.Bold,
                fontSize = P14_SIZE_FECHA.sp
            )*/

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 5.dp, bottom = 3.dp) // end = 10.dp, bottom = 10.dp
            ) {
                Text(
                    text = "ita.tech",
                    color = stateEveniment.color_texto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }


    }
}
