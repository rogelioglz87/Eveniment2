package ita.tech.eveniment.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.broadcast.RebootReceiver
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

@SuppressLint("ServiceCast")
fun alarmaDeReinicioDispositivo(context: Context){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, RebootReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        99999, // Request code único para esta alarma
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 1. Define la zona horaria y el momento actual
    val zoneId = ZoneId.of("America/Chihuahua")
    val now = ZonedDateTime.now(zoneId)

    // 2. Calcula la hora del próximo domingo a las 7 am
    var nextRebootTime = now
        // .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)) // Busca el próximo domingo (o hoy si es domingo) .SUNDAY
        .withHour(9)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

    // 3. Si la hora calculada ya pasó hoy, programa para el siguiente domingo
    if (nextRebootTime.isBefore(now) || nextRebootTime.isEqual(now)) {
        // nextRebootTime = nextRebootTime.plusWeeks(1)
        nextRebootTime = nextRebootTime.plusDays(1)
    }

    // Cancela cualquier alarma previa con el mismo PendingIntent para evitar duplicados
    alarmManager.cancel(pendingIntent)

    // 4. Programa la alarma para que se repita cada 7 días
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        nextRebootTime.toInstant().toEpochMilli(),
        AlarmManager.INTERVAL_DAY, // Intervalo de 7 días (AlarmManager.INTERVAL_DAY * 7)
        pendingIntent
    )
}