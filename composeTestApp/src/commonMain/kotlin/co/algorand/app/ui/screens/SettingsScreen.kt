package co.algorand.app.ui.screens

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.ic_dark_mode
import algorand_android.composetestapp.generated.resources.ic_light_mode
import algorand_android.composetestapp.generated.resources.nav_settings
import algorand_android.composetestapp.generated.resources.open_github
import algorand_android.composetestapp.generated.resources.theme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.algorand.app.openUrl
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import com.algorand.common.ui.theme.LocalThemeIsDark
import com.algorand.common.ui.theme.PeraTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SettingsScreen(
    navController: NavController,
    snackbarViewModel: SnackbarViewModel,
    tag: String,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize().background(PeraTheme.colors.background)
    ) {
        Text(text = stringResource(Res.string.nav_settings), color = PeraTheme.colors.textMain)

        var isDark by LocalThemeIsDark.current
        val icon = remember(isDark) {
            if (isDark) Res.drawable.ic_light_mode
            else Res.drawable.ic_dark_mode
        }

        ElevatedButton(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
            onClick = { isDark = !isDark },
            content = {
                Icon(vectorResource(icon), contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(Res.string.theme))
            }
        )

        ElevatedButton(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
            onClick = {
                snackbarViewModel.setSnackBarMessage("Snackbar message")
            },
            content = {
                Text("Display snackbar")
            }
        )

        ElevatedButton(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
            onClick = { navController.navigate(PeraTypographyPreviewScreenNavigation) },
            content = {
                Text("Typography")
            }
        )

        TextButton(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
            onClick = { openUrl("https://github.com/perawallet/pera-android") },
        ) {
            Text(stringResource(Res.string.open_github))
        }
    }
}