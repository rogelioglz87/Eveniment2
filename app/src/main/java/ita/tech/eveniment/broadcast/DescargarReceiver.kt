package ita.tech.eveniment.broadcast

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi

class DescargarReceiver(
    private val getIdsDescarga: () -> Set<Long>,
    private val totalRecursos: Int,
    val onComplete: () -> Unit,
    val onRecursoDescargado:(descargados: Int) -> Unit
): BroadcastReceiver() {

    private var registered: Boolean = false
    private var recursosDescargados: Int = 0

    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

            if (id == -1L) return

            // Consulta los IDs EN EL MOMENTO que llega el broadcast
            val idsActuales = getIdsDescarga()
            if (!idsActuales.contains(id)) return

            val downloadManager =
                context?.let { it.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
            val cursor = downloadManager?.query(DownloadManager.Query().setFilterById(id))

            // Validamos si la descarga fue exitosa
            cursor?.use {
                if (it.moveToFirst()) {
                    val status = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            recursosDescargados++
                            onRecursoDescargado(recursosDescargados)
                            println("---- Descarga recursosDescargados: $recursosDescargados")
                            if (recursosDescargados >= totalRecursos) {
                                println("---- Descarga FINALIZAR")
                                onComplete()
                                // Volvemos a inciarel contador para que ingrese a la funcion "onComplete" en una descarga posterior.
                                recursosDescargados = 0
                            }
                        }

                        DownloadManager.STATUS_FAILED -> {
                            println("---- Descarga fallida: $id")
                            //-- Borramos archivo con Falla
                            downloadManager.remove(id)

                            //-- Contabilizamos las fallidas para no dejar la pantalla congelada
                            recursosDescargados++
                            if (recursosDescargados >= totalRecursos) {
                                onComplete()
                                recursosDescargados = 0
                            }
                        }
                    }
                }
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