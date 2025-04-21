package com.algorand.android.ui.compose.widget.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
private fun PeraCoreIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String
) {
    Image(
        painter = painter,
        colorFilter = ColorFilter.tint(color = PeraTheme.colors.link.primary),
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Composable
fun PeraIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String
) {
    PeraCoreIcon(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription
    )
}

@Composable
fun PeraIconRoundShape(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String
) {
    Box(
        modifier = modifier
            .padding(start = 10.dp)
            .size(40.dp)
            .clip(shape = CircleShape)
            .background(color = PeraTheme.colors.layer.grayLighter)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = imageVector,
            tint = PeraTheme.colors.text.main,
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
            .background(color = PeraTheme.colors.layer.grayLighter)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .height(40.dp)
                .width(40.dp),
            imageVector = imageVector,
            tint = PeraTheme.colors.text.main,
            contentDescription = contentDescription
        )
    }
}
