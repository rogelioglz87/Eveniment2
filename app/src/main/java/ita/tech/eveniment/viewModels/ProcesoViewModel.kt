package ita.tech.eveniment.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.provider.Settings.Secure
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import ita.tech.eveniment.model.CalendarioAlarmaDB
import ita.tech.eveniment.model.InformacionPantallaDB
import ita.tech.eveniment.model.InformacionPantallaModel
import ita.tech.eveniment.model.InformacionCalendarioModel
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.model.RssEntry
import ita.tech.eveniment.repository.CalendarioAlarmaRepository
import ita.tech.eveniment.repository.EvenimentRepository
import ita.tech.eveniment.repository.InformacionPantallaRepository
import ita.tech.eveniment.state.EvenimentState
import ita.tech.eveniment.state.InformacionPantallaState
import ita.tech.eveniment.util.Constants.Companion.CENTRO_DEFAULT
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT_DATOS
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT_IMAGENES
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT_VIDEOS
import ita.tech.eveniment.util.EmiteNotificacionCalendario
import ita.tech.eveniment.util.alarmaCalendario
import ita.tech.eveniment.util.alarmaCalendarioCancelar
import ita.tech.eveniment.util.formatTimeFechaEspaniol
import ita.tech.eveniment.util.formatTimeFechaIngles
import ita.tech.eveniment.util.formatTimeHora
import ita.tech.eveniment.util.obtenerValorAleatorio
import ita.tech.eveniment.util.setTimeZone
import ita.tech.eveniment.util.stringDateToZoneDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Collections
import javax.inject.Inject


@HiltViewModel
class ProcesoViewModel @Inject constructor(
    application: Application,
    private val repository: EvenimentRepository,
    private val informacionPantallaRepository: InformacionPantallaRepository,
    private val calendarioAlarmaRepository: CalendarioAlarmaRepository
    ) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    // Bandera para inicializar la App
    private val _isAppInitialized = MutableStateFlow(false)
    val isAppInitialized: StateFlow<Boolean> = _isAppInitialized.asStateFlow()

    var stateInformacionPantalla by mutableStateOf(InformacionPantallaState())
        private set

    var stateEveniment by mutableStateOf(EvenimentState())
        private set

    // Variables de Reinicio App
    // private val _eventoDeReinicio = MutableSharedFlow<Unit>()
    // val eventoDeReinicio = _eventoDeReinicio.asSharedFlow()

    // Variables para el Calendario
    private val calendario = MutableStateFlow<List<InformacionCalendarioModel>>(emptyList()) // Almacenara la lista de recursos mientras se descarga
    private var calendarioActivo: InformacionCalendarioModel? = null

    // Variables para la Lista de reproduccion PRINCIPAL
    private val _recursos_tmp = MutableStateFlow<List<InformacionRecursoModel>>(emptyList()) // Almacenara la lista de recursos mientras se descarga
    private val _recursos = MutableStateFlow<List<InformacionRecursoModel>>(emptyList())     // Almacenara la lista de recursos cuando tenga path local.
    val recursos = _recursos.asStateFlow()

    // Variables para la lista de reproduccion PLANTILLA
    private val _recursos_plantilla_tmp = MutableStateFlow<List<InformacionRecursoModel>>(emptyList()) // Almacenara la lista de recursos mientras se descarga
    private val _recursos_plantilla = MutableStateFlow<List<InformacionRecursoModel>>(emptyList())     // Almacenara la lista de recursos cuando tenga path local.
    val recursos_plantilla = _recursos_plantilla.asStateFlow()

    // Variables para obtener noticias RSS
    var noticias_rss by mutableStateOf("")
        private set

    private val gson = Gson()

    // Canal para enviar eventos de orientación a la UI
    private val _eventoDeOrientacion = MutableSharedFlow<Int>()
    val eventoDeOrientacion = _eventoDeOrientacion.asSharedFlow()

    /**
     * Almacena los IDs de los recursos a descargar
     */
    private var _recursosId = mutableListOf<Long>()
    var recursosId = _recursosId

    //-- Variables de tiempo
    private var cronJobTimer by mutableStateOf<Job?>(null)
    var horaActual by mutableStateOf("")
        private set

    private var fechaActualGeneral: String? = null
    private var fechaActualGeneralBand: Boolean = false

    var fechaActualEspaniol by mutableStateOf("")
        private set
    var fechaActualIngles by mutableStateOf("")
        private set

    // Formato de fechas recibida
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init{
        // Proceso que escucha la notificacion de la Alarma del Calendario
        notificarCalendario()
    }

    /**
     * Set estatus del proceso
     */
    fun setEstatusDescarga( status: Boolean ){
        stateEveniment = stateEveniment.copy(bandDescargaRecursos = status)
    }

    fun setbandDescargaLbl( status: Boolean ){
        stateEveniment = stateEveniment.copy(bandDescargaLbl = status)
    }

    fun setTotalRecursos( total: Int ){
        stateEveniment = stateEveniment.copy(totalRecursos = total)
    }

    private fun setEstatusCarpetas(status: Boolean ){
        stateEveniment = stateEveniment.copy(bandCarpetasCreadas = status)
    }

    fun setTotalRecursosDescargados( total: Int ){
        stateEveniment = stateEveniment.copy(totalRecursosDescargados = total)
    }

    fun setBandInicioDescarga( status: Boolean ){
        stateEveniment = stateEveniment.copy(bandInicioDescarga = status)
    }

    fun setEstatusInternet( status: Boolean ){
        stateEveniment = stateEveniment.copy( estatusInternet = status )
    }
    private fun setMostrarCarrucel(estatus: Boolean ){
        stateEveniment = stateEveniment.copy( mostrarCarrucel = estatus )
    }

    fun resetAppInitialized(){
        _isAppInitialized.value = false;
    }

    /**
     * Inicializa la app de forma asincrona
     */
    fun initializeApplication() {
        viewModelScope.launch(Dispatchers.IO) { // Toda esta lógica se ejecuta en un hilo IO
            delay(2000)
            Log.d(" ProcesoViewModel", "Iniciando initializeApplication")

            val creacionCarpetas: Boolean = crearDirectoriosGenerales()
            if (creacionCarpetas) {
                withContext(Dispatchers.Main) { setEstatusCarpetas(true) } // Actualiza UI en Main thread

                obtenerIdDevices()
                obtenerIpAdress()
                altaDispositivo(CENTRO_DEFAULT)
                descargarInformacion() // Esto ya lanza su propia coroutine con IO

                _isAppInitialized.value = true
                Log.d("ProcesoViewModel", "Aplicación inicializada completamente.")

            } else {
                Log.e("ProcesoViewModel", "Error al crear directorios generales. No se pudo inicializar la app.")
                // Aquí podrías manejar un error, por ejemplo, mostrando un mensaje al usuario
            }
        }
    }

    /**
     * Obtiene ID unico del dispositivo.
     */
    @SuppressLint("HardwareIds")
    fun obtenerIdDevices() {
        stateEveniment = stateEveniment.copy(
            idDispositivo = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
            // idDispositivo = "f4e1c5ef8a4cfeb4"
        )
    }

    /**
     * Crea la estructura de carpetas para almacenar los recursos de la app.
     * Eveniment -> datos
     * Eveniment -> videos
     * Eveniment -> imagenes
     */
    private fun crearDirectoriosGenerales(): Boolean{
        val folderEveniment = File(FOLDER_EVENIMENT)
        val folderDatos = File(FOLDER_EVENIMENT_DATOS)
        val folderVideos = File(FOLDER_EVENIMENT_VIDEOS)
        val folderImagenes = File(FOLDER_EVENIMENT_IMAGENES)
        var band: Boolean = false;

        try{
            if( !folderEveniment.exists() ){
                folderEveniment.mkdir()
            }
            if ( !folderDatos.exists() ){
                folderDatos.mkdirs()
            }
            if ( !folderVideos.exists() ){
                folderVideos.mkdirs()
            }
            if ( !folderImagenes.exists() ){
                folderImagenes.mkdirs()
            }
        } catch (e: FileNotFoundException){
            println("Error: " + e.printStackTrace())
        }

        if(folderEveniment.exists() && folderDatos.exists() && folderVideos.exists() && folderImagenes.exists()){
            band = true
        }
        return band
    }

    /**
     * Obtiene la IP del dispositivo.
     */
    private fun obtenerIpAdress() {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        // WIFI
                        if (intf.name.contains("wlan0")) {
                            stateEveniment =
                                stateEveniment.copy(ipAddress = sAddr?.toString() ?: "")
                        }

                        // ETHERNET
                        if (intf.name.contains("eth0")) {
                            stateEveniment =
                                stateEveniment.copy(ipAddress = sAddr?.toString() ?: "")
                        }

                        // VPN
                        if (intf.name.contains("tun1")) {
                            stateEveniment =
                                stateEveniment.copy(ipVPN = sAddr?.toString() ?: "")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR IP DEVICES", e.message.toString())
        }
    }

    private suspend fun altaDispositivo(idCetroDefault: String) {
        try {
            val result = repository.altaDispositivo(stateEveniment.idDispositivo, idCetroDefault)
            if (result == "1") {
                withContext(Dispatchers.Main) {
                    stateEveniment = stateEveniment.copy(altaDispositivo = true)
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    /**
     * Descarga inicial, descarga recursos de la pantalla y de la lista de reproducción
     */
    private suspend fun descargarInformacion() {

        //-- API Descargamos recursos de la PANTALLA
        obtenerInformacionPantalla()

        // Obtenemos y convertimos los colores de la pantalla
        withContext(Dispatchers.Main) {
            convertirColoresPantalla()
        }

        // Rotamos la pantalla en caso de ser necesario
        determinaOrientacionPantalla()

        // Obtenemos los recursos descargables de la Pantalla (logo, imagen de default, video de alerta, etc...)
        val recursosPantalla: List<String> = obtenerRecursosPantalla()

        //-- API Descargamos recursos de la Lista de Reproduccion (PLANTILLA)
        // Plantillas con Lista de Reproduccion independiente: 11, 12, 13
        if( stateInformacionPantalla.id_lista_reproduccion > 0 ){
            obtenerInformacionRecursosPlantilla()
        }

        //-- API RSS
        if(stateInformacionPantalla.tipo_disenio == "13" ||
            stateInformacionPantalla.tipo_disenio == "14"){
            obtenerInformacionRss()
        }

        // Obtenemos los recursos descargables.
        val recursosDescargablesPlantilla: List<InformacionRecursoModel> = obtenerRecursosDescargables(_recursos_plantilla_tmp)

        //-- 1 Obtenemos informacion del Calendario
        if( stateInformacionPantalla.tipo_fuente_eventos == "calendario" ){
            obtenerInformacionCalendario()
            //-- 2 Determinar la Lista de Reproduccion a mostrar
            this.calendarioActivo = obtenerListaReproduccion()
            _recursos_tmp.value = this.calendarioActivo?.eventos?.values?.toList() ?: emptyList()
        }
        else{
            // Borrar Alarmas del Calendario en caso de que se hayan programado con anterioridad
            borrarAlarmasCalendario()
            calendarioAlarmaRepository.delete()

            //-- API Descargamos recursos de la Lista de Reproduccion (PRINCIPAL)
            obtenerInformacionRecursos()
        }

        // Obtenemos los recursos descargables.
        val recursosDescargables: List<InformacionRecursoModel> = obtenerRecursosDescargables(_recursos_tmp)

        // Almacenamos el Total de recursos a descargar más los recursos de pantalla
        withContext(Dispatchers.Main) {
            stateEveniment =
                stateEveniment.copy(totalRecursos = recursosDescargables.size + recursosPantalla.size + recursosDescargablesPlantilla.size)
        }

        // Descargamos los recursos de pantalla
        descargarArchivosPantalla(recursosPantalla)

        // Descargamos los recursos de la lista de reproduccion (PRINCIPAL)
        descargarArchivos(recursosDescargables)
        if(recursosDescargables.isEmpty()){
            // Cambiamos la URL por el PATH Local
            sustituyeUrlPorPathLocal()
        }

        // Descargamos los recursos de la lista de reproduccion (PLANTILLA)
        descargarArchivos(recursosDescargablesPlantilla)
        if( recursosDescargablesPlantilla.isEmpty() ){
            // Cambiamos la URL por el PATH Local
            sustituyeUrlPorPathLocalPlantilla()
        }

        // Borramos recursos que no se ocupen
        if( stateEveniment.totalRecursos == 0 ){
            borrarRecursos()
        }

        // Indicamos el momento en que se inicia la descarga
        withContext(Dispatchers.Main) {
            stateEveniment = if (stateEveniment.totalRecursos > 0) {
                stateEveniment.copy(bandInicioDescarga = true)
            } else {
                // Quitamos pantalla de Descarga
                stateEveniment.copy(bandDescargaRecursos = false)
            }
        }
    }

    fun borrarRecursos(){
        // Borrar recursos de Pantalla
        borrarRecursosPantalla()

        // Borrar recursos de Lista de reproduccion
        borrarRecursosListaReproduccion()
    }

    private fun borrarRecursosListaReproduccion(){
        var listaRecursos: List<InformacionRecursoModel> = emptyList()
        var listaRecursosPlantilla: List<InformacionRecursoModel> = emptyList()
        val archivosImagenes: List<String> = listarArchivosDeCarpeta(FOLDER_EVENIMENT_IMAGENES)
        val archivosVideos: List<String> = listarArchivosDeCarpeta(FOLDER_EVENIMENT_VIDEOS)
        var bandBorrar: Boolean

        if( stateInformacionPantalla.tipo_fuente_eventos == "calendario" ){
            val calendarioListas = mutableListOf<InformacionRecursoModel>()
            calendario.value.forEach { evento ->
                val listaRecursosTmp = evento.eventos.values.toList()
                calendarioListas += listaRecursosTmp.filter { it.tipo_slide == "video" || it.tipo_slide == "imagen" }
            }
            listaRecursos = calendarioListas
        }
        else{
            if( _recursos.value.isNotEmpty() ){
                listaRecursos = _recursos.value.filter { it.tipo_slide == "video" || it.tipo_slide == "imagen" }
            }
        }


        // Lista de reproduccion de Plantilla
        if( _recursos_plantilla.value.isNotEmpty() ){
            listaRecursosPlantilla = _recursos_plantilla.value.filter { it.tipo_slide == "video" || it.tipo_slide == "imagen" }
        }

        val listaCompleta: List<InformacionRecursoModel> = listaRecursos + listaRecursosPlantilla
        val listaCompletaUnica: List<InformacionRecursoModel> = obtenerRecursosUnicos( listaCompleta )

        // Validamos Imagenes
        if( archivosImagenes.isNotEmpty() ){
            archivosImagenes.forEach { archivo ->
                bandBorrar = true
                run loop@{
                    listaCompletaUnica.forEach { recurso ->
                        if (archivo == obtenerNombreUrl(recurso.datos.toString())) {
                            bandBorrar = false
                            return@loop
                        }
                    }
                }
                // Borramos archivo
                if( bandBorrar ){
                    try {
                        File("$FOLDER_EVENIMENT_IMAGENES/$archivo").delete()
                    }catch (e:SecurityException){
                        println("***Error al borrar el archivo: $FOLDER_EVENIMENT_IMAGENES/$archivo")
                    }
                }
            }
        }

        // Validamos Videos
        if( archivosVideos.isNotEmpty() ){
            archivosVideos.forEach { archivo ->
                bandBorrar = true
                run loop@{
                    listaCompletaUnica.forEach { recurso ->
                        if( archivo == obtenerNombreUrl( recurso.datos.toString() ) ){
                            bandBorrar = false
                            return@loop
                        }
                    }
                }

                // Borramos archivo
                if( bandBorrar ){
                    try {
                        File("$FOLDER_EVENIMENT_VIDEOS/$archivo").delete()
                    }catch (e:SecurityException){
                        println("***Error al borrar el archivo: $FOLDER_EVENIMENT_VIDEOS/$archivo")
                    }
                }
            }
        }
    }

    /**
     * Borra los recursos de la Pantalla que no se ocupen
     */
    private fun borrarRecursosPantalla(){
        val archivos: List<String> = listarArchivosDeCarpeta(FOLDER_EVENIMENT_DATOS)
        var bandBorrar: Boolean
        if( archivos.isNotEmpty() ){
            archivos.forEach { archivo ->
                // Validamos si el archivo existe en los recursos de la pantalla, en caso de no existir se borra
                bandBorrar = true
                if( obtenerNombreUrl(stateInformacionPantalla.logo_app) == archivo ||
                    obtenerNombreUrl(stateInformacionPantalla.nombreArchivo) == archivo ||
                    obtenerNombreUrl(stateInformacionPantalla.nombreArchivoImgDisenioDos) == archivo ||
                    obtenerNombreUrl(stateInformacionPantalla.nombreArchivoImgDisenioTres) == archivo ||
                    obtenerNombreUrl(stateInformacionPantalla.u_logo_app) == archivo ||
                    obtenerNombreUrl(stateInformacionPantalla.video_alerta) == archivo){
                    bandBorrar = false
                }
                if( bandBorrar ){
                    try {
                        File("$FOLDER_EVENIMENT_DATOS/$archivo").delete()
                    }catch (e:SecurityException){
                        println("***Error al borrar el archivo: $FOLDER_EVENIMENT_DATOS/$archivo")
                    }
                }
            }
        }
    }

    /**
     * Lista los archivos de una ruta dada
     */
    private fun listarArchivosDeCarpeta(ruta: String): List<String>{
        val path = File(ruta)
        if( !path.exists() || !path.isDirectory ){
            return emptyList()
        }
        return path.listFiles()?.map { it.name } ?: emptyList()
    }

    /**
     * Descarga toda la informacion de la Pantalla (Logo, nombre, colores, etc...)
     */
    fun descargarInformacionPantalla(){
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { setbandDescargaLbl(true) }

            //-- API Descargamos recursos de la PANTALLA
            obtenerInformacionPantalla()

            //-- API Rss
            if(stateInformacionPantalla.tipo_disenio == "13" ||
                stateInformacionPantalla.tipo_disenio == "14"){
                obtenerInformacionRss()
            }

            // Obtenemos los recursos descargables de la Pantalla (logo, imagen de default, video de alerta, etc...)
            val recursosPantalla: List<String> = obtenerRecursosPantalla()

            //-- API Descargamos recursos de la Lista de Reproduccion (PLANTILLA)
            // Plantillas con Lista de Reproduccion independiente: 11, 12, 13
            if( stateInformacionPantalla.id_lista_reproduccion > 0 ){
                obtenerInformacionRecursosPlantilla()
            }
            // Obtenemos los recursos descargables.
            val recursosDescargablesPlantilla: List<InformacionRecursoModel> = obtenerRecursosDescargables(_recursos_plantilla_tmp)

            // Descargamos los recursos de pantalla
            descargarArchivosPantalla(recursosPantalla)

            // Descargamos los recursos de la lista de reproduccion (PLANTILLA)
            descargarArchivos(recursosDescargablesPlantilla)
            if( recursosDescargablesPlantilla.isEmpty() ){
                // Cambiamos la URL por el PATH Local
                sustituyeUrlPorPathLocalPlantilla()
            }

            // Borramos recursos que no se ocupen
            borrarRecursos()

            // Rotamos la pantalla en caso de ser necesario
            determinaOrientacionPantalla()

            withContext(Dispatchers.Main) {
                // Obtenemos y convertimos los colores de la pantalla
                convertirColoresPantalla()

                // Almacenamos el Total de recursos a descargar más los recursos de pantalla
                stateEveniment = stateEveniment.copy(totalRecursos = recursosPantalla.size + recursosDescargablesPlantilla.size)

                // Indicamos el momento en que se inicia la descarga
                if( stateEveniment.totalRecursos > 0 ){
                    stateEveniment = stateEveniment.copy( bandInicioDescarga = true )
                } else{
                    // Quitamos pantalla de Descarga
                    stateEveniment = stateEveniment.copy(bandDescargaLbl = false)
                    // Si la plantilla contiene una lista de reproduccion actualizamos
                    if( stateInformacionPantalla.id_lista_reproduccion > 0 ){
                        resetCarrucel()
                    }
                }
            }

        }
    }

    /**
     * Descarga toda la informacion de la Pantalla (Solo texto)
     * El objetivo principal es actualizar la fuente de informacion del dispositivo (Lista o Calendario)
     */
    private  suspend fun descargarInformacionPantallaLite(){
        //-- API Descargamos recursos de la PANTALLA
        obtenerInformacionPantalla()
    }

    /**
     * Descarga solo los recursos de la lista de reproducción.
     */
    fun descargarInformacionListaReproduccion(){
        val self = this
        viewModelScope.launch (Dispatchers.IO) {
            withContext(Dispatchers.Main) { setbandDescargaLbl(true) }

            // Actualizamos la informacion del dispositivo
            descargarInformacionPantallaLite()

            //-- API Descargamos recursos de la Lista de Reproduccion (PLANTILLA)
            if( stateInformacionPantalla.id_lista_reproduccion > 0 ){
                obtenerInformacionRecursosPlantilla()
            }

            // Obtenemos los recursos descargables.
            val recursosDescargablesPlantilla: List<InformacionRecursoModel> = obtenerRecursosDescargables(_recursos_plantilla_tmp)

            //-- 1 Obtenemos informacion del Calendario
            if( stateInformacionPantalla.tipo_fuente_eventos == "calendario" ){
                obtenerInformacionCalendario()
                //-- 2 Determinar la Lista de Reproduccion a mostrar
                self.calendarioActivo = obtenerListaReproduccion()
                _recursos_tmp.value = self.calendarioActivo?.eventos?.values?.toList() ?: emptyList()
            }
            else{
                // Borrar Alarmas del Calendario en caso de que se hayan programado con anterioridad
                borrarAlarmasCalendario()
                calendarioAlarmaRepository.delete()

                //-- API Descargamos recursos de la LISTA DE REPRODUCCION
                obtenerInformacionRecursos()
            }

            // Obtenemos los recursos descargables.
            val recursosDescargables: List<InformacionRecursoModel> = obtenerRecursosDescargables(_recursos_tmp)

            // Almacenamos el Total de recursos a descargar más los recursos de pantalla
            withContext(Dispatchers.Main) {
                stateEveniment = stateEveniment.copy(totalRecursos = recursosDescargables.size + recursosDescargablesPlantilla.size)
            }

            // Descargamos los recursos
            descargarArchivos(recursosDescargables)
            if(recursosDescargables.isEmpty()){
                // Cambiamos la URL por el PATH Local
                sustituyeUrlPorPathLocal()
            }

            // Descargamos los recursos de la lista de reproduccion (PLANTILLA)
            descargarArchivos(recursosDescargablesPlantilla)
            if( recursosDescargablesPlantilla.isEmpty() ){
                // Cambiamos la URL por el PATH Local
                sustituyeUrlPorPathLocalPlantilla()
            }

            /// Borrar recursos de Lista de reproduccion
            if(stateEveniment.totalRecursos == 0){
                borrarRecursosListaReproduccion()
            }

            // Indicamos el momento en que se inicia la descarga
            withContext(Dispatchers.Main) {
                if (stateEveniment.totalRecursos > 0) {
                    stateEveniment = stateEveniment.copy(bandInicioDescarga = true)
                } else {
                    // Quitamos etiqueta de Descarga
                    stateEveniment = stateEveniment.copy(bandDescargaLbl = false)
                    resetCarrucel()
                }
            }
        }
    }

    /**
     * Actualiza la lista de reproduccion en funcion al Calendario programado
     */
    private suspend fun actualizarListaReproduccion(){
        // Obtenemos la Lista de reproduccion en Turno del Calendario
        val listaEnTurno = obtenerListaReproduccionTurno()

        if( listaEnTurno != null ){
            val idProgramacionAnterior = calendarioActivo?.idProgramacion

            if( idProgramacionAnterior != listaEnTurno.idProgramacion ){
                // Mostramos etiqueta de descarga
                withContext(Dispatchers.Main) { setbandDescargaLbl(true) }

                calendarioActivo = listaEnTurno
                _recursos_tmp.value = listaEnTurno.eventos.values.toList() ?: emptyList()

                // Obtenemos los recursos descargables.
                val recursosDescargables: List<InformacionRecursoModel> = obtenerRecursosDescargables(_recursos_tmp)

                // Almacenamos el Total de recursos a descargar más los recursos de pantalla
                withContext(Dispatchers.Main) {
                    stateEveniment = stateEveniment.copy(totalRecursos = recursosDescargables.size)
                }

                // Descargamos los recursos
                descargarArchivos(recursosDescargables)
                if(recursosDescargables.isEmpty()){
                    // Cambiamos la URL por el PATH Local
                    sustituyeUrlPorPathLocal()
                }

                // Indicamos el momento en que se inicia la descarga
                withContext(Dispatchers.Main) {
                    if (stateEveniment.totalRecursos > 0) {
                        stateEveniment = stateEveniment.copy(bandInicioDescarga = true)
                    } else {
                        // Quitamos etiqueta de Descarga
                        stateEveniment = stateEveniment.copy(bandDescargaLbl = false)
                        resetCarrucel()
                    }
                }
            }
        }
    }


    private suspend fun obtenerInformacionPantalla() {
        var result: InformacionPantallaModel? = null
        try {
            result = repository.obtenerInformacionPantalla(stateEveniment.idDispositivo)

            // Guardamos el resultado en Room.
            if( result != null ){
                informacionPantallaRepository.clear("informacionPantalla")
                informacionPantallaRepository.insert(
                    InformacionPantallaDB(
                        concepto = "informacionPantalla",
                        valor = gson.toJson(result)
                    )
                )
            }else{
                // La red respondio, pero sin datos, intentamos usar la BD.
                throw Exception("Respuesta de la red Nula.")
            }
        } catch (e: Exception) {

            // En caso de algun error buscamos informacion de forma Local.
            val informacionLocal = informacionPantallaRepository.getInformacion("informacionPantalla").firstOrNull()
            if( informacionLocal != null ){
                result = gson.fromJson(informacionLocal.valor, InformacionPantallaModel::class.java)
            }
        }

        withContext(Dispatchers.Main) {
            stateInformacionPantalla = stateInformacionPantalla.copy(
                centro = result?.centro ?: "",
                subdominio = result?.subdominio ?: "",
                nombreArchivo = result?.nombreArchivo ?: "",
                tipo_disenio = result?.tipo_disenio ?: "",
                id_lista_reproduccion = result?.id_lista_reproduccion ?: 0,
                url_pagina_web = result?.url_pagina_web ?: "",
                duracion_slide = result?.duracion_slide ?: "",
                logo = result?.logo ?: "",
                logo_app = result?.logo_app ?: "",
                color_primario = result?.color_primario ?: "",
                color_secundario = result?.color_secundario ?: "",
                color_boton = result?.color_boton ?: "",
                color_texto = result?.color_texto ?: "",
                color_logo = result?.color_logo ?: "",
                efecto_app = result?.efecto_app ?: "",
                fuente_link = result?.fuente_link ?: "",
                fuente_nombre = result?.fuente_nombre ?: "",
                tituloCentro = result?.tituloCentro ?: "",
                textoLibre = result?.textoLibre ?: "",
                nombreArchivoImgDisenioDos = result?.nombreArchivoImgDisenioDos ?: "",
                nombreArchivoImgDisenioTres = result?.nombreArchivoImgDisenioTres ?: "",
                eventos_texto_agrupado = result?.eventos_texto_agrupado ?: "",
                idSeccion = result?.idSeccion ?: "",
                u_logo_app = result?.u_logo_app ?: "",
                u_color_primario = result?.u_color_primario ?: "",
                u_color_secundario = result?.u_color_secundario ?: "",
                u_color_texto = result?.u_color_texto ?: "",
                u_color_logo = result?.u_color_logo ?: "",
                u_efecto_app = result?.u_efecto_app ?: "",
                video_alerta = result?.video_alerta ?: "",
                time_zone = result?.time_zone ?: "America/Mexico_City",
                tipo_fuente_eventos = result?.tipo_fuente_eventos ?: "",
                rss_adicional = result?.rss_adicional ?: "",
                id_pantalla = result?.id_pantalla ?: "",
                calendario_operativo = result?.calendario_operativo ?: ""
            )
        }
        

    }

    /**
     * Obtiene los recursos descargables de la pantalla
     */
    private fun obtenerRecursosPantalla(): List<String>{
        val recursosPantalla = mutableListOf<String>()
        if (stateInformacionPantalla.nombreArchivo != "" && !validaRecursoPantallaExistente(stateInformacionPantalla.nombreArchivo) ){
            recursosPantalla.add(stateInformacionPantalla.nombreArchivo)
        }
        if (stateInformacionPantalla.logo_app != "" && !validaRecursoPantallaExistente(stateInformacionPantalla.logo_app)){
            recursosPantalla.add(stateInformacionPantalla.logo_app)
        }
        if (stateInformacionPantalla.nombreArchivoImgDisenioDos != "" && !validaRecursoPantallaExistente(stateInformacionPantalla.nombreArchivoImgDisenioDos)){
            recursosPantalla.add(stateInformacionPantalla.nombreArchivoImgDisenioDos)
        }
        if (stateInformacionPantalla.nombreArchivoImgDisenioTres != "" && !validaRecursoPantallaExistente(stateInformacionPantalla.nombreArchivoImgDisenioTres)){
            recursosPantalla.add(stateInformacionPantalla.nombreArchivoImgDisenioTres)
        }
        if (stateInformacionPantalla.u_logo_app != "" && !validaRecursoPantallaExistente(stateInformacionPantalla.u_logo_app)){
            recursosPantalla.add(stateInformacionPantalla.u_logo_app)
        }
        if (stateInformacionPantalla.video_alerta != "" && !validaRecursoPantallaExistente(stateInformacionPantalla.video_alerta)){
            recursosPantalla.add(stateInformacionPantalla.video_alerta)
        }
        return recursosPantalla
    }

    /**
     * Limpia los IDs de las descargas
     */
    fun clearListaIdRecursos(){
        _recursosId.clear()
    }

    private suspend fun obtenerInformacionCalendario(){
        var result: List<InformacionCalendarioModel>? = null

        try {
            result = repository.obtenerInformacionRecursosCalendario(
                stateEveniment.idDispositivo,
                stateInformacionPantalla.tipo_fuente_eventos
            )

            calendario.value = result ?: emptyList()

            informacionPantallaRepository.clear("informacionCalendario")
            informacionPantallaRepository.insert(
                InformacionPantallaDB(
                    concepto = "informacionCalendario",
                    valor = gson.toJson(calendario.value)
                )
            )
        }catch (e: Exception){
            // En caso de algun error buscamos informacion de forma Local.
            val informacionLocal = informacionPantallaRepository.getInformacion("informacionCalendario").firstOrNull()
            if( informacionLocal != null ){
                val calendarioLocal = object : TypeToken<List<InformacionCalendarioModel>>(){}.type
                calendario.value = gson.fromJson(informacionLocal.valor, calendarioLocal)
            }else{
                calendario.value = emptyList()
            }
        }

    }

    /**
     * Obtiene los recursos de la lista de reproduccion principal
     */
    private suspend fun obtenerInformacionRecursos() {
        var result: List<InformacionRecursoModel>? = null

        try {
            result = repository.obtenerInformacionRecursos(
                stateEveniment.idDispositivo,
                stateInformacionPantalla.tipo_fuente_eventos,
                0
            )
            _recursos_tmp.value = result ?: emptyList()

            informacionPantallaRepository.clear("informacionRecursos")
            informacionPantallaRepository.insert(
                InformacionPantallaDB(
                    concepto = "informacionRecursos",
                    valor = gson.toJson(_recursos_tmp.value)
                )
            )
        } catch (e: Exception) {
            // En caso de algun error buscamos informacion de forma Local.
            val informacionLocal = informacionPantallaRepository.getInformacion("informacionRecursos").firstOrNull()
            if( informacionLocal != null ){
                val recursosLista = object : TypeToken<List<InformacionRecursoModel>>(){}.type
                _recursos_tmp.value = gson.fromJson(informacionLocal.valor, recursosLista)
            }else{
                _recursos_tmp.value = emptyList()
            }

        }
    }

    /**
     * Obtiene los recursos de la lista de reproduccion de la plantilla
     */
    private suspend fun obtenerInformacionRecursosPlantilla() {
        var result: List<InformacionRecursoModel>? = null

        try {
            result = repository.obtenerInformacionRecursos(
                stateEveniment.idDispositivo,
                stateInformacionPantalla.tipo_fuente_eventos,
                stateInformacionPantalla.id_lista_reproduccion
            )
            _recursos_plantilla_tmp.value = result ?: emptyList()

            informacionPantallaRepository.clear("informacionRecursosPlantilla")
            informacionPantallaRepository.insert(
                InformacionPantallaDB(
                    concepto = "informacionRecursosPlantilla",
                    valor = gson.toJson(_recursos_plantilla_tmp.value)
                )
            )
        } catch (e: Exception) {
            // Obtener informacion Local
            _recursos_plantilla_tmp.value = emptyList()
            val informacionLocal = informacionPantallaRepository.getInformacion("informacionRecursosPlantilla").firstOrNull()
            if( informacionLocal != null ){
                val recursosLista = object : TypeToken<List<InformacionRecursoModel>>(){}.type
                _recursos_plantilla_tmp.value = gson.fromJson(informacionLocal.valor, recursosLista)
            }else{
                _recursos_plantilla_tmp.value = emptyList()
            }
        }
    }

    /**
     * Obtiene una lista de los recursos que se pueden descargar (Imagenes y Videos).
     */
    private fun obtenerRecursosDescargables( recursos: MutableStateFlow<List<InformacionRecursoModel>> ): List<InformacionRecursoModel>{
        var nuevaListaRecursos: List<InformacionRecursoModel> = emptyList()
        if(recursos.value.isNotEmpty()){
            nuevaListaRecursos = recursos.value.filter { recurso -> recurso.tipo_slide == "video" || recurso.tipo_slide == "imagen" }
        }
        return validaRecursoExistente( obtenerRecursosUnicos(nuevaListaRecursos) )
    }

    private fun obtenerRecursosUnicos(listaRecursos: List<InformacionRecursoModel>): List<InformacionRecursoModel>{
        val nuevaListaRecursos = mutableListOf<InformacionRecursoModel>()
        if(listaRecursos.isNotEmpty()) {
            listaRecursos.forEach { recurso ->
                if( !nuevaListaRecursos.any{ it.idEvento == recurso.idEvento } ){
                    nuevaListaRecursos.add(recurso)
                }
            }
        }
        return nuevaListaRecursos
    }

    /**
     * Valida que los recursos no existan en el dispositivo
     */
    private fun validaRecursoExistente(listaRecursos: List<InformacionRecursoModel>): List<InformacionRecursoModel>{
        val nuevaListaRecursos = mutableListOf<InformacionRecursoModel>()
        if(listaRecursos.isNotEmpty()){
            listaRecursos.forEach{ recurso ->
                val ruta: String = if (recurso.tipo_slide=="imagen") FOLDER_EVENIMENT_IMAGENES else FOLDER_EVENIMENT_VIDEOS
                val recursoNombre: String = obtenerNombreUrl( recurso.datos.toString() )
                val archivo = File("$ruta/$recursoNombre")
                if( !archivo.exists() )
                {
                    nuevaListaRecursos.add(recurso)
                }
            }
        }
        return nuevaListaRecursos
    }

    private fun validaRecursoPantallaExistente( url: String ): Boolean{
        val recursoNombre: String = obtenerNombreUrl( url )
        val archivo = File("$FOLDER_EVENIMENT_DATOS/$recursoNombre")
        return archivo.exists()
    }

    /**
     * Descarga los recursos en funcion a una lista de recursos.
     */
    private fun descargarArchivos(listaRecursos: List<InformacionRecursoModel>){
        if(listaRecursos.isNotEmpty()){
            listaRecursos.forEach{ recurso ->
                val carpeta: String = if (recurso.tipo_slide=="imagen") "Imagenes" else "Videos"
                val recursoDatos: String = recurso.datos.toString()
                val recursoNombre: String = obtenerNombreUrl( recursoDatos )

                // Descarga de recursos
                _recursosId.add( descargar( recursoNombre, recursoDatos, carpeta ) )
            }
        }
        // else{
            // Cambiamos la URL por el PATH Local
            // sustituyeUrlPorPathLocal()
        // }
    }

    private fun descargarArchivosPantalla(listaRecursos: List<String>){
        if(listaRecursos.isNotEmpty()){
            listaRecursos.forEach { recurso ->
                val recursoNombre: String = obtenerNombreUrl( recurso )
                _recursosId.add( descargar( recursoNombre, recurso, "Datos" ) )
            }
        }
        else{
            // Cambiamos la URL por el PATH Local
            sustituyeUrlPorPathLocalPantalla()
        }
    }

    /**
     * Realiza la descarga de un archivo.
     */
    private fun descargar(recursoName: String, recursoUri: String, recursoCarpeta: String): Long{
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(recursoUri))
            .setTitle("Descargando archivos...")
            .setDescription("Espere un momento por favor.")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "/Eveniment/$recursoCarpeta/$recursoName")
        return downloadManager.enqueue(request)
    }

    /**
     * Obtiene la Lista de Reproduccion en turno del Calendario
     */
    private suspend fun obtenerListaReproduccion(): InformacionCalendarioModel? {
        var calendarioEvento: InformacionCalendarioModel? = null
        val timeZone = stateInformacionPantalla.time_zone
        val fechaActual = setTimeZone( System.currentTimeMillis(), timeZone )
        val fechaSiguiente = fechaActual.plusDays(2)

        // Borrar Alarmas del Calendario
        borrarAlarmasCalendario()
        calendarioAlarmaRepository.delete()

        // Recorremos el calendario
        calendario.value.forEach { evento ->
            val fechas = evento.fechas.values.toList()
            if( fechas.isNotEmpty() ){

                // Validamos las Fechas
                fechas.forEachIndexed { index, fecha ->
                    val fecha_ini = stringDateToZoneDateTime(fecha.ini, this.formatter, timeZone)
                    val fecha_fin = stringDateToZoneDateTime(fecha.fin, this.formatter, timeZone)

                    val valorAleatorio = obtenerValorAleatorio(100,999);
                    val indexIni = "$index${valorAleatorio}".toInt()
                    val indexfin = "$index${valorAleatorio+1}".toInt()

                    if( (fechaActual.isEqual(fecha_ini) || fechaActual.isAfter(fecha_ini)) &&
                        fechaActual.isBefore(fecha_fin) ){

                        // Validamos prioridad
                        if (calendarioEvento != null) {
                            if( evento.prioridad_horario >= calendarioEvento!!.prioridad_horario ){
                                calendarioEvento = evento
                            }
                        }else{
                            calendarioEvento = evento
                        }
                    }

                    // Creamos Alarmas para la validacion de la siguiente Lista de Reproducción
                    if (fecha_ini != null && fecha_fin != null) {
                        // Fecha Inicio
                        if( fecha_ini.isAfter(fechaActual) && fecha_ini.isBefore(fechaSiguiente) ){
                            alarmaCalendario( context, fecha_ini, indexIni )                                // Creamos Alerta
                            calendarioAlarmaRepository.insert( CalendarioAlarmaDB( alarmaId = indexIni ) ) // Guardamos ID de Alarma

                        }
                        // Fecha Fin
                        if( fecha_fin.isAfter(fechaActual) && fecha_fin.isBefore(fechaSiguiente) ){
                            alarmaCalendario( context, fecha_fin, indexfin )                                // Creamos Alerta
                            calendarioAlarmaRepository.insert( CalendarioAlarmaDB( alarmaId = indexfin ) ) // Guardamos ID de Alarma
                        }
                    }
                }
            }
        }
        return if (calendarioEvento != null) {
            calendarioEvento
        }else{
            null
        }
    }

    /**
     * Valida si es necesario cambiar la Lista de Reproducción de un Calendario
     */
    private fun obtenerListaReproduccionTurno(): InformacionCalendarioModel? {
        var calendarioEvento: InformacionCalendarioModel? = null
        val timeZone = stateInformacionPantalla.time_zone
        val fechaActual = setTimeZone( System.currentTimeMillis(), timeZone )

        // Recorremos el calendario
        calendario.value.forEach { evento ->
            val fechas = evento.fechas.values.toList()
            if( fechas.isNotEmpty() ){
                // Validamos las Fechas
                fechas.forEach { fecha ->
                    val fecha_ini = stringDateToZoneDateTime(fecha.ini, this.formatter, timeZone)
                    val fecha_fin = stringDateToZoneDateTime(fecha.fin, this.formatter, timeZone)

                    if( (fechaActual.isEqual(fecha_ini) || fechaActual.isAfter(fecha_ini)) &&
                        fechaActual.isBefore(fecha_fin) ){

                        // Validamos prioridad
                        if (calendarioEvento != null) {
                            if( evento.prioridad_horario >= calendarioEvento!!.prioridad_horario ){
                                calendarioEvento = evento
                            }
                        }else{
                            calendarioEvento = evento
                        }
                    }
                }

            }
        }
        return if (calendarioEvento != null) {
            calendarioEvento
        }else{
            null
        }
    }

    /**
     * Borra las alarmas programadas para el Calendario
     */
    private suspend fun borrarAlarmasCalendario(){
        val listaAlarmas = calendarioAlarmaRepository.getInformacion().first()
        if( listaAlarmas.isNotEmpty() ){
            listaAlarmas.forEach { alarma ->
                alarmaCalendarioCancelar(context, alarma.alarmaId)
            }
        }
    }

    /**
     * Obtiene el nombre de una imagen/video apartir de la URL
     */
    private fun obtenerNombreUrl(url: String): String{
        val datos: List<String> = url.split("/")
        return datos.last()
    }

    /**
     * Al finalizar la descarga de todos los recursos, cambiamos la URL por el PATH local donde se almacenan los recursos
     */
    fun sustituyeUrlPorPathLocal(){
        val nuevaListaRecursos = mutableListOf<InformacionRecursoModel>()
        if(_recursos_tmp.value.isNotEmpty()){
            _recursos_tmp.value.forEach{ recurso ->
                val recursoDatosNombre = obtenerNombreUrl( recurso.datos.toString() )
                if(recurso.tipo_slide=="imagen"){
                    recurso.datos = "$FOLDER_EVENIMENT_IMAGENES/$recursoDatosNombre"
                }else if(recurso.tipo_slide=="video"){
                    recurso.datos = "$FOLDER_EVENIMENT_VIDEOS/$recursoDatosNombre"
                }
                nuevaListaRecursos.add( recurso )
            }
        }
        _recursos.value = nuevaListaRecursos
    }

    fun sustituyeUrlPorPathLocalPlantilla(){
        val nuevaListaRecursos = mutableListOf<InformacionRecursoModel>()
        if(_recursos_plantilla_tmp.value.isNotEmpty()){
            _recursos_plantilla_tmp.value.forEach{ recurso ->
                val recursoDatosNombre = obtenerNombreUrl( recurso.datos.toString() )
                if(recurso.tipo_slide=="imagen"){
                    recurso.datos = "$FOLDER_EVENIMENT_IMAGENES/$recursoDatosNombre"
                }else if(recurso.tipo_slide=="video"){
                    recurso.datos = "$FOLDER_EVENIMENT_VIDEOS/$recursoDatosNombre"
                }
                nuevaListaRecursos.add( recurso )
            }
        }
        _recursos_plantilla.value = nuevaListaRecursos
    }

    /**
     * Al finalizar la descarga de todos los recursos de Pantalla, cambiamos la URL por el PATH local donde se almacenan los recursos
     */
    fun sustituyeUrlPorPathLocalPantalla() {
        if (stateInformacionPantalla.nombreArchivo != "" ){
            stateInformacionPantalla = stateInformacionPantalla.copy( nombreArchivo = "$FOLDER_EVENIMENT_DATOS/${obtenerNombreUrl(stateInformacionPantalla.nombreArchivo)}")
        }
        if (stateInformacionPantalla.logo_app != "" ){
            stateInformacionPantalla = stateInformacionPantalla.copy( logo_app = "$FOLDER_EVENIMENT_DATOS/${obtenerNombreUrl(stateInformacionPantalla.logo_app)}")
        }
        if (stateInformacionPantalla.nombreArchivoImgDisenioDos != "" ){
            stateInformacionPantalla = stateInformacionPantalla.copy( nombreArchivoImgDisenioDos = "$FOLDER_EVENIMENT_DATOS/${obtenerNombreUrl(stateInformacionPantalla.nombreArchivoImgDisenioDos)}")
        }
        if (stateInformacionPantalla.nombreArchivoImgDisenioTres != "" ){
            stateInformacionPantalla = stateInformacionPantalla.copy( nombreArchivoImgDisenioTres = "$FOLDER_EVENIMENT_DATOS/${obtenerNombreUrl(stateInformacionPantalla.nombreArchivoImgDisenioTres)}")
        }
        if (stateInformacionPantalla.u_logo_app != "" ){
            stateInformacionPantalla = stateInformacionPantalla.copy( u_logo_app = "$FOLDER_EVENIMENT_DATOS/${obtenerNombreUrl(stateInformacionPantalla.u_logo_app)}")
        }
        if (stateInformacionPantalla.video_alerta != "" ){
            stateInformacionPantalla = stateInformacionPantalla.copy( video_alerta = "$FOLDER_EVENIMENT_DATOS/${obtenerNombreUrl(stateInformacionPantalla.video_alerta)}")
        }
    }

    /**
     * Convierte un color hexadecimal a Color compose
     */
    private fun convertirColoresPantalla(){
        stateEveniment = stateEveniment.copy(
            color_primario = hexToColor(stateInformacionPantalla.color_primario),
            color_secundario = hexToColor(stateInformacionPantalla.color_secundario),
            color_boton = hexToColor(stateInformacionPantalla.color_boton),
            color_texto = hexToColor(stateInformacionPantalla.color_texto),
            color_logo = hexToColor(stateInformacionPantalla.color_logo),
            u_color_primario = hexToColor( if(stateInformacionPantalla.u_color_primario == "") "#FFFFFF" else stateInformacionPantalla.u_color_primario ),
            u_color_secundario = hexToColor( if(stateInformacionPantalla.u_color_secundario == "") "#FFFFFF" else stateInformacionPantalla.u_color_secundario ),
            u_color_texto = hexToColor( if(stateInformacionPantalla.u_color_texto == "") "#FFFFFF" else stateInformacionPantalla.u_color_texto ),
            u_color_logo = hexToColor( if(stateInformacionPantalla.u_color_logo == "") "#FFFFFF" else stateInformacionPantalla.u_color_logo )
        )
    }

    private fun hexToColor(hexString: String): Color {
        return Color(("ff" + hexString.removePrefix("#").lowercase()).toLong(16))
    }

    //-- Funciones para obtener la Hora en funcion a la Zona Horaria
    fun activarTime(){
        cronJobTimer?.cancel()
        cronJobTimer = viewModelScope.launch(Dispatchers.Default) {
            while (true){
                delay(1000) // 1000
                val tiempoActual = setTimeZone( System.currentTimeMillis(), stateInformacionPantalla.time_zone )

                if( fechaActualGeneral == null || fechaActualGeneral != tiempoActual.toLocalDate().toString() ){
                    fechaActualGeneral = tiempoActual.toLocalDate().toString()
                    fechaActualGeneralBand = true
                }
                withContext(Dispatchers.Main) {
                    horaActual = formatTimeHora(tiempoActual)
                    if(fechaActualGeneralBand){
                        println("***Actualizar fecha")
                        fechaActualEspaniol = formatTimeFechaEspaniol(tiempoActual)
                        fechaActualIngles = formatTimeFechaIngles(tiempoActual)
                        fechaActualGeneralBand = false
                    }
                }
            }
        }
    }

    fun detenerTime(){
        cronJobTimer?.cancel()
    }

    private fun determinaOrientacionPantalla(){
        val orientacion = when( stateInformacionPantalla.tipo_disenio ){
            "7","8","9" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        viewModelScope.launch {
            _eventoDeOrientacion.emit(orientacion)
        }
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

    private suspend fun obtenerInformacionRss(){
        var tmpNoticiasRss: List<RssEntry> = emptyList()

        try {
            val result = repository.obtenerInformacionRss(stateEveniment.idDispositivo)
            if (result != null) {
                tmpNoticiasRss = result
                // Almacenamos informacion RSS
                informacionPantallaRepository.clear("informacionRSS")
                informacionPantallaRepository.insert(
                    InformacionPantallaDB(
                        concepto = "informacionRSS",
                        valor = gson.toJson(result)
                    )
                )
            }else{
                throw Exception("Respuesta de la red Nula.")
            }
        }catch (e: Exception){
            // Obtener informacion de manera Local
            val result = informacionPantallaRepository.getInformacion("informacionRSS").firstOrNull()
            if( result != null ){
                val rssLista = object : TypeToken<List<RssEntry>>(){}.type
                tmpNoticiasRss = gson.fromJson(result.valor, rssLista)
            }
        }

        val textNoticias: String = obtenerInformacionRssTitle(tmpNoticiasRss)

        withContext(Dispatchers.Main){
            // Decodificar Mensaje
            noticias_rss = decoderTextRss(stateInformacionPantalla.rss_adicional) + "    •    " + textNoticias
        }
    }

    private fun obtenerInformacionRssTitle(rssNoticias: List<RssEntry>): String{
        val listaNoticias = mutableListOf<String>()
        if( rssNoticias.isNotEmpty() ){
            rssNoticias.forEach { row ->
                val noticias = row.noticias
                if(noticias.isNotEmpty()){
                    noticias.forEach{ noticia ->
                        listaNoticias.add(noticia.value.title)
                    }
                }
            }
        }
        return listaNoticias.joinToString(separator = "    •    ")
    }

    private fun decoderTextRss( cadena: String ): String{
        var texto: String = ""
        if(cadena.isNotEmpty()){
            // Get a Base64 decoder instance
            val decoder: Base64.Decoder = Base64.getDecoder()

            // Decode the Base64 string to a byte array
            val decodedBytes: ByteArray = decoder.decode(cadena)

            // Convert the byte array back to a String (assuming UTF-8 encoding)
            // val decodedString: String = String(decodedBytes, Charsets.UTF_8)
            val decodedString: String = String(decodedBytes)

            texto = decodedString.replace(",","    •    ")
        }
        return texto
    }

    fun solicitaReinicioApp(){
        /*
        viewModelScope.launch {
            // Emite un evento que se ejecuta en el Composable 'HomeView' dentro del LaunchedEffect
            _eventoDeReinicio.emit(Unit)
        }
        */
    }

    /**
     * Valida el calendario en caso de una notificacion de Alarma
     */
    private fun notificarCalendario(){
        viewModelScope.launch(Dispatchers.IO) {
            EmiteNotificacionCalendario.notificacion.collect{
                // Validamos que el tipo de fuente sea Calendario
                if( stateInformacionPantalla.tipo_fuente_eventos == "calendario" ) {
                    actualizarListaReproduccion()
                }
            }
        }
    }

}