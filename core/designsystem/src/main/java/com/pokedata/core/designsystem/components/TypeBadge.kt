package com.pokedata.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pokedata.core.designsystem.theme.PokemonTypeColors
import com.pokedata.core.designsystem.theme.contrastingTextColor
import com.pokedata.core.designsystem.utils.capitalizeFirst

@Composable
fun TypeBadge(
    typeName: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = PokemonTypeColors.getColor(typeName)
    val textColor = backgroundColor.contrastingTextColor()

    Text(
        text = typeName.capitalizeFirst(),
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .semantics { contentDescription = "$typeName type" },
        color = textColor,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        overflow = androidx.compose.ui.text.style.TextOverflow.Clip
    )
}
