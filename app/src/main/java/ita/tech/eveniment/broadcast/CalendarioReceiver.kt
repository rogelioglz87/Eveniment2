package ita.tech.eveniment.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.MainActivity
import ita.tech.eveniment.util.EmiteNotificacionCalendario
import ita.tech.eveniment.util.EmiteNotificacionTexto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Notificara si existe un cambio en las listas de reproducción
 */
class CalendarioReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val tipoAlarma = intent?.getStringExtra("TIPO_ALARMA") ?: ""

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when ( tipoAlarma ){
                    "CALENDARIO" -> {
                        EmiteNotificacionCalendario.enviarNotificacion()
                    }
                    "TEXTO" -> {
                        EmiteNotificacionTexto.enviarNotificacion()
                    }
                    else -> {

                    }
                }
            }finally {
                pendingResult.finish()
            }
        }

    }


}