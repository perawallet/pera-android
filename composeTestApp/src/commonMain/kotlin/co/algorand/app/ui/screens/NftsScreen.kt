package co.algorand.app.ui.screens

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.nav_nfts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import co.algorand.app.ui.viewmodels.AlgorandBaseViewModel
import co.algorand.app.ui.widgets.SuppressLint
import org.jetbrains.compose.resources.stringResource

@SuppressLint("ComposableNaming")
@Composable
fun NftsScreen(
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
            .background(Color.White),
    ) {
        Text(text = stringResource(Res.string.nav_nfts))
    }
}