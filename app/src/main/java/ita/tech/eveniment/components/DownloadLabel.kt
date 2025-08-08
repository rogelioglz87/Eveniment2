package ita.tech.eveniment.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import ita.tech.eveniment.broadcast.DescargarReceiver
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DownloadLabel(procesoVM: ProcesoViewModel){

    val stateEveniment = procesoVM.stateEveniment
    val context = LocalContext.current

    //-- Contabiliza los recursos descargados
    var recursosDescargados: Int = 1;

    DisposableEffect(stateEveniment.bandInicioDescarga) {

        val receiver = DescargarReceiver(
            procesoVM.recursosId,
            onComplete = {
                procesoVM.sustituyeUrlPorPathLocal()
                procesoVM.sustituyeUrlPorPathLocalPlantilla()
                procesoVM.sustituyeUrlPorPathLocalPantalla()
                procesoVM.resetCarrucel()
                procesoVM.setBandInicioDescarga(false)
                procesoVM.setTotalRecursos(0)    // Inicializamos el total de recursos a descargar
                procesoVM.setTotalRecursosDescargados(0)
                procesoVM.clearListaIdRecursos() // Borramos los Ids de las descargas
                procesoVM.setbandDescargaLbl(false)
                // Actualizar informacion de pantalla

                // Actualizar informacion de recursos
            },
            onRecursoDescargado = {
                procesoVM.setTotalRecursosDescargados(recursosDescargados)
                recursosDescargados++
            }
        )
        if(stateEveniment.bandInicioDescarga){
            receiver.register(context)
        }
        onDispose {
            receiver.unregister(context)
        }
    }

    Text(text = "Descargando recursos ${stateEveniment.totalRecursosDescargados} de ${stateEveniment.totalRecursos}", color = Color.White)
}