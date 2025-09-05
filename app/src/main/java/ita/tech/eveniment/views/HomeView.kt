package ita.tech.eveniment.views

import android.content.Context
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil3.imageLoader
import ita.tech.eveniment.components.DownloadLabel
import ita.tech.eveniment.components.DownloadScreen
import ita.tech.eveniment.socket.SocketHandler
import ita.tech.eveniment.viewModels.ProcesoViewModel
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Catorce
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Cinco
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Cuatro
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Doce
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Dos
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Tres
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Uno
import ita.tech.eveniment.views.plantillasVerticales.Plantilla_Vertical_Nueve
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Once
import ita.tech.eveniment.views.plantillasHorizontales.Plantilla_Horizontal_Trece


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeView(
    procesoVM: ProcesoViewModel,
    navController: NavController
) {

    // val context = LocalContext.current

    val stateEveniment = procesoVM.stateEveniment
    val stateInformacionPantalla = procesoVM.stateInformacionPantalla

    //-- Obtiene la lista de los recursos
    val recursos by procesoVM.recursos.collectAsState()

    //-- Obtiene la lista de recursos de la plantilla
    val recursosPlantilla by procesoVM.recursos_plantilla.collectAsState()

    //-- Conexión del Socket
    val mSocket = SocketHandler.getSocket()

    //-- Contador de coneciones a internet
    var contadorInternet by remember { mutableIntStateOf(0) }
    var reiniciarAppBand by remember { mutableStateOf(false) } // Indica si se reiniciara la app

    LaunchedEffect(stateEveniment.estatusInternet) {
        if( stateEveniment.estatusInternet )
        {
            // INGRESA SI LA APP FUE INICIALIZADA SIN INTERNET
            if( reiniciarAppBand )
            {
                // Colocamos en false la variable isAppInitialized para que inicie la descarga de informacion
                procesoVM.resetAppInitialized()

                // En caso de iniciar la App sin internet, se enviara a la pantalla de Splash para descargar los recursos
                navController.navigate("SplashScreen"){
                    popUpTo(0){ saveState = false }
                    launchSingleTop = true
                    restoreState = false
                }
            }
            // INGRESA SI LA APP DURANTE LA EJECUCION SE QUEDO SIN RED (SOLO ACTUALIZARA LA LISTA DE REPRODUCCION Y EL SOCKET)
            else
            {
                // Conectamos el dispositivo al Socket
                mSocket.emit("connected",stateEveniment.idDispositivo,stateEveniment.ipAddress)

                // Validamos si actualizamos los recursos en caso de una reconexión
                if( contadorInternet > 0 )
                {
                    procesoVM.descargarInformacionListaReproduccion()
                }
            }
            contadorInternet++;
        }
        else
        {
            if( contadorInternet == 0 )
            {
                reiniciarAppBand = true;
            }
        }
    }

    LaunchedEffect(Unit) {
        // Escuchamos evento del servidor
        mSocket.on("connect") {
            println("****COMANDO SERV:")
            // En caso de una desconexion, el servidor puede volver a notificar al dispositivo para su reconexion.
            // Conectamos el dispositivo al Socket
            mSocket.emit("connected",stateEveniment.idDispositivo,stateEveniment.ipAddress)
        }

        // Escuchamos los eventos del usuario
        mSocket.on("metodos_servidor") { args ->
            println("****COMANDO: ${args[0]}")
            if (args != null) {
                val comando = args[0]
                //-- Actualiza la lista de reproducción
                if (comando == "actualizar_recursos")
                {
                    procesoVM.descargarInformacionListaReproduccion()
                }
                //-- Actualizar los datos del dispositivo
                else if(comando == "actualizar_datos_dispositivo")
                {
                    procesoVM.descargarInformacionPantalla()
                }
            }
        }

        // Proceso de Reinicio App (CANCELADO)
        /*
        procesoVM.eventoDeReinicio.collect{
            println("***Reiniar App con funcion")
            realizarReinicio(navController, context)
        }
        */
    }

    if (stateEveniment.bandDescargaRecursos) {
        DownloadScreen(procesoVM)
    } else {
        Box (
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            //-- Muestra la plantilla seleccionada
            when (stateInformacionPantalla.tipo_disenio) {
                "1" -> {
                    Plantilla_Horizontal_Uno(recursos, procesoVM)
                }

                "2" -> {
                    Plantilla_Horizontal_Dos(recursos, procesoVM)
                }

                "3" -> {
                    Plantilla_Horizontal_Tres(recursos, procesoVM)
                }

                "4" -> {
                    Plantilla_Horizontal_Cuatro(recursos, procesoVM)
                }

                "5" -> {
                    Plantilla_Horizontal_Cinco(recursos, procesoVM)
                }

                "9" -> {
                    Plantilla_Vertical_Nueve(recursos, procesoVM)
                }

                "11" -> {
                    Plantilla_Horizontal_Once(recursos, procesoVM, recursosPlantilla)
                }

                "12" -> {
                    Plantilla_Horizontal_Doce(recursos, procesoVM, recursosPlantilla)
                }

                "13" -> {
                    Plantilla_Horizontal_Trece(recursos, procesoVM, recursosPlantilla)
                }

                "14" -> {
                    Plantilla_Horizontal_Catorce(recursos, procesoVM)
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
                        .align(Alignment.BottomStart)
                ) {
                    DownloadLabel(procesoVM)
                }
            }
        }
        
    }
}

fun realizarReinicio(
    navController: NavController,
    context: Context
){
    val imagenLoader = context.imageLoader
    imagenLoader.memoryCache?.clear()
    imagenLoader.diskCache?.clear()

    val startDestinationRoute = navController.graph.findStartDestination().route ?: return
    navController.navigate(startDestinationRoute){
        popUpTo(startDestinationRoute){
            inclusive = true
        }
        launchSingleTop = true
        restoreState = false
    }
}