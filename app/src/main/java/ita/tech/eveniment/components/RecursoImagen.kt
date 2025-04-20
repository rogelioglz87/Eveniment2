package ita.tech.eveniment.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun RecursoImagen( rutaImagen: String, context: Context ){
    AsyncImage(
        model = ImageRequest.Builder(context).data(rutaImagen)
            .crossfade(enable = true).build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}