package ita.tech.eveniment.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.broadcast.RestartReceiver
import java.util.Calendar

fun alarmaDeReinicio( context: Context ){

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Intent que apunta a nuestro RestartReceiver
    val intent = Intent(context, RestartReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0, // Request code
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    /*
    // Configura la hora: 11:00 PM (23:00)
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 6) // 23
        set(Calendar.MINUTE, 0) // 0
        set(Calendar.SECOND, 0)
    }

    // Si ya pasaron las 11:00 PM de hoy, programa la alarma para mañana
    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    */

    // Colocamos una alarma para ejecutarce cada 6 hrs
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        // Añade 6 horas a la hora actual para la primera ejecución
        add(Calendar.HOUR_OF_DAY, 6)
    }

    // Cancela cualquier alarma previa con el mismo PendingIntent para evitar duplicados
    alarmManager.cancel(pendingIntent)

    // Programa una alarma inexacta que se repite todos los días
    // Es más eficiente en cuanto a batería y suficiente para este caso.
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY, // Ejecuta la alarma una vez al día
        // AlarmManager.INTERVAL_HALF_DAY, // En este caso ejecuta la alarma cada 12 hrs
        pendingIntent
    )

}