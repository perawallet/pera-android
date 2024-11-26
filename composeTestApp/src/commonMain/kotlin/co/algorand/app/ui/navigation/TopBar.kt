package co.algorand.app.ui.navigation

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.app_bar_header
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.algorand.common.ui.theme.PeraTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = PeraTheme.colors.background,
            titleContentColor = PeraTheme.colors.textMain,
        ),
        title = {
            Text(
                stringResource(resource = Res.string.app_bar_header),
                maxLines = 1,
            )
        },
    )
}