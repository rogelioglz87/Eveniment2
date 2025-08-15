package ita.tech.eveniment.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import ita.tech.eveniment.R
import ita.tech.eveniment.broadcast.DescargarReceiver
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DownloadScreen(procesoVM: ProcesoViewModel){
    val stateEveniment = procesoVM.stateEveniment
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes( R.raw.loading ))

    val context = LocalContext.current

    //-- Contabiliza los recursos descargados
    var recursosDescargados: Int = 1;

    val scope = rememberCoroutineScope()

    /**
     * Monitorea la descarga de los recursos
     */
    DisposableEffect(stateEveniment.bandInicioDescarga) {
        println("Inicia descarga 0")
        val receiver = DescargarReceiver(
            procesoVM.recursosId,
            onComplete = {
                procesoVM.sustituyeUrlPorPathLocal()
                procesoVM.sustituyeUrlPorPathLocalPlantilla()
                procesoVM.sustituyeUrlPorPathLocalPantalla()
                procesoVM.setEstatusDescarga(false)      // Bandera para indicar que se quite la pantalla de descarga
                procesoVM.setBandInicioDescarga(false)
                procesoVM.setTotalRecursos(0)            // Inicializamos el Total de recursos a descargar
                procesoVM.setTotalRecursosDescargados(0) // Inicializamos el Contador de recursos descargados
                procesoVM.clearListaIdRecursos()         // Borramos los Ids de las descargas
                scope.launch(Dispatchers.IO) {
                    procesoVM.borrarRecursos()
                }
            },
            onRecursoDescargado = {
                procesoVM.setTotalRecursosDescargados(recursosDescargados)
                recursosDescargados++
            }
        )
        if(stateEveniment.bandInicioDescarga){
            receiver.register(context)
        }
        onDispose { receiver.unregister(context) }
    }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(20.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(400.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(20.dp))

        if(!stateEveniment.bandCarpetasCreadas){
            Text(text = "No se generaron las carpetas correctamente, verifique los permisos de la app.", fontSize = 18.sp, fontStyle = FontStyle.Italic, color = Color.White)
        }
        else{
            Text(text = "Descargando recursos, ${stateEveniment.totalRecursosDescargados} de ${stateEveniment.totalRecursos} ", fontSize = 25.sp, fontStyle = FontStyle.Italic, color = Color.White)
        }

    }

}