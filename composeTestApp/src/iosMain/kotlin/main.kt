import androidx.compose.ui.window.ComposeUIViewController
import co.algorand.app.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
