package ita.tech.eveniment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun RecursoCCTVMaster(
    path: String,
    isCurrentlyVisible: Boolean,
    isOverlay: Boolean = false
){
    // Procesamos las URLs. El remember asegura que no se re-procese
    // innecesariamente en cada recomposición.
    val urlList = remember(path) {
        path.split("|").filter { it.isNotBlank() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when( urlList.size ){
            1 -> {
                RecursoCCTV(path = urlList[0], isCurrentlyVisible, isOverlay = isOverlay, 1)
            }
            2 -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        RecursoCCTV(path = urlList[0], isCurrentlyVisible, isOverlay = isOverlay, 1)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        RecursoCCTV(path = urlList[1], isCurrentlyVisible, isOverlay = isOverlay, 2)
                    }
                }
            }
            else -> {
                // Para 3 o 4 cámaras usamos una cuadrícula (Grid)
                val rows = if (urlList.size > 2) 2 else 1
                val columns = 2

                Column(Modifier.fillMaxSize()) {
                    for (r in 0 until rows) {
                        Row(Modifier.weight(1f)) {
                            for (c in 0 until columns) {
                                val index = r * columns + c
                                if (index < urlList.size) {
                                    RecursoCCTV(
                                        path = urlList[index],
                                        isCurrentlyVisible,
                                        isOverlay = isOverlay,
                                        index,
                                        modifier = Modifier.weight(1f))
                                }else{
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}