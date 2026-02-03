package ita.tech.eveniment.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ita.tech.eveniment.model.DatosAgenda

@Composable
fun RecursoTexto(
    recursos: List<DatosAgenda>,
    colorSecundario: Color = Color.Black,
    textoAgrupado: String = "si",
    plantilla: Int = 1
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp)
    ){
        if( textoAgrupado == "si" ){
            recursos.forEach { recurso ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.33f)
                ) {
                    RecursoTextoTarjetaAgrupado(recurso, colorSecundario, plantilla)
                }
            }
        }else{
            RecursoTextoTarjetaNoAgrupado(recursos[0], colorSecundario, plantilla)
        }

    }

}