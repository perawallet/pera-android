package co.algorand.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import co.algorand.app.di.initKoinConfig
import co.algorand.app.ui.navigation.AppNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
internal fun App() {
    KoinApplication(
        application = initKoinConfig
    ) {
        MaterialTheme {
            AppNavigation()
        }
    }
}


internal expect fun openUrl(url: String?)