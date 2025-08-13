package ita.tech.eveniment

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ita.tech.eveniment.socket.SocketHandler
import ita.tech.eveniment.ui.theme.EvenimentTheme
import ita.tech.eveniment.util.Constants.Companion.HOST_INTERNET
import ita.tech.eveniment.viewModels.ProcesoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val disposables = CompositeDisposable() // Para manejar la suscripción de ReactiveNetwork
    private var disconnectionTimestamp: Long = 0L // 0 significa que estamos conectados

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificamos si somos "Device Owner" antes de intentar anclar la pantalla
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        if (dpm.isDeviceOwnerApp(packageName)) {
            startLockTask()
        }

        val procesoVM: ProcesoViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            /**
             * Permisos para la lectura de archivos Locales
             */
            var permission by remember {
                mutableStateOf(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
            }

            val permissionLaucher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted -> permission = isGranted }
            )

            LaunchedEffect(key1 = true) {
                if( !permission ){
                    permissionLaucher.launch( Manifest.permission.READ_EXTERNAL_STORAGE )
                }

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
                NavManager(procesoVM)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

}