package ita.tech.eveniment.views.plantillasHorizontales

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import ita.tech.eveniment.components.Carrucel
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.viewModels.ProcesoViewModel

@Composable
fun Plantilla_Horizontal_Trece(
    recursos: List<InformacionRecursoModel>,
    procesoVM: ProcesoViewModel,
    recursosPlantilla: List<InformacionRecursoModel>
){
    var columnWidth by remember { mutableStateOf(1.0f) }
    var tipoSlideActualPrincipal by remember { mutableStateOf("") }

    LaunchedEffect(tipoSlideActualPrincipal) {
        Log.d("*** TIPO SLIDE", tipoSlideActualPrincipal);
        if( tipoSlideActualPrincipal == "sin_publicidad" ){
            columnWidth = 1.0f
        }else{
            columnWidth = 0.80f
        }
    }

    val animatedColumnWidth by animateFloatAsState(
        targetValue = columnWidth,
        animationSpec = tween(durationMillis = 900)
    )

    ConstraintLayout(Modifier.fillMaxSize()) {
        val (contenidoPrincipal, contenidoAnuncios, rss) = createRefs()
        val imgDefault = procesoVM.stateInformacionPantalla.nombreArchivo
        Column(
            modifier = Modifier
                .fillMaxHeight(0.93f)
                .fillMaxWidth()
                .background(Color.Black)
                .constrainAs(contenidoPrincipal) {}
        ) {
            if(procesoVM.stateEveniment.mostrarCarrucel){
                Carrucel(recursos, imgDefault, onTipoSlideChange = { tipoSlide ->
                    // Solo capturamos el tipo de slide en caso de que el carrucel sea el PRINCIPAL
                    tipoSlideActualPrincipal = tipoSlide
                })
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    // CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(100.dp))
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(0.93f)
                .fillMaxWidth(animatedColumnWidth)
                .background(Color.White)
                .constrainAs(contenidoAnuncios) {
                    end.linkTo(parent.end)
                }
        ) {
            if(procesoVM.stateEveniment.mostrarCarrucel){
                Carrucel(recursosPlantilla, imgDefault, onTipoSlideChange = {})
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){}
            }
        }

        //-- RSS
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.07f)
                .constrainAs(rss){
                    bottom.linkTo(parent.bottom)
                },
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = procesoVM.noticias_rss,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        velocity = 60.dp, // Adjust scrolling speed
                    )
            )
        }
    }
}