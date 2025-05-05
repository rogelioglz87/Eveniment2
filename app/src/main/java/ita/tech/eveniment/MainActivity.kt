package ita.tech.eveniment

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.ui.text.font.FontVariation
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import ita.tech.eveniment.broadcast.BootCompletedReceiver
import ita.tech.eveniment.navegation.NavManager
import ita.tech.eveniment.socket.SocketHandler
import ita.tech.eveniment.ui.theme.EvenimentTheme
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import ita.tech.eveniment.viewModels.RecursoVideoModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
                SocketHandler.setSocket()
                SocketHandler.establishConnection()
                permissionLaucher.launch( Manifest.permission.READ_EXTERNAL_STORAGE )
            }

            DisposableEffect(Unit) {
                onDispose {
                    println("*** --- DESCONECTAR SOCKET")
                    SocketHandler.closeConnection()
                }
            }

            EvenimentTheme {
                NavManager(procesoVM, carrucelVM)
            }
        }
    }

}