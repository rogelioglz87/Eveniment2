package ita.tech.eveniment.util

class Constants {

    companion object{
        // Webservices
        const val BASE_URL = "https://serviciows.itatechapps.com/"
        const val AUTH_USER = "itatech_apppantallas"
        const val AUTH_PASS = "|2Ow02*.w{(>;Y/RThaxaxnP"

        // ENDPOINTS
        const val CENTRO_DEFAULT = "1"
        const val ALTA_DISPOSITIVO = "altaPantalla.php"
        const val INFORMACION_PANTALLA = "obtenerInformacionPantalla.php"
        const val INFORMACION_RECURSOS = "obtener_eventos.php"
        const val INFORMACION_RECURSOS_CALENDARIO = "obtener_eventos_calendario.php"
        const val INFORMACION_RSS = "obtener_rss_ws_json.php";

        // SOCKET
        const val SOCKET_URL = "https://servicionj.itatechapps.com/"
        const val TOKEN_IO = "I)JKe|He]7e=!2C!N54c<{^q&I&b8t7O1c\$]k9IW8<6}-y86"

        // Directorios de la App.
        const val FOLDER_EVENIMENT = "/storage/emulated/0/Documents/Eveniment"
        const val FOLDER_EVENIMENT_DATOS = "/storage/emulated/0/Documents/Eveniment/Datos"
        const val FOLDER_EVENIMENT_VIDEOS = "/storage/emulated/0/Documents/Eveniment/Videos"
        const val FOLDER_EVENIMENT_IMAGENES = "/storage/emulated/0/Documents/Eveniment/Imagenes"

        // Estilo de la Fuente (General)
        // Plantilla 1
        const val P1_SIZE_TITULO = 26
        const val P1_SIZE_FECHA = 16

        // Plantilla 2
        const val P2_SIZE_FECHA = 14
        const val P2_SIZE_TITULO = 18

        // HOST PARA VALIDAR QUE LA APP CUENTA CON INTERNET
        const val HOST_INTERNET = "secure.itatechapps.com"

    }

}