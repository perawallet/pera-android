package co.algorand.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import co.algorand.app.ui.navigation.AppNavigation
import co.algorand.app.ui.theme.AppTheme
import org.koin.compose.KoinContext

@Composable
internal fun App() =
    AppTheme {
        KoinContext {
            MaterialTheme {
                AppNavigation()
            }
        }
    }

internal expect fun openUrl(url: String?)