package co.algorand.app

import androidx.compose.runtime.Composable
import co.algorand.app.ui.navigation.AppNavigation
import com.algorand.common.ui.theme.PeraTheme
import org.koin.compose.KoinContext

@Composable
internal fun App() =
    PeraTheme {
        KoinContext {
            AppNavigation()
        }
    }

internal expect fun openUrl(url: String?)