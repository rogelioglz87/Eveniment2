package ita.tech.eveniment.services

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {

            while (true) {
                // Verificamos si la App esta abierta
                if (!isMainAppRunning()) {
                    // Comando que inicia de nuevo la App.
                    val command = "am start -n ${packageName}/.MainActivity"

                    try {
                        // 1. Inicia un proceso de superusuario ('su') de forma interactiva
                        val process = Runtime.getRuntime().exec("su")

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

                    } catch (e: IOException) {
                        // Log.e("ROOT_COMMAND", "Excepción al ejecutar comando root", e)
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