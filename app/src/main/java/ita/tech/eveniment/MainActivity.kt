package ita.tech.eveniment

import android.Manifest
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ita.tech.eveniment.broadcast.MyDeviceAdminReceiver
import ita.tech.eveniment.navegation.NavManager
import ita.tech.eveniment.services.EvenimentServices
import ita.tech.eveniment.socket.SocketHandler
import ita.tech.eveniment.ui.theme.EvenimentTheme
import ita.tech.eveniment.util.alarmaDeReinicio
import ita.tech.eveniment.util.Constants.Companion.HOST_INTERNET
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val disposables = CompositeDisposable() // Para manejar la suscripción de ReactiveNetwork
    private var disconnectionTimestamp: Long = 0L // 0 significa que estamos conectados

    // Instancia de DevicePolicyManager y ComponentName
    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    val procesoVM: ProcesoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // val context = this

        // -- Activamos alarma de reinicio para la App
        alarmaDeReinicio(this)

        // -- Verificamos si somos "Device Owner" antes de intentar anclar la pantalla
        /*
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)
        */

        // -- Inicia el Monitoreo de la App
        val serviceIntent = Intent(this, EvenimentServices::class.java)
        startService(serviceIntent)

        //-- Inicia Alarmas de Encendido/Apagado (Aun no funciona el ENCENDIDO)
        //-- Checar comando: input keyevent 223
        //-- Lista de comandos: https://gist.github.com/arjunv/2bbcca9a1a1c127749f8dcb6d36fb0bc
        // AlarmaEncendidoApagado.scheduleDailyAlarms(this)

        enableEdgeToEdge()
        setContent {
            // Estado para el permiso de administrador de dispositivo
            // var isAdminActive by remember { mutableStateOf(dpm.isAdminActive(adminComponent)) }

            /**
             * Permisos para la lectura de archivos Locales
             */
            var permission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                )
            }
/*
            val permissionLaucher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted -> permission = isGranted }
            )
*/
            val permissionLaucher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissionsMap ->
                    permission = permissionsMap.values.all { it }
                }
            )
/*
            var permissionWrite by remember {
                mutableStateOf(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
            }

            val permissionLaucherWrite = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted -> permissionWrite = isGranted }
            )
*/
            // Launcher para el permiso de administrador de dispositivo
            /*
            val deviceAdminLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                // Verificamos si el permiso de administrador se concedió después de la solicitud
                isAdminActive = dpm.isAdminActive(adminComponent)
                if (isAdminActive) {
                    // Si se concedió, anclamos la pantalla
                    println("***Si se concedió, anclamos la pantalla")
                    startLockTask()
                }
            }
            */

            // Obtenemos una referencia a la Activity actual
            val activity = LocalContext.current as Activity

            LaunchedEffect(Unit) {
                // Escuchamos el evento de orientacion
                procesoVM.eventoDeOrientacion.collect{ orientacion ->
                    activity.requestedOrientation = orientacion
                }
            }

            LaunchedEffect(key1 = true) {
                // Permiso: Administrador
                /*
                if (!isAdminActive) {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Esta aplicación necesita permisos de administrador para habilitar el modo Kiosco.")
                    }
                    deviceAdminLauncher.launch(intent)
                }
                */

                // Permiso: Almacenamiento
                if( !permission ){
                    // permissionLaucher.launch( Manifest.permission.READ_EXTERNAL_STORAGE )
                    permissionLaucher.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }

                // if( !permissionWrite ){
                    // permissionLaucherWrite.launch( Manifest.permission.WRITE_EXTERNAL_STORAGE )
                // }

                launch(Dispatchers.IO) {
                    // Validamos estatus de red
                    val settings = InternetObservingSettings.builder()
                        .host(HOST_INTERNET)
                        .strategy(SocketInternetObservingStrategy())
                        .build()

                    val disposable = ReactiveNetwork
                        .observeInternetConnectivity(settings)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { connectivity ->
                            if( !connectivity ){
                                if (disconnectionTimestamp == 0L) {
                                    procesoVM.setEstatusInternet(connectivity)
                                    disconnectionTimestamp = System.currentTimeMillis()
                                }
                            }
                            else{
                                if (disconnectionTimestamp > 0L) { // Desconexion previa
                                    val tiempoDesconectado =
                                        System.currentTimeMillis() - disconnectionTimestamp
                                    if (tiempoDesconectado > 10000) { // 10,000 milisegundos = 10 segundos
                                        // Actualizar estado
                                        procesoVM.setEstatusInternet(connectivity)
                                    } else {
                                        // NO SE ACTUALIZA: La desconexión fue demasiado breve
                                    }

                                    // Reseteamos la marca de tiempo porque ya hemos recuperado la conexión.
                                    disconnectionTimestamp = 0L
                                }
                                else{
                                    // En caso de que no exista una desconexion previa actualizamos estatus
                                    procesoVM.setEstatusInternet(connectivity)
                                }
                            }

                        }
                    disposables.add(disposable)
                }

            }

            DisposableEffect(Unit) {
                SocketHandler.setSocket()
                SocketHandler.establishConnection()

                onDispose {
                    println("*** --- DESCONECTAR SOCKET Y ReactiveNetwork")
                    SocketHandler.closeConnection()
                    disposables.clear() // Desuscribe todos los observadores
                }
            }

            EvenimentTheme {
                // if (isAdminActive && permission) {
                if (permission) {
                    NavManager(procesoVM)
                }else {
                    // Muestra una pantalla de espera o de explicación si no hay permisos
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Esperando permisos para continuar...")
                            Button(onClick = {
                                // 4. Lanzamos la solicitud con un ARRAY de los permisos.
                                permissionLaucher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                )
                            }) {
                                Text("Conceder Permisos")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Reiniciar App (Se reemplazo por una Alarma generada en el Broadcast)
        if( intent.action == "ACTION_RESTART_APP" ){
            procesoVM.solicitaReinicioApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

}