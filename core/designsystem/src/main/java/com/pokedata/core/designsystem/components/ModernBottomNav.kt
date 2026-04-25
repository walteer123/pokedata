package com.pokedata.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pokedata.core.navigation.Route

data class NavItem(
    val route: Any,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val label: String
)

val DefaultNavItems = listOf(
    NavItem(
        route = Route.PokemonList,
        icon = Icons.Default.Home,
        label = "Pokedex"
    ),
    NavItem(
        route = Route.Search,
        icon = Icons.Default.Search,
        label = "Search"
    ),
    NavItem(
        route = Route.Favorites,
        icon = Icons.Default.Favorite,
        label = "Favorites"
    )
)

@Composable
fun ModernBottomNav(
    currentRoute: Route,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
    items: List<NavItem> = DefaultNavItems
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        items.forEach { item ->
            val isSelected = currentRoute::class == item.route::class
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (val route = item.route) {
                        is Route -> onNavigate(route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}
