package ita.tech.eveniment.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.broadcast.CalendarioReceiver
import java.time.ZonedDateTime

/**
 * Reutilizaremos esta funcion para crear Alarma de Texto
 */

@SuppressLint("ScheduleExactAlarm")
fun alarmaCalendario(
    context: Context,
    time: ZonedDateTime?,
    idAlarma: Int,
    tipoAlarma: String = "CALENDARIO"
){

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, CalendarioReceiver::class.java).apply {
        putExtra("TIPO_ALARMA", tipoAlarma)
        putExtra("ID_ALARMA", idAlarma)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        idAlarma,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Crea la alarma para que se ejecuta una sola vez.
    if (time != null) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.toInstant().toEpochMilli(),
            pendingIntent
        )
    }

}

fun alarmaCalendarioCancelar(context: Context, idAlarma: Int ){

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, CalendarioReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        idAlarma,
        intent,
        // PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // FLAG_NO_CREATE retorna NULL en caso de no existir la alarma.
    )
    if (pendingIntent != null) {
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}