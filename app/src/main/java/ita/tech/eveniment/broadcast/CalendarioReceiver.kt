package ita.tech.eveniment.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.MainActivity
import ita.tech.eveniment.util.EmiteNotificacionCalendario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Notificara si existe un cambio en las listas de reproducción
 */
class CalendarioReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                EmiteNotificacionCalendario.enviarNotificacion()
            }finally {
                pendingResult.finish()
            }
        }

    }


}