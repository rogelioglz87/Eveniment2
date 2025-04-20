package ita.tech.eveniment.broadcast

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi

class DescargarReceiver(
    private val recursos: List<Long>,
    val onComplete: () -> Unit,
    val onRecursoDescargado:() -> Unit
): BroadcastReceiver() {

    private var registered: Boolean = false
    private val totalRecursos: Int = recursos.size
    private var recursosDescargados: Int = 1

    override fun onReceive(context: Context?, intent: Intent?) {
        if( intent?.action == "android.intent.action.DOWNLOAD_COMPLETE" ){
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

            if( recursos.contains(id) ){
                onRecursoDescargado()
                if( totalRecursos == recursosDescargados ){
                    onComplete()

                    // Volvemos a inciarel contador para que ingrese a la funcion "onComplete" en una descarga posterior.
                    recursosDescargados = 1
                }
                recursosDescargados++
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun register(context: Context){
        if(!registered){
            val filter = IntentFilter()
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            context.registerReceiver(this, filter, Context.RECEIVER_EXPORTED)
            registered = true
        }
    }

    fun unregister(context: Context){
        if(registered){
            context.unregisterReceiver(this)
            registered = false
        }
    }


}