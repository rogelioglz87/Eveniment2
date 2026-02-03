package ita.tech.eveniment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ita.tech.eveniment.model.DatosAgenda
import ita.tech.eveniment.model.DetalleEvento
import ita.tech.eveniment.util.Constants.Companion.ESPACIO_BOTTOM_TARJETA_TITULO
import ita.tech.eveniment.util.Constants.Companion.ETNP_SIZE_PARRAFO
import ita.tech.eveniment.util.Constants.Companion.ETNP_SIZE_PARRAFO_ESPACIO
import ita.tech.eveniment.util.Constants.Companion.ETNP_SIZE_TITULO
import ita.tech.eveniment.util.Constants.Companion.ETNP_SIZE_PARRAFO_ANCHO

@Composable
fun RecursoTextoTarjetaNoAgrupado(
    datosAgenda: DatosAgenda,
    colorSecundario: Color = Color.Black,
    plantilla: Int = 1
){
    val sizeTitulo = (ETNP_SIZE_TITULO[plantilla] ?: 42).sp
    val sizeParrafo = (ETNP_SIZE_PARRAFO[plantilla] ?: 28).sp
    val sizeParrafoEspacio = (ETNP_SIZE_PARRAFO_ESPACIO[plantilla] ?: 12).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        // Titulo Principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ESPACIO_BOTTOM_TARJETA_TITULO.dp)
        ) {
            datosAgenda.nombre?.let {
                Text(text = it, fontWeight = FontWeight.Bold, fontSize = sizeTitulo, style = MaterialTheme.typography.bodyLarge.copy( lineHeight = 70.sp ) ) // style = TextStyle( lineHeight = 70.sp)
            }
        }

        // Datos
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (datosAgenda.eventos?.isNotEmpty() == true){
                datosAgenda.eventos.forEach { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = sizeParrafoEspacio)
                    ) {
                        DatoFilaNoAgrupado(item, colorSecundario, sizeParrafo, plantilla)
                    }
                }
            }
        }
    }
}


@Composable
fun DatoFilaNoAgrupado(
    item: DetalleEvento,
    colorSecundario: Color = Color.Black,
    sizeParrafo: TextUnit = 28.sp,
    plantilla: Int = 1
){
    Row {
        Column(
            modifier = Modifier
                .weight(ETNP_SIZE_PARRAFO_ANCHO[plantilla]?.get(1) ?: 0.70f )
        ) {
            item.evento?.let { Text(it, fontWeight = FontWeight.Bold, fontSize = sizeParrafo, style = MaterialTheme.typography.bodyLarge.copy( lineHeight = 70.sp )) }
        }
        Column(
            modifier = Modifier
                .weight(ETNP_SIZE_PARRAFO_ANCHO[plantilla]?.get(2) ?: 0.30f)
        ) {
            item.hora?.let {
                if( it.isNotEmpty() ){
                    Text("$it HRS", fontWeight = FontWeight.Bold, fontSize = sizeParrafo, color = colorSecundario, style = MaterialTheme.typography.bodyLarge.copy( lineHeight = 70.sp ))
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item.desc?.let { Text(it, fontWeight = FontWeight.Bold, fontSize = sizeParrafo, style = MaterialTheme.typography.bodyLarge.copy( lineHeight = 70.sp )) }
    }
}