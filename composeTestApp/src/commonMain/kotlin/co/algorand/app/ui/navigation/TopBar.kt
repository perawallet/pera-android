package co.alogrand.app.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import co.algorand.app.ui.theme.md_theme_light_primary
import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.app_bar_header
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = md_theme_light_primary,
            titleContentColor = Color.White,
        ),
        title = {
            Text(
                stringResource(resource = Res.string.app_bar_header),
                maxLines = 1,
            )
        },
    )
}