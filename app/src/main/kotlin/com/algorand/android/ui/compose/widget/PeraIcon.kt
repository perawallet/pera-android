package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun PeraIconBig(modifier: Modifier, painter: Painter, contentDescription: String) {
    Image(
        painter = painter,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surfaceDim),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
