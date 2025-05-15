package ita.tech.eveniment.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import ita.tech.eveniment.components.DownloadLabel
import ita.tech.eveniment.components.DownloadScreen
import ita.tech.eveniment.socket.SocketHandler
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Cinco
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Cuatro
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Dos
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Tres
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Uno
import ita.tech.eveniment.views.plantillasVerticales.Plantilla_Vertical_Nueve

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeView(
    procesoVM: ProcesoViewModel,
    carrucelVM: CarrucelViewModel
) {

    val context = LocalContext.current

    val stateEveniment = procesoVM.stateEveniment
    val stateInformacionPantalla = procesoVM.stateInformacionPantalla

    //-- Obtiene la lista de los recursos
    val recursos by procesoVM.recursos.collectAsState()

    //-- Conexión del Socket
    val mSocket = SocketHandler.getSocket()

    LaunchedEffect(Unit) {
        // Conectamos el dispositivo
        mSocket.emit("connected", stateEveniment.idDispositivo, stateEveniment.ipAddress)

        // Escuchamos los eventos del usuario
        mSocket.on("metodos_servidor") { args ->
            if (args != null) {
                val comando = args[0]
                //-- Actualiza la lista de reproducción
                if (comando == "reinicio") {

                    // Actualiza los recursos
                    // procesoVM.descargarInformacionListaReproduccion(context, carrucelVM)

                    // Actualiza la información de la pantalla
                    procesoVM.descargarInformacionPantalla(context)
                }
            }
        }
    }

    if (stateEveniment.bandDescargaRecursos) {
        DownloadScreen(procesoVM)
    } else {
        ConstraintLayout(
            Modifier.fillMaxSize()
        ) {
            val lblDescarga = createRef()

            //-- Muestra la plantilla seleccionada
            when (stateInformacionPantalla.tipo_disenio) {
                "1" -> {
                    Plantilla_Horizontal_Uno(carrucelVM, recursos, procesoVM, context)
                }
                "2" -> {
                    Plantilla_Horizontal_Dos(carrucelVM, recursos, procesoVM, context)
                }
                "3" -> {
                    Plantilla_Horizontal_Tres(carrucelVM, recursos, procesoVM, context)
                }
                "4" -> {
                    Plantilla_Horizontal_Cuatro(carrucelVM, recursos, procesoVM, context)
                }
                "5" -> {
                    Plantilla_Horizontal_Cinco(carrucelVM, recursos, procesoVM, context)
                }
                "9" -> {
                    Plantilla_Vertical_Nueve(carrucelVM, recursos, procesoVM, context)
                }
                else -> {

                }
            }

            //-- Muesta el estado de descarga
            if (stateEveniment.bandDescargaLbl) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(color = Color.Black.copy(alpha = 0.6f))
                        .padding(8.dp)
                        .constrainAs(lblDescarga) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                ) {
                    DownloadLabel(procesoVM, carrucelVM)
                }
            }
        }
    }
}