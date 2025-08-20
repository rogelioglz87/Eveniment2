package ita.tech.eveniment.broadcast

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.MainActivity

class RestartReceiver: BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context?, intent: Intent?) {

        println("***Reiniciar App 0")
        /*
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = "ACTION_RESTART_APP_"
        }
        context?.startActivity(activityIntent)
        */

        // Se programa una Alarma para abrir la app 1seg despues de cerrar la app.
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            123, // Un request code diferente para no entrar en conflicto
            activityIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000, // 2000 milisegundos = 2 segundos
            pendingIntent
        )

        // Cerramos la App
        android.os.Process.killProcess(android.os.Process.myPid())
    }


}