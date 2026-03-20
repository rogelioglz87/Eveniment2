package ita.tech.eveniment.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import ita.tech.eveniment.broadcast.DescargarReceiver
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DownloadLabel(procesoVM: ProcesoViewModel){

    val stateEveniment = procesoVM.stateEveniment
    val context = LocalContext.current

    DisposableEffect(stateEveniment.bandInicioDescarga) {

        val receiver = DescargarReceiver(
            getIdsDescarga = { procesoVM.recursosId.value },
            totalRecursos = procesoVM.stateEveniment.totalRecursos,
            onComplete = {
                println("---- Descarga COMPLETA")
                procesoVM.onDescargaCompletaLabel()
            },
            onRecursoDescargado = { descargados ->
                procesoVM.setTotalRecursosDescargados(descargados)
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