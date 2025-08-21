package ita.tech.eveniment.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.MainActivity
import ita.tech.eveniment.util.alarmaDeReinicio

class BootCompletedReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if( intent?.action == Intent.ACTION_BOOT_COMPLETED ){
            println("***Inicia app")
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context?.startActivity(activityIntent)
        }
    }


}