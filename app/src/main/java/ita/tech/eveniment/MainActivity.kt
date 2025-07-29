package ita.tech.eveniment

import android.Manifest
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
import ita.tech.eveniment.navegation.NavManager
import ita.tech.eveniment.socket.SocketHandler
import ita.tech.eveniment.ui.theme.EvenimentTheme
import ita.tech.eveniment.util.Constants.Companion.HOST_INTERNET
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val disposables = CompositeDisposable() // Para manejar la suscripción de ReactiveNetwork

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val procesoVM: ProcesoViewModel by viewModels()
        val carrucelVM: CarrucelViewModel by viewModels()

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
                // SocketHandler.setSocket()
                // SocketHandler.establishConnection()
                if( !permission ){
                    permissionLaucher.launch( Manifest.permission.READ_EXTERNAL_STORAGE )
                }

                // Validamos estatus de red
                /*
                val settings = InternetObservingSettings.builder()
                    .host(HOST_INTERNET)
                    .strategy(SocketInternetObservingStrategy())
                    .build()

                ReactiveNetwork
                    .observeInternetConnectivity(settings)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { connectivity ->
                        procesoVM.setEstatusInternet( connectivity )
                    };
                */
            }

            DisposableEffect(Unit) {
                val settings = InternetObservingSettings.builder()
                    .host(HOST_INTERNET)
                    .strategy(SocketInternetObservingStrategy())
                    .build()

                val disposable = ReactiveNetwork
                    .observeInternetConnectivity(settings)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { connectivity ->
                        procesoVM.setEstatusInternet(connectivity)
                    }
                disposables.add(disposable)

                SocketHandler.setSocket()
                SocketHandler.establishConnection()

                onDispose {
                    println("*** --- DESCONECTAR SOCKET Y ReactiveNetwork")
                    // SocketHandler.closeConnection()
                    disposables.clear() // Desuscribe todos los observadores
                }
            }

            EvenimentTheme {
                NavManager(procesoVM, carrucelVM)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

}