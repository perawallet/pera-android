package co.algorand.app.ui.screens

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.nav_home
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import com.algorand.common.ui.theme.PeraTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    navController: NavController,
    snackbarViewModel: SnackbarViewModel,
    tag: String,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize().background(PeraTheme.colors.background)
    ) {
        Text(text = stringResource(Res.string.nav_home), color = PeraTheme.colors.textMain)

        ElevatedButton(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
            onClick = { navController.navigate(QrScannerScreenNavigation) },
            content = {
                Text("Qr scanner")
            }
        )
    }
}