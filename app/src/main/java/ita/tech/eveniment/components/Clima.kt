package ita.tech.eveniment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ita.tech.eveniment.R
import ita.tech.eveniment.model.InformacionClimaModel
import ita.tech.eveniment.util.iconClima
import ita.tech.eveniment.util.obtenerDiaAbreviado
import ita.tech.eveniment.util.quitarDecimal

@Composable
fun Clima(
    clima: InformacionClimaModel?,
){

    val context = LocalContext.current
    val descripcionClimaActual = clima?.clima_actual?.condition?.text.toString() ?: "";

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_clima),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                /*
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp, // Esquina superior izquierda (en idiomas LTR)
                        topEnd = 18.dp,   // Esquina superior derecha
                        bottomEnd = 0.dp, // Abajo derecha (cuadrada)
                        bottomStart = 0.dp // Abajo izquierda (cuadrada)
                    )
                )
                */
        )

        // Informacion
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp)
        ) {
            // Informacion en Grados
            Row(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "${quitarDecimal(clima?.clima_actual?.temp_c.toString()).toString()}°",
                        color = Color.White, fontSize = 50.sp, fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black,      // Color de la sombra
                                offset = Offset(4f, 4f),  // Desplazamiento en X y Y (píxeles)
                                blurRadius = 8f          // QUÉ TAN DIFUMINADA ES LA SOMBRA
                            )
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    iconClima( clima?.clima_actual?.condition?.icon.toString(), context, Modifier.fillMaxSize(0.8f) )
                }
            }
            // Informacion en Texto
            Column(
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    clima?.clima_actual?.condition?.text ?: "",
                    color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,      // Color de la sombra
                            offset = Offset(4f, 4f),  // Desplazamiento en X y Y (píxeles)
                            blurRadius = 8f          // QUÉ TAN DIFUMINADA ES LA SOMBRA
                        )
                    )
                )
            }
            // Demas días
            Row(
                modifier = Modifier
                    .weight(0.50f)
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(             // Tu forma redondeada
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    ),
            ){
                Column(
                    modifier = Modifier
                        .weight(0.33f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icono
                    iconClima( clima?.clima_dias_siguientes[1]?.day?.condition?.icon.toString(), context, Modifier.fillMaxSize(0.5f) )
                    // dia
                    Text("${obtenerDiaAbreviado(clima?.clima_dias_siguientes[1]?.date.toString())}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    // Temp
                    Text("${clima?.clima_dias_siguientes[1]?.day?.avgtemp_c.toString()}°", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(0.33f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icono
                    iconClima( clima?.clima_dias_siguientes[2]?.day?.condition?.icon.toString(), context, Modifier.fillMaxSize(0.5f) )
                    // dia
                    Text("${obtenerDiaAbreviado(clima?.clima_dias_siguientes[2]?.date.toString())}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    // Temp
                    Text("${clima?.clima_dias_siguientes[2]?.day?.avgtemp_c.toString()}°", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(0.33f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icono
                    iconClima( clima?.clima_dias_siguientes[3]?.day?.condition?.icon.toString(), context, Modifier.fillMaxSize(0.5f) )
                    // dia
                    Text("${obtenerDiaAbreviado(clima?.clima_dias_siguientes[3]?.date.toString())}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    // Temp
                    Text("${clima?.clima_dias_siguientes[3]?.day?.avgtemp_c.toString()}°", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

            }
        }

    }

}