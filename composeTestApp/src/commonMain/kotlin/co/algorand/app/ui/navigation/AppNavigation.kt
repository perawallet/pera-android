package co.algorand.app.ui.navigation

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.ic_collectibles
import algorand_android.composetestapp.generated.resources.ic_dark_mode
import algorand_android.composetestapp.generated.resources.ic_global
import algorand_android.composetestapp.generated.resources.ic_home
import algorand_android.composetestapp.generated.resources.ic_pera
import algorand_android.composetestapp.generated.resources.ic_settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.algorand.app.ui.screens.DiscoverScreen
import co.algorand.app.ui.screens.HomeScreen
import co.algorand.app.ui.screens.NftsScreen
import co.algorand.app.ui.screens.SettingsScreen
import co.algorand.app.ui.viewmodels.AlgorandBaseViewModel
import co.alogrand.app.ui.navigation.TopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar()
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier)
        },
        bottomBar = {
            NavigationBar {
                bottomNavigationItems().forEachIndexed { _, navigationItem ->
                    NavigationBarItem(
                        selected = navigationItem.route == currentDestination?.route,
                        label = {
                            Text(navigationItem.label)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(navigationItem.icon),
                                contentDescription = navigationItem.label,
                            )
                        },
                        onClick = {
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().navigatorName) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            route = "parentRoute",
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {
            composable(Screen.HomeScreen.route) {
                val backStackEntry = remember(it) { navController.getBackStackEntry("parentRoute") }
                val sharedViewModel: AlgorandBaseViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
                HomeScreen(
                    tag = Screen.HomeScreen.route,
                    navController = navController,
                    algorandBaseViewModel = sharedViewModel,
                )
                SnackBarLayout(sharedViewModel, snackbarHostState)
            }
            composable(Screen.DiscoverScreen.route) {
                val backStackEntry = remember(it) { navController.getBackStackEntry("parentRoute") }
                val sharedViewModel: AlgorandBaseViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
                DiscoverScreen(
                    tag = Screen.DiscoverScreen.route,
                    navController = navController,
                    algorandBaseViewModel = sharedViewModel,
                )
                SnackBarLayout(sharedViewModel, snackbarHostState)
            }
            composable(BottomSheet.BottomNav.route) {
                val backStackEntry = remember(it) { navController.getBackStackEntry("parentRoute") }
                val sharedViewModel: AlgorandBaseViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
                // open or close bottom sheet
                SnackBarLayout(sharedViewModel, snackbarHostState)
            }
            composable(Screen.NftsScreen.route) {
                val backStackEntry = remember(it) { navController.getBackStackEntry("parentRoute") }
                val sharedViewModel: AlgorandBaseViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
                NftsScreen(
                    tag = Screen.NftsScreen.route,
                    navController = navController,
                    algorandBaseViewModel = sharedViewModel,
                )
                SnackBarLayout(sharedViewModel, snackbarHostState)
            }
            composable(Screen.SettingsScreen.route) {
                val backStackEntry = remember(it) { navController.getBackStackEntry("parentRoute") }
                val sharedViewModel: AlgorandBaseViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
                SettingsScreen(
                    tag = Screen.NftsScreen.route,
                    navController = navController,
                    algorandBaseViewModel = sharedViewModel,
                )
                SnackBarLayout(sharedViewModel, snackbarHostState)
            }
        }
    }
}

@Composable
fun SnackBarLayout(
    algorandBaseViewModel: AlgorandBaseViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val snackBarStateFlow by algorandBaseViewModel.snackBarStateFlow.collectAsStateWithLifecycle()
    if (snackBarStateFlow.trim().length > 0) {
        // val context = LocalContext.current
        LaunchedEffect(Unit) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    duration = SnackbarDuration.Short,
                    message = snackBarStateFlow,
                )
                // Toast.makeText(context, snackBarStateFlow, Toast.LENGTH_SHORT).show()
                launch {
                    delay(1000L)
                    algorandBaseViewModel.setSnackBarMessage("")
                }
            }
        }
    }
}

fun bottomNavigationItems(): List<BottomNavigationItem> =
    listOf(
        BottomNavigationItem(
            label = "Home",
            icon = Res.drawable.ic_home,
            route = Screen.HomeScreen.route,
        ),
        BottomNavigationItem(
            label = "Discover",
            icon = Res.drawable.ic_global,
            route = Screen.DiscoverScreen.route,
        ),
        BottomNavigationItem(
            label = "",
            icon = Res.drawable.ic_pera,
            route = BottomSheet.BottomNav.route,
        ),
        BottomNavigationItem(
            label = "NFTs",
            icon = Res.drawable.ic_collectibles,
            route = Screen.NftsScreen.route,
        ),
        BottomNavigationItem(
            label = "Settings",
            icon = Res.drawable.ic_settings,
            route = Screen.SettingsScreen.route,
        ),
    )

data class BottomNavigationItem(
    val label: String = "",
    val icon: DrawableResource = Res.drawable.ic_dark_mode,
    val route: String = "",
)

sealed class Screen(
    val route: String,
) {
    object HomeScreen : Screen("home")

    object SettingsScreen : Screen("settings")

    object DiscoverScreen : Screen("discover")

    object NftsScreen : Screen("nfts")
}

sealed class BottomSheet(
    val route: String,
) {
    object BottomNav : BottomSheet("bottom_nav")
}