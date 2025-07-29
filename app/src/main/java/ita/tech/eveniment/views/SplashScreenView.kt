package ita.tech.eveniment.views

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import ita.tech.eveniment.R
import ita.tech.eveniment.util.Constants.Companion.CENTRO_DEFAULT
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreenView(navController: NavController, procesoVM: ProcesoViewModel){
    ContentSplashScreenView(navController, procesoVM)
}

@Composable
fun ContentSplashScreenView(navController: NavController, procesoVM: ProcesoViewModel){
    val context = LocalContext.current
    val isAppInitialized by procesoVM.isAppInitialized.collectAsState()

    // Creador de enfoque
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isAppInitialized) {
        /*
        val creacionCarpetas: Boolean = procesoVM.crearDirectoriosGenerales()

        if (creacionCarpetas) {

            procesoVM.setEstatusCarpetas(true)

            procesoVM.obtenerIdDevices(context)
            procesoVM.obtenerIpAdress()

            //-- INTERNET = TRUE
            procesoVM.altaDispositivo(CENTRO_DEFAULT)

            // Obtiene Informacion del dispositivo y tambien obtiene los recursos.
            procesoVM.descargarInformacion(context)

        }

        // Despues de 1 seg pasamos a Home
        delay(1000)
        navController.navigate("Home"){ popUpTo(0)}
        */
        if (isAppInitialized) {
            // Navega a Home solo cuando la app está completamente inicializada
            navController.navigate("Home") { popUpTo(0) }
        } else {
            // Llama a la función de inicialización del ViewModel
            procesoVM.initializeApplication(context)
        }
        // Solicita el enfoque al Column principal una vez que el composable está listo.
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .fillMaxHeight()
            .focusRequester(focusRequester)
            .focusGroup(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = R.drawable.logo_itatech_blanco,
            contentDescription = "ITA TECH",
            modifier = Modifier
                .width(250.dp)
                .padding(bottom = 100.dp)
        )
        Text(
            text = "w w w . i t a . t e c h",
            color = Color.White,
            fontSize = 20.sp
        )
    }
}