package com.algorand.android.ui.compose.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PeraTitleText(modifier: Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontFamily = peraSans,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
fun PeraDescriptionText(modifier: Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontFamily = peraSans,
        fontWeight = FontWeight.Normal
    )
}
