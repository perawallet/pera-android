/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package co.algorand.app.ui.navigation

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.ic_collectibles
import algorand_android.composetestapp.generated.resources.ic_global
import algorand_android.composetestapp.generated.resources.ic_home
import algorand_android.composetestapp.generated.resources.ic_pera
import algorand_android.composetestapp.generated.resources.ic_settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.algorand.common.ui.theme.PeraTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PeraNavigationBar(
    navController: NavController,
    displayCoreActionsBottomSheet: () -> Unit
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    NavigationBar(
        containerColor = PeraTheme.colors.tabBarBackground
    ) {
        topLevelRoutes.forEachIndexed { _, navigationItem ->
            NavigationBarItem(
                selected = navigationItem::class.qualifiedName == currentDestination?.route,
                label = {
                    (navigationItem.type as? TopLevelRoute.Type.NavButton)?.let {
                        Text(it.label)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(navigationItem.details.icon),
                        contentDescription = "",
                    )
                },
                onClick = {
                    when (navigationItem.type) {
                        TopLevelRoute.Type.CircularButton -> displayCoreActionsBottomSheet()
                        is TopLevelRoute.Type.NavButton -> {
                            navController.navigate(navigationItem) {
                                popUpTo(navController.graph.findStartDestination().navigatorName) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedTextColor = PeraTheme.colors.tabBarIconActive,
                    unselectedTextColor = PeraTheme.colors.tabBarIconNonActive,
                    selectedIconColor = PeraTheme.colors.tabBarIconActive,
                    unselectedIconColor = PeraTheme.colors.tabBarIconNonActive,
                    selectedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class TopLevelRouteDetails<T : Any>(val name: String, val route: T, val icon: DrawableResource)

private val topLevelRoutes: List<TopLevelRoute> = listOf(Home, Discover, CoreActions, Collectibles, Settings)

sealed interface TopLevelRoute {
    val type: Type
    val details: TopLevelRouteDetails<*>

    sealed interface Type {
        data class NavButton(val label: String) : Type
        data object CircularButton : Type
    }
}

@Serializable
data object Home : TopLevelRoute {
    override val type: TopLevelRoute.Type = TopLevelRoute.Type.NavButton("Home")
    override val details = TopLevelRouteDetails(
        name = "Home",
        route = this,
        icon = Res.drawable.ic_home
    )
}

@Serializable
data object Discover : TopLevelRoute {
    override val type: TopLevelRoute.Type = TopLevelRoute.Type.NavButton("Discover")
    override val details = TopLevelRouteDetails(
        name = "Search",
        route = this,
        icon = Res.drawable.ic_global
    )
}

@Serializable
data object CoreActions : TopLevelRoute {
    override val type: TopLevelRoute.Type = TopLevelRoute.Type.CircularButton
    override val details = TopLevelRouteDetails(
        name = "Pera",
        route = this,
        icon = Res.drawable.ic_pera
    )
}

@Serializable
data object Collectibles : TopLevelRoute {
    override val type: TopLevelRoute.Type = TopLevelRoute.Type.NavButton("NFTs")
    override val details = TopLevelRouteDetails(
        name = "NFTs",
        route = this,
        icon = Res.drawable.ic_collectibles
    )
}

@Serializable
data object Settings : TopLevelRoute {
    override val type: TopLevelRoute.Type = TopLevelRoute.Type.NavButton("Settings")
    override val details = TopLevelRouteDetails(
        name = "Settings",
        route = this,
        icon = Res.drawable.ic_settings
    )
}
