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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import ita.tech.eveniment.util.Constants.Companion.MARCA_SIZE
import ita.tech.eveniment.util.Constants.Companion.MEDIDAS
import ita.tech.eveniment.util.Constants.Companion.P1_SIZE_FECHA
import ita.tech.eveniment.util.Constants.Companion.P1_SIZE_TITULO
import ita.tech.eveniment.viewModels.ProcesoViewModel

@Composable
fun PHBarraLateralUno(procesoVM: ProcesoViewModel) {

    val stateInformacionPantalla = procesoVM.stateInformacionPantalla
    val stateEveniment = procesoVM.stateEveniment
    val context = LocalContext.current

    // Medidas
    val tamanioLogo = MEDIDAS?.logo ?: 1.0f


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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
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
            modifier = Modifier.fillMaxWidth(tamanioLogo) // 1.0f para tamaño 640x480
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(stateEveniment.color_primario),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size( 30.dp ) )
        Text(text = stateInformacionPantalla.textoLibre, color = stateEveniment.color_texto, fontWeight = FontWeight.Bold, fontSize = P1_SIZE_TITULO.sp)

        Spacer(modifier = Modifier.height( 15.dp) )
        Text(text = procesoVM.horaActual, color = stateEveniment.color_texto, fontWeight = FontWeight.Bold, fontSize = P1_SIZE_TITULO.sp)

        Spacer(modifier = Modifier.height( 30.dp) )
        // "Miércoles, 03 de Septiembre" procesoVM.fechaActualEspaniol
        Text(text = procesoVM.fechaActualEspaniol, color = stateEveniment.color_texto, fontWeight = FontWeight.Bold, fontSize = P1_SIZE_FECHA.sp)
        // Wednesday, 03 September procesoVM.fechaActualIngles
        Text(text = procesoVM.fechaActualIngles, color = stateEveniment.color_texto, fontWeight = FontWeight.Bold, fontSize = P1_SIZE_FECHA.sp)

        Spacer(modifier = Modifier.height( 60.dp) )
        Text(text = stateInformacionPantalla.tituloCentro, color = stateEveniment.color_texto, fontWeight = FontWeight.Bold, fontSize = P1_SIZE_TITULO.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = stateInformacionPantalla.centro, color = stateEveniment.color_texto, fontWeight = FontWeight.Bold, fontSize = P1_SIZE_TITULO.sp)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 10.dp, bottom = 10.dp)  // pantalla: 1080p: 10, pantalla: 640x480: 5
        ) {
            Text(
                text = "ita.tech",
                color = stateEveniment.color_texto,
                fontWeight = FontWeight.Bold,
                fontSize = MARCA_SIZE.sp,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

