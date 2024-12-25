package co.algorand.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.algorand.app.ui.AppViewModel
import co.algorand.app.ui.screens.CoreActionsBottomSheet
import co.algorand.app.ui.screens.PeraTypographyPreviewScreen
import co.algorand.app.ui.screens.PeraTypographyPreviewScreenNavigation
import co.algorand.app.ui.screens.QrScannerScreen
import co.algorand.app.ui.screens.QrScannerScreenNavigation
import com.algorand.common.ui.theme.PeraTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    appViewModel: AppViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val isBottomSheetVisible = remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        appViewModel.initCache(lifecycleOwner.lifecycle)
    }

    Scaffold(
        modifier = Modifier
            .background(color = PeraTheme.colors.background)
            .fillMaxSize(),
        topBar = {
            TopBar()
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier)
        },
        bottomBar = {
            PeraNavigationBar(navController) {
                isBottomSheetVisible.value = true
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Accounts,
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {
            getBottomNavigationGraph(navController, snackbarHostState)
            composable<PeraTypographyPreviewScreenNavigation> {
                PeraTypographyPreviewScreen()
            }
            composable<QrScannerScreenNavigation> {
                QrScannerScreen()
            }
        }
        CoreActionsBottomSheet(paddingValues, isBottomSheetVisible)
    }
}
