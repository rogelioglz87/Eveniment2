package ita.tech.eveniment.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TextDifuminado(
    text: String,
    fontSize: TextUnit,
    color: Color,
    colorSombreado: Color,
    fontWeight: FontWeight,
    styleBase: TextStyle = MaterialTheme.typography.bodyLarge
){
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        style = styleBase.copy(
            shadow = Shadow(
                color = colorSombreado,
                offset = Offset(3f, 4f),
                blurRadius = 6f
            )
        )
    )
}