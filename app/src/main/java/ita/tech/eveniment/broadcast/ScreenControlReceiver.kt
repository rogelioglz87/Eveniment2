package ita.tech.eveniment.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ita.tech.eveniment.util.AlarmaEncendidoApagado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.IOException

class ScreenControlReceiver: BroadcastReceiver() {

    companion object {
        const val ACTION_TURN_ON = "ita.tech.eveniment.TURN_ON"
        const val ACTION_TURN_OFF = "ita.tech.eveniment.TURN_OFF"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync() // Para poder usar coroutines
println("***ENCENDER DISPOSITIVO")
        CoroutineScope(Dispatchers.IO).launch {
            when (intent?.action) {
                ACTION_TURN_ON -> {
                    turnScreenOn()
                    // Vuelve a programar la alarma para el día siguiente
                    // AlarmaEncendidoApagado.scheduleDailyAlarm(context!!, isTurnOn = true)
                }
                ACTION_TURN_OFF -> {
                    turnScreenOff()
                    // Vuelve a programar la alarma para el día siguiente
                    // AlarmaEncendidoApagado.scheduleDailyAlarm(context!!, isTurnOn = false)
                }
            }
            pendingResult.finish() // Finaliza la operación asíncrona
        }

    }

    // Función principal para ejecutar cualquier comando como root
    private fun executeRootCommand(command: String) {
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("$command\n")
            os.writeBytes("exit\n")
            os.flush()
            os.close()
            process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // Comprueba si la pantalla está actualmente encendida
    private fun isScreenOn(): Boolean {
        try {
            val process = Runtime.getRuntime().exec("dumpsys power | grep 'mWakefulness='")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            process.waitFor()
            // Si el estado es "Awake", la pantalla está encendida. Si es "Asleep" o "Dozing", está apagada.
            return output.contains("mWakefulness=Awake")
        } catch (e: Exception) {
            e.printStackTrace()
            return false // Asumimos que está apagada si hay un error
        }
    }

    // Apaga la pantalla solo si está encendida
    private fun turnScreenOff() {
        if (isScreenOn()) {
            println("--- Pantalla encendida, apagando...")
            executeRootCommand("input keyevent 26") // Simula el botón de encendido
        } else {
            println("--- Pantalla ya está apagada.")
        }
    }

    // Enciende la pantalla solo si está apagada
    private fun turnScreenOn() {
        executeRootCommand("input keyevent 26") // Simula el botón de encendido
        /*
        if (!isScreenOn()) {
            println("--- Pantalla apagada, encendiendo...")
            executeRootCommand("input keyevent 26") // Simula el botón de encendido
        } else {
            println("--- Pantalla ya está encendida.")
        }
        */
    }


}