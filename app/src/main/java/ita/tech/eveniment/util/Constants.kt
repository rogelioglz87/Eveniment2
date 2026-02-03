package ita.tech.eveniment.util

import androidx.compose.ui.unit.sp

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
        const val INFORMACION_CLIMA = "obtener_clima_weatherapi_ws.php"

        // SOCKET
        const val SOCKET_URL = "https://servicionj.itatechapps.com/"
        const val TOKEN_IO = "I)JKe|He]7e=!2C!N54c<{^q&I&b8t7O1c\$]k9IW8<6}-y86"

        // Directorios de la App.
        const val FOLDER_EVENIMENT = "/storage/emulated/0/Documents/Eveniment"
        const val FOLDER_EVENIMENT_DATOS = "/storage/emulated/0/Documents/Eveniment/Datos"
        const val FOLDER_EVENIMENT_VIDEOS = "/storage/emulated/0/Documents/Eveniment/Videos"
        const val FOLDER_EVENIMENT_IMAGENES = "/storage/emulated/0/Documents/Eveniment/Imagenes"

        // HOST PARA VALIDAR QUE LA APP CUENTA CON INTERNET
        // const val HOST_INTERNET = "secure.itatechapps.com"
        const val HOST_INTERNET = "google.com"

        // ---------------------- Diseño Medidas por Plantilla
        data class Configuracion(
            val barra: Float,
            val padding: Int,
            val contenido: Float
        )

        data class DisenioBase(
            val plantillas: Map<Int, Configuracion>,
            val logo: Float
        )

        // MEDIDAS PARA PANTALLA 1080p
        val MEDIDAS = DisenioBase(
            mapOf(
                1 to Configuracion(  0.18f,  10,0.82f )
            ),
            0.5f
        )

        /*
        // MEDIDAS PARA PANTALLA 640x480
        val MEDIDAS = DisenioBase(
            mapOf(
                1 to Configuracion(  0.24f,  5,0.76f )
            ),
            0.6f
        )
        */
        // ---------------------- Estilo de Fuente (General)

        // Plantilla 1
        const val P1_SIZE_TITULO = 22
        const val P1_SIZE_FECHA = 15

        // Plantilla 2
        const val P2_SIZE_FECHA = 14
        const val P2_SIZE_TITULO = 18

        // Plantilla 14
        const val P14_SIZE_FECHA = 17

         // Barra Vertical Cuatro
        const val PBV4_SIZE_TITULO = 22
        const val PBV4_SIZE_FECHA = 15
        const val PBV4_SIZE_ESPACIO1 = 12
        const val PBV4_SIZE_ESPACIO2 = 5

        // Barra Vertical
        const val PBV_SIZE_TITULO = 22
        const val PBV_SIZE_FECHA = 16

        // EVENTO DE TEXTO AGRUPADO
        val ETP_SIZE_TITULO = mapOf(
            1 to 36,
            2 to 39,
            3 to 39,
            4 to 36,
            5 to 38,
            10 to 36,
            11 to 34,
            12 to 36,
            13 to 36,
            14 to 36,
            15 to 36,

            7 to 36,
            8 to 36,
            9 to 36
        )
        val ETP_SIZE_PARRAFO = mapOf(
            1 to 25,
            2 to 26,
            3 to 26,
            4 to 25,
            5 to 26,
            10 to 25,
            11 to 24,
            12 to 25,
            13 to 25,
            14 to 25,
            15 to 25,

            7 to 25,
            8 to 25,
            9 to 25
        )
        val ETP_SIZE_PARRAFO_ESPACIO = mapOf(
            1 to 12,
            2 to 6,
            3 to 6,
            4 to 12,
            5 to 12,
            10 to 12,
            11 to 8,
            12 to 12,
            13 to 12,
            14 to 12,
            15 to 12,

            7 to 10,
            8 to 10,
            9 to 10
        )
        val ETP_SIZE_PARRAFO_ANCHO = mapOf(
            1 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),
            2 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            3 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            4 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),
            5 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),
            10 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),
            11 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.43f ),
            12 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),
            13 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),
            14 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),
            15 to mapOf( 1 to 0.43f, 2 to 0.13f, 3 to 0.44f ),

            7 to mapOf( 1 to 0.43f, 2 to 0.19f, 3 to 0.38f ),
            8 to mapOf( 1 to 0.43f, 2 to 0.19f, 3 to 0.38f ),
            9 to mapOf( 1 to 0.43f, 2 to 0.19f, 3 to 0.38f )
        )

        // EVENTO DE TEXTO NO AGRUPADO
        val ETNP_SIZE_TITULO = mapOf(
            1 to 84,
            2 to 70,
            3 to 70,
            4 to 84,
            5 to 84,
            10 to 84,
            11 to 78,
            12 to 84,
            13 to 84,
            14 to 84,
            15 to 84,

            7 to 60,
            8 to 60,
            9 to 60
        )
        val ETNP_SIZE_PARRAFO = mapOf(
            1 to 50,
            2 to 45,
            3 to 45,
            4 to 50,
            5 to 50,
            10 to 50,
            11 to 40,
            12 to 50,
            13 to 50,
            14 to 50,
            15 to 50,

            7 to 43,
            8 to 43,
            9 to 43
        )
        val ETNP_SIZE_PARRAFO_ESPACIO = mapOf(
            1 to 50,
            2 to 30,
            3 to 30,
            4 to 50,
            5 to 60,
            10 to 50,
            11 to 30,
            12 to 50,
            13 to 50,
            14 to 50,
            15 to 50,

            7 to 80,
            8 to 80,
            9 to 80
        )
        val ETNP_SIZE_PARRAFO_ANCHO = mapOf(
            1 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            2 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            3 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            4 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            5 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            10 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            11 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            12 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            13 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            14 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            15 to mapOf( 1 to 0.70f, 2 to 0.30f ),

            7 to mapOf( 1 to 0.80f, 2 to 0.20f ),
            8 to mapOf( 1 to 0.80f, 2 to 0.20f ),
            9 to mapOf( 1 to 0.80f, 2 to 0.20f )
        )

        // ALTO DE TEXTO, TARJETA AGRUPADO
        const val ALTO_TEXTO_TARJETA_AGRUPADO_TITULO = 38
        const val ALTO_TEXTO_TARJETA_AGRUPADO_PARRAFO = 30
        const val ESPACIO_BOTTOM_TARJETA_TITULO = 20

        const val MARCA_SIZE = 12

        // ---------------------- Estilo de Fuente (Gayosso)
/*
        // Plantilla 1
        const val P1_SIZE_TITULO = 32 // pantalla: 1080p: 32, pantalla: 640x480: 22
        const val P1_SIZE_FECHA = 20 // pantalla: 1080p: 20, pantalla: 640x480: 15

        // Plantilla 2
        const val P2_SIZE_FECHA = 18
        const val P2_SIZE_TITULO = 24

        // Plantilla 14
        const val P14_SIZE_FECHA = 17

        // Barra Vertical Cuatro
        const val PBV4_SIZE_TITULO = 26
        const val PBV4_SIZE_FECHA = 14
        const val PBV4_SIZE_ESPACIO1 = 10
        const val PBV4_SIZE_ESPACIO2 = 1

        // Barra Vertical
        const val PBV_SIZE_TITULO = 34
        const val PBV_SIZE_FECHA = 20

        // EVENTO DE TEXTO AGRUPADO
        val ETP_SIZE_TITULO = mapOf(
            1 to 42,
            2 to 40,
            3 to 40,
            4 to 42,
            5 to 42,
            10 to 42,
            11 to 42,
            12 to 42,
            13 to 42,

            7 to 43,
            8 to 43,
            9 to 44
        )
        val ETP_SIZE_PARRAFO = mapOf(
            1 to 28,
            2 to 26,
            3 to 26,
            4 to 28,
            5 to 28,
            10 to 28,
            11 to 30,
            12 to 28,
            13 to 28,

            7 to 29,
            8 to 29,
            9 to 30
        )
        val ETP_SIZE_PARRAFO_ESPACIO = mapOf(
            1 to 12,
            2 to 6,
            3 to 6,
            4 to 12,
            5 to 12,
            10 to 12,
            11 to 8,
            12 to 12,
            13 to 12,

            7 to 10,
            8 to 10,
            9 to 10
        )
        val ETP_SIZE_PARRAFO_ANCHO = mapOf(
            1 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            2 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            3 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            4 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            5 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            10 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            11 to mapOf( 1 to 0.45f, 2 to 0.12f, 3 to 0.43f ),
            12 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),
            13 to mapOf( 1 to 0.45f, 2 to 0.11f, 3 to 0.44f ),

            7 to mapOf( 1 to 0.45f, 2 to 0.16f, 3 to 0.39f ),
            8 to mapOf( 1 to 0.45f, 2 to 0.16f, 3 to 0.39f ),
            9 to mapOf( 1 to 0.45f, 2 to 0.17f, 3 to 0.38f )
        )

        // EVENTO DE TEXTO NO AGRUPADO
        val ETNP_SIZE_TITULO = mapOf(
            1 to 85, // pantalla: 1080p: 85, pantalla: 640x480: 54
            2 to 78,
            3 to 78,
            4 to 85,
            5 to 85,
            10 to 85,
            11 to 85,
            12 to 88,
            13 to 88,
            14 to 85,
            15 to 85,

            7 to 85,
            8 to 85,
            9 to 86
        )
        val ETNP_SIZE_PARRAFO = mapOf(
            1 to 58, // pantalla: 1080p: 58, pantalla: 640x480: 28
            2 to 54,
            3 to 54,
            4 to 58,
            5 to 58,
            10 to 58,
            11 to 58,
            12 to 60,
            13 to 60,
            14 to 58,
            15 to 58,

            7 to 60,
            8 to 60,
            9 to 64
        )
        val ETNP_SIZE_PARRAFO_ESPACIO = mapOf(
            1 to 16,
            2 to 10,
            3 to 10,
            4 to 16,
            5 to 16,
            10 to 16,
            11 to 8,
            12 to 18,
            13 to 18,
            14 to 16,
            15 to 16,

            7 to 16,
            8 to 16,
            9 to 18
        )
        val ETNP_SIZE_PARRAFO_ANCHO = mapOf(
            1 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            2 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            3 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            4 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            5 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            10 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            11 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            12 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            13 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            14 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            15 to mapOf( 1 to 0.70f, 2 to 0.30f ),

            7 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            8 to mapOf( 1 to 0.70f, 2 to 0.30f ),
            9 to mapOf( 1 to 0.68f, 2 to 0.32f )
        )

        // ALTO DE TEXTO, TARJETA AGRUPADO
        const val ALTO_TEXTO_TARJETA_AGRUPADO_TITULO = 24
        const val ALTO_TEXTO_TARJETA_AGRUPADO_PARRAFO = 24
        const val ESPACIO_BOTTOM_TARJETA_TITULO = 5

        // Marca ITA TECH
        const val MARCA_SIZE = 14  // pantalla: 1080p: 14, pantalla: 640x480: 9
*/
    }

}