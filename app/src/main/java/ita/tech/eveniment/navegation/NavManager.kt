package ita.tech.eveniment.navegation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ita.tech.eveniment.viewModels.CarrucelViewModel
import ita.tech.eveniment.viewModels.ProcesoViewModel
import ita.tech.eveniment.viewModels.RecursoVideoModel
import ita.tech.eveniment.views.HomeView
import ita.tech.eveniment.views.SplashScreenView

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavManager(procesoVM: ProcesoViewModel){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "SplashScreen"){
        composable("Home") {
            HomeView(procesoVM, navController)
        }
        composable("SplashScreen") {
            SplashScreenView(navController, procesoVM)
        }
    }

}