package ita.tech.eveniment.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ita.tech.eveniment.state.CarrucelState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CarrucelViewModel: ViewModel() {

    var stateCarrucel by mutableStateOf(CarrucelState())
        private set

    private var cronJob by mutableStateOf<Job?>(null)
        private set

    private var tiempoTranscurrido by mutableStateOf(0L)
        private set

    fun detener(){
        cronJob?.cancel()
        tiempoTranscurrido = 0
    }

    fun activarCarrucel(
        onDuracionFinalizada: () -> Unit
    ){
        cronJob?.cancel()
        cronJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                // Validamos la duracion del recurso y detenemos el CronJob
                if (validaTiempoDeVisualizacion()) {
                    detener()
                    onDuracionFinalizada()
                }
                tiempoTranscurrido += 1000
            }
        }
    }

    /**
     * Valida si el tiempo transcurrido es mayor o igual a la duración del recurso
     */
    private fun validaTiempoDeVisualizacion(): Boolean{
        var band: Boolean = false
        if(tiempoTranscurrido >= stateCarrucel.duracionRecursoActual){
            band = true
        }
        return band
    }

    fun setDuracionRecursoActual( duracion: Long ){
        stateCarrucel = stateCarrucel.copy( duracionRecursoActual = (duracion*1000) )
    }

    fun setTiposlide( tipo: String ){
        stateCarrucel = stateCarrucel.copy( tipoSlide = tipo )
    }

    private fun setMostrarCarrucel(estatus: Boolean ){
        stateCarrucel = stateCarrucel.copy( mostrarCarrucel = estatus )
    }

    /**
     * Oculta y vuelve a mostrar el carrucel para reiniciar el composable
     */
    fun resetCarrucel(){
        viewModelScope.launch {
            setMostrarCarrucel(false)
            delay(500)
            setMostrarCarrucel(true)
        }
    }




}