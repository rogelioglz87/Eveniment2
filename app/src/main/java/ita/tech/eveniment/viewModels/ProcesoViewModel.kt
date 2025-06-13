package ita.tech.eveniment.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.provider.Settings.Secure
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.repository.EvenimentRepository
import ita.tech.eveniment.state.EvenimentState
import ita.tech.eveniment.state.InformacionPantallaState
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT_DATOS
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT_IMAGENES
import ita.tech.eveniment.util.Constants.Companion.FOLDER_EVENIMENT_VIDEOS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Collections
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ProcesoViewModel @Inject constructor(private val repository: EvenimentRepository) :
    ViewModel() {

    var stateInformacionPantalla by mutableStateOf(InformacionPantallaState())
        private set

    var stateEveniment by mutableStateOf(EvenimentState())
        private set

    private val _recursos_tmp = MutableStateFlow<List<InformacionRecursoModel>>(emptyList()) // Almacenara la lista de recursos mientras se descarga
    private val _recursos = MutableStateFlow<List<InformacionRecursoModel>>(emptyList())     // Almacenara la lista de recursos cuando la tenga hasta el path local.
    val recursos = _recursos.asStateFlow()



    /**
     * Almacena los IDs de los recursos a descargar
     */
    private var _recursosId = mutableListOf<Long>()
    var recursosId = _recursosId

    //-- Variables de tiempo
    private var cronJobTimer by mutableStateOf<Job?>(null)
    var horaActual by mutableStateOf("")
        private set
    var fechaActualEspaniol by mutableStateOf("")
        private set
    var fechaActualIngles by mutableStateOf("")
        private set


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

    fun setEstatusCarpetas( status: Boolean ){
        stateEveniment = stateEveniment.copy(bandCarpetasCreadas = status)
    }

    fun setTotalRecursosDescargados( total: Int ){
        stateEveniment = stateEveniment.copy(totalRecursosDescargados = total)
    }

    fun setBandInicioDescarga( status: Boolean ){
        stateEveniment = stateEveniment.copy(bandInicioDescarga = status)
    }

    /**
     * Obtiene ID unico del dispositivo.
     */
    @SuppressLint("HardwareIds")
    fun obtenerIdDevices(context: Context) {
        stateEveniment = stateEveniment.copy(
            // idDispositivo = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
            idDispositivo = "f4e1c5ef8a4cfeb4"
        )
    }

    /**
     * Crea la estructura de carpetas para almacenar los recursos de la app.
     * Eveniment -> datos
     * Eveniment -> videos
     * Eveniment -> imagenes
     */
    fun crearDirectoriosGenerales(): Boolean{
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
    fun obtenerIpAdress() {
        try {
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        // WIFI
                        if (intf.name.contains("wlan0")) {
                            stateEveniment = stateEveniment.copy( ipAddress = sAddr?.toString() ?: "" )
                        }

                        // ETHERNET
                        if (intf.name.contains("eth0")) {
                            stateEveniment = stateEveniment.copy( ipAddress = sAddr?.toString() ?: "" )
                        }

                        // VPN
                        if (intf.name.contains("tun1")) {
                            stateEveniment = stateEveniment.copy( ipVPN = sAddr?.toString() ?: "" )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR IP DEVICES", e.message.toString())
        }
    }

    fun altaDispositivo(idCetroDefault: String){
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                val result = repository.altaDispositivo(stateEveniment.idDispositivo, idCetroDefault)
                if( result == "1" )
                {
                    stateEveniment = stateEveniment.copy( altaDispositivo = true )
                }
            }
        }
    }

    /**
     * Descarga inicial, descarga recursos de la pantalla y de la lista de reproducción
     */
    fun descargarInformacion(context: Context){
        viewModelScope.launch {

            //-- API Descargamos recursos de la PANTALLA
            obtenerInformacionPantalla()

            // Obtenemos y convertimos los colores de la pantalla
            convertirColoresPantalla()

            // Rotamos la pantalla en caso de ser necesario
            val contextAux = context
            if( contextAux is Activity)
            {
                contextAux.requestedOrientation = determinaOrientacionPantalla()
            }

            // Obtenemos los recursos descargables de la Pantalla (logo, imagen de default, video de alerta, etc...)
            val recursosPantalla: List<String> = obtenerRecursosPantalla()

            //-- API Descargamos recursos de la LISTA DE REPRODUCCION
            obtenerInformacionRecursos()

            // Obtenemos los recursos descargables.
            val recursosDescargables: List<InformacionRecursoModel> = obtenerRecursosDescargables()

            // Almacenamos el Total de recursos a descargar más los recursos de pantalla
            stateEveniment = stateEveniment.copy( totalRecursos = recursosDescargables.size + recursosPantalla.size )

            // Descargamos los recursos de pantalla
            descargarArchivosPantalla(recursosPantalla, context)

            // Descargamos los recursos
            descargarArchivos(recursosDescargables, context)

            // Indicamos el momento en que se inicia la descarga
            stateEveniment = if( stateEveniment.totalRecursos > 0 ){
                stateEveniment.copy( bandInicioDescarga = true )
            } else{
                // Quitamos pantalla de Descarga
                stateEveniment.copy( bandDescargaRecursos = false )
            }

        }
    }

    fun descargarInformacionPantalla(context: Context){
        stateEveniment = stateEveniment.copy(bandDescargaLbl = true)
        viewModelScope.launch {
            //-- API Descargamos recursos de la PANTALLA
            obtenerInformacionPantalla()

            // Obtenemos y convertimos los colores de la pantalla
            convertirColoresPantalla()

            // Rotamos la pantalla en caso de ser necesario
            val contextAux = context
            if( contextAux is Activity)
            {
                contextAux.requestedOrientation = determinaOrientacionPantalla()
            }

            // Obtenemos los recursos descargables de la Pantalla (logo, imagen de default, video de alerta, etc...)
            val recursosPantalla: List<String> = obtenerRecursosPantalla()

            // Almacenamos el Total de recursos a descargar más los recursos de pantalla
            stateEveniment = stateEveniment.copy( totalRecursos = recursosPantalla.size )

            // Descargamos los recursos de pantalla
            descargarArchivosPantalla(recursosPantalla, context)

            // Indicamos el momento en que se inicia la descarga
            if( stateEveniment.totalRecursos > 0 ){
                stateEveniment = stateEveniment.copy( bandInicioDescarga = true )
            } else{
                // Quitamos pantalla de Descarga
                stateEveniment = stateEveniment.copy(bandDescargaLbl = false)
            }
        }
    }


    /**
     * Descarga solo los recursos de la lista de reproducción.
     */
    fun descargarInformacionListaReproduccion(context: Context, carrucelVM: CarrucelViewModel){
        stateEveniment = stateEveniment.copy(bandDescargaLbl = true)
        viewModelScope.launch {
            //-- API Descargamos recursos de la LISTA DE REPRODUCCION
            obtenerInformacionRecursos()

            // Obtenemos los recursos descargables.
            val recursosDescargables: List<InformacionRecursoModel> = obtenerRecursosDescargables()

            // Almacenamos el Total de recursos a descargar más los recursos de pantalla
            stateEveniment = stateEveniment.copy( totalRecursos = recursosDescargables.size )

            // Descargamos los recursos
            descargarArchivos(recursosDescargables, context)

            // Indicamos el momento en que se inicia la descarga
            if( stateEveniment.totalRecursos > 0 ){
                stateEveniment = stateEveniment.copy( bandInicioDescarga = true )
            } else{
                // Quitamos etiqueta de Descarga
                stateEveniment = stateEveniment.copy(bandDescargaLbl = false)
                carrucelVM.resetCarrucel()
            }
        }
    }

    private suspend fun obtenerInformacionPantalla() {
        withContext(Dispatchers.IO) {
            val result = repository.obtenerInformacionPantalla(stateEveniment.idDispositivo)
            stateInformacionPantalla = stateInformacionPantalla.copy(
                centro = result?.centro ?: "",
                subdominio = result?.subdominio ?: "",
                nombreArchivo = result?.nombreArchivo ?: "",
                tipo_disenio = result?.tipo_disenio ?: "",
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

    private suspend fun obtenerInformacionRecursos() {
        withContext(Dispatchers.IO) {
            val result = repository.obtenerInformacionRecursos(
                stateEveniment.idDispositivo,
                stateInformacionPantalla.tipo_fuente_eventos
            )
            _recursos_tmp.value = result ?: emptyList()
        }
    }

    /**
     * Obtiene una lista de los recursos que se pueden descargar (Imagenes y Videos).
     */
    private fun obtenerRecursosDescargables(): List<InformacionRecursoModel>{
        var nuevaListaRecursos: List<InformacionRecursoModel> = emptyList()
        if(_recursos_tmp.value.isNotEmpty()){
            nuevaListaRecursos = _recursos_tmp.value.filter { recurso -> recurso.tipo_slide == "video" || recurso.tipo_slide == "imagen" }
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
    private fun descargarArchivos(listaRecursos: List<InformacionRecursoModel>, context: Context){
        if(listaRecursos.isNotEmpty()){
            listaRecursos.forEach{ recurso ->
                val carpeta: String = if (recurso.tipo_slide=="imagen") "Imagenes" else "Videos"
                val recursoDatos: String = recurso.datos.toString()
                val recursoNombre: String = obtenerNombreUrl( recursoDatos )

                // Descarga de recursos
                _recursosId.add( descargar(context, recursoNombre, recursoDatos, carpeta ) )
            }
        }
        else{
            // Cambiamos la URL por el PATH Local
            sustituyeUrlPorPathLocal()
        }
    }

    private fun descargarArchivosPantalla(listaRecursos: List<String>, context: Context){
        if(listaRecursos.isNotEmpty()){
            listaRecursos.forEach { recurso ->
                val recursoNombre: String = obtenerNombreUrl( recurso )
                _recursosId.add( descargar(context, recursoNombre, recurso, "Datos" ) )
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
    private fun descargar(context: Context, recursoName: String, recursoUri: String, recursoCarpeta: String): Long{
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(recursoUri))
            .setTitle("Descargando archivos...")
            .setDescription("Espere un momento por favor.")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "/Eveniment/$recursoCarpeta/$recursoName")
        return downloadManager.enqueue(request)
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
        cronJobTimer = viewModelScope.launch {
            while (true){
                delay(1000)
                val tiempoActual = setTimeZone( System.currentTimeMillis() )
                horaActual = formatTimeHora(tiempoActual)
                fechaActualEspaniol = formatTimeFechaEspaniol(tiempoActual)
                fechaActualIngles = formatTimeFechaIngles(tiempoActual)
            }
        }
    }

    fun detenerTime(){
        cronJobTimer?.cancel()
    }

    private fun setTimeZone( timer: Long ): ZonedDateTime {
        return ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(timer),
            ZoneId.of(stateInformacionPantalla.time_zone)
        )
    }

    private fun formatTimeHora(time: ZonedDateTime?): String{
        val formato = DateTimeFormatter.ofPattern("HH:mm");
        return if (time != null) {
            time.format(formato)
        }else{
            ""
        }
    }

    private fun formatTimeFechaEspaniol(time: ZonedDateTime?): String{
        val formato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("es", "ES"));
        return time?.format(formato)?.split(" ")?.mapIndexed { index, palabra ->
            when (palabra) {
                "de" -> palabra // Mantener "de" en minúscula
                else -> palabra.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } // Capitalizar otras palabras
            }
        }?.joinToString(" ") ?: ""
    }

    private fun formatTimeFechaIngles(time: ZonedDateTime?): String{
        val formato = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.ENGLISH);
        return if (time != null) {
            time.format(formato)
        }else{
            ""
        }
    }

    private fun determinaOrientacionPantalla(): Int{
        var orientacion: Int = 0
        orientacion = when( stateInformacionPantalla.tipo_disenio ){
            "7","8","9" -> forzarOrientacionVertical()
            else -> forzarOrientacionHorizontal()
        }
        println("*** orientacion $orientacion")
        return orientacion
    }

    // Para forzar la orientación vertical (retrato):
    private fun forzarOrientacionVertical(): Int {
        println("*** orientacion vertical")
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // Para forzar la orientación horizontal (landscape):
    private fun forzarOrientacionHorizontal(): Int {
        println("*** orientacion horizontal")
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

}