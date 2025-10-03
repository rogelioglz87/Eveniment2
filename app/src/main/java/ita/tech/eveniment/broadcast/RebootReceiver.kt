package ita.tech.eveniment.broadcast

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class RebootReceiver: BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        // Obtenemos el DevicePolicyManager
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)

        // Verificamos si somos "Device Owner" y reiniciamos
        if (dpm.isDeviceOwnerApp(context.packageName)) {
            dpm.reboot(adminComponent)
        }
    }
}