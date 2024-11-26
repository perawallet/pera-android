package co.algorand.app

import androidx.compose.runtime.Composable
import co.algorand.app.di.initKoinConfig
import co.algorand.app.ui.navigation.AppNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import com.algorand.common.ui.theme.PeraTheme

@Composable
@Preview
internal fun App() {
    KoinApplication(
        application = initKoinConfig
    ) {
        PeraTheme {
            AppNavigation()
        }
    }
}

internal expect fun openUrl(url: String?)