package ita.tech.eveniment.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.broadcast.ScreenControlReceiver
import java.util.Calendar
import java.util.TimeZone

object AlarmaEncendidoApagado {
    fun scheduleDailyAlarms(context: Context) {
        scheduleDailyAlarm(context, isTurnOn = true)  // Programa el encendido de las 7 AM
        // scheduleDailyAlarm(context, isTurnOn = false) // Programa el apagado de las 6 PM
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleDailyAlarm(context: Context, isTurnOn: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ScreenControlReceiver::class.java).apply {
            action = if (isTurnOn) ScreenControlReceiver.ACTION_TURN_ON else ScreenControlReceiver.ACTION_TURN_OFF
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            if (isTurnOn) 1001 else 1002, // Request code debe ser único por alarma
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 1 PASAR LA ZONA HORARIA
        // 2 PROGRAMRA LOS DÍAS DE ENCENDIDO Y APAGADO POR PANTALLA
        // Nota: Es necesario que el dispositivo tenga la zona horaria correcta
        val timeZone = TimeZone.getTimeZone("America/Chihuahua")
        val calendar = Calendar.getInstance(timeZone).apply {
            timeInMillis = System.currentTimeMillis()
            // set(Calendar.HOUR_OF_DAY, if (isTurnOn) 12 else 11) // 7 AM para encender, 18 (6 PM) para apagar
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 34)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si la hora ya pasó hoy, programa la alarma para mañana
            /*
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
            */
        }

        // Usamos setAlarmClock para asegurar la máxima prioridad y exactitud
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
            pendingIntent
        )

        println("***Alarma programada para ${if (isTurnOn) "ENCENDER" else "APAGAR"} a las: ${calendar.time}")
    }
}