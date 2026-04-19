package com.pokedata.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokedata.core.designsystem.theme.PokemonTypeColors
import com.pokedata.core.designsystem.utils.capitalizeFirst

@Composable
fun TypeFilterChips(
    types: List<String>,
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(types) { type ->
            val isSelected = selectedType == type
            val typeColor = PokemonTypeColors.getColor(type)

            FilterChip(
                selected = isSelected,
                onClick = {
                    onTypeSelected(if (isSelected) null else type)
                },
                label = {
                    Text(
                        text = type.capitalizeFirst(),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                modifier = Modifier.height(32.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = typeColor,
                    selectedLabelColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) typeColor else MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
    }
}
