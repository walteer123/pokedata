package com.pokedata.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pokedata.core.designsystem.theme.PokedexTheme

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    fillMaxSize: Boolean = true
) {
    Box(
        modifier = if (fillMaxSize) modifier.fillMaxSize() else modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 4.dp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(showBackground = true, backgroundColor = 0xFF201A19, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoadingIndicatorPreview() {
    PokedexTheme {
        LoadingIndicator()
    }
}
