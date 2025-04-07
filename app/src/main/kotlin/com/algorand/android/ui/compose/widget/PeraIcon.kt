package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PeraIcon(
    modifier: Modifier,
    painter: Painter,
    colorFilter: ColorFilter? = null,
    contentDescription: String
) {
    Image(
        painter = painter,
        colorFilter = colorFilter
            ?: run {
                ColorFilter.tint(color = MaterialTheme.colorScheme.surfaceDim)
            },
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Composable
fun PeraIconRoundShape(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiary,
    contentDescription: String
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(shape = CircleShape)
            .background(color = backgroundColor)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = imageVector,
            tint = color,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun PeraIconRoundShapeBig(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String
) {
    Box(
        modifier = modifier
            .padding(start = 10.dp)
            .size(64.dp)
            .clip(shape = CircleShape)
            .background(color = MaterialTheme.colorScheme.tertiary)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .height(40.dp)
                .width(40.dp),
            imageVector = imageVector,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = contentDescription
        )
    }
}
