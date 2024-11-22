package co.algorand.app.ui.screens

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.ic_dark_mode
import algorand_android.composetestapp.generated.resources.ic_light_mode
import algorand_android.composetestapp.generated.resources.nav_settings
import algorand_android.composetestapp.generated.resources.open_github
import algorand_android.composetestapp.generated.resources.theme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import co.algorand.app.ui.theme.LocalThemeIsDark
import co.algorand.app.ui.viewmodels.AlgorandBaseViewModel
import co.algorand.app.ui.widgets.SuppressLint
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@SuppressLint("ComposableNaming")
@Composable
fun SettingsScreen(
    navController: NavController,
    algorandBaseViewModel: AlgorandBaseViewModel,
    tag: String,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier =
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(text = stringResource(Res.string.nav_settings))

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

        TextButton(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).widthIn(min = 200.dp),
            onClick = { openUrl("https://github.com/perawallet/pera-android") },
        ) {
            Text(stringResource(Res.string.open_github))
        }
    }
}