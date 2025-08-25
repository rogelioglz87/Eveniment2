package ita.tech.eveniment.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.state.CarrucelState
import ita.tech.eveniment.util.setTimeZone
import ita.tech.eveniment.util.stringDateToZoneDateTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CarrucelViewModel: ViewModel() {

    private var stateCarrucel by mutableStateOf(CarrucelState())
        private set

    var cronJob by mutableStateOf<Job?>(null)
        private set

    var tiempoTranscurrido by mutableStateOf(0L)
        private set

    // Guarda la lista original sin filtrar
    private val _listaOriginal = MutableStateFlow<List<InformacionRecursoModel>>(emptyList())


    // Mostrara la Lista ya filtrada en funcion al Calendario de la lista de reproducción
    private val _listaFiltrada = MutableStateFlow<List<InformacionRecursoModel>>(emptyList())
    val listaFiltrada = _listaFiltrada.asStateFlow()

    private var listaJob by mutableStateOf<Job?>(null)

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private var timeZone = "America/Mexico_City";

    fun detener(){
        cronJob?.cancel()
        tiempoTranscurrido = 0
    }

    fun detenerFiltroLista(){
        listaJob?.cancel()
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
                println("***--Duracion SEG: ${tiempoTranscurrido}")
                tiempoTranscurrido += 1000
            }
        }
    }

    /**
     * Valida si el tiempo transcurrido es mayor o igual a la duración del recurso
     */
    private fun validaTiempoDeVisualizacion(): Boolean{
        var band = false
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

    fun iniciarFiltrado(recursos: List<InformacionRecursoModel>, timeZone: String) {
        _listaOriginal.value = recursos
        this.timeZone = timeZone
        iniciarTemporizadorDeFiltro()
    }

    private fun iniciarTemporizadorDeFiltro() {
        listaJob?.cancel()
        listaJob = viewModelScope.launch {
            while (true) {
                actualizarListaFiltrada()
                delay(60000)
            }
        }
    }

    /**
     * Filtramos la lista de recursos en caso de que el recurso tenga una fecha de inicio y termino de visualización
     */
    private fun actualizarListaFiltrada() {
        val fechaActual =  setTimeZone( System.currentTimeMillis(), this.timeZone )
        println("*** Filtrar lista de recursos: $fechaActual")

        val nuevaLista = _listaOriginal.value.filter { recurso ->
            try {
                val fechaIni = stringDateToZoneDateTime(recurso.fecha_ini, this.formatter, this.timeZone)
                val fechaFin = stringDateToZoneDateTime(recurso.fecha_fin, this.formatter, this.timeZone)
                (fechaIni == null && fechaFin == null) || (fechaActual.isEqual(fechaIni) || fechaActual.isAfter(fechaIni)) && fechaActual.isBefore(fechaFin)
            }catch (e: DateTimeParseException){
                false
            }
        }

        _listaFiltrada.value = nuevaLista
    }

}