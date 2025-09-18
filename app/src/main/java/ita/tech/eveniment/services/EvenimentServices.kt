package ita.tech.eveniment.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ita.tech.eveniment.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.IOException

class EvenimentServices: Service() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @SuppressLint("SuspiciousIndentation")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {

            while (true) {
                // Verificamos si la App esta abierta
                if (!isMainAppRunning()) {
                    //-- ANDROID 11
                    /*
                    // Comando que inicia de nuevo la App.
                    val command = "am start -n ${packageName}/.MainActivity"

                        try {
                            // 1. Inicia un proceso de superusuario ('su') de forma interactiva
                            val process = Runtime.getRuntime().exec("/system/xbin/su")

                            // 2. Obtenemos un canal para escribir comandos en el proceso
                            val os = DataOutputStream(process.outputStream)

                            // 3. Escribimos nuestro comando y simulamos un "Enter" con '\n'
                            os.writeBytes("$command\n")
                            os.flush()

                            // 4. Escribimos el comando 'exit' para cerrar la shell de root limpiamente
                            os.writeBytes("exit\n")
                            os.flush()

                            // 5. Esperamos a que el proceso y sus comandos terminen
                            process.waitFor()

                            // Log.d("ROOT_COMMAND", "Comando '$command' ejecutado con código de salida ${process.exitValue()}")
                        }catch (e: IOException){
                            e.message?.let { Log.d("ROOT_COMMAND", it) }
                        }
                    */
                    //-- ANDROID 9
                    val launchIntent = Intent(applicationContext, MainActivity::class.java).apply {
                        // Flags importantes para traer la app al frente o crearla si no existe
                        addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        )
                    }

                    try {
                        startActivity(launchIntent)
                    } catch (e: Exception) {
                        Log.e("WatchdogService", "Falló el intento de startActivity sin root.", e)
                    }
                }

                // Esperamos 5 seg antes de volver a verificar
                delay(5000)
            }
        }

        return START_STICKY
    }

    private fun isMainAppRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // Obtenemos la lista de procesos en ejecución
        val runningProcesses = activityManager.runningAppProcesses ?: return false

        // Verificamos si alguno de los procesos en ejecución coincide con el
        // nombre de nuestro paquete principal.
        // corre en un proceso separado (ej. "ita.tech.eveniment:evenimentServices").
        return runningProcesses.any { it.processName == packageName }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}