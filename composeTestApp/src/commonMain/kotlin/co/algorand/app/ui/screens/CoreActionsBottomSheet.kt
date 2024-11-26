/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package co.algorand.app.ui.screens

import algorand_android.composetestapp.generated.resources.Res
import algorand_android.composetestapp.generated.resources.ic_pera
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.algorand.common.ui.theme.PeraTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreActionsBottomSheet(
    paddingValues: PaddingValues,
    isVisible: MutableState<Boolean>
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(isVisible.value) {
        if (isVisible.value) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }
    if (isVisible.value) {
        ModalBottomSheet(
            onDismissRequest = { isVisible.value = false },
            sheetState = sheetState,
            windowInsets = WindowInsets(bottom = paddingValues.calculateBottomPadding()),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column {
                CoreActionItem(
                    iconRes = Res.drawable.ic_pera,
                    title = "Swap",
                    description = "Convert between popular ASA pairs"
                )

                CoreActionItem(
                    iconRes = Res.drawable.ic_pera,
                    title = "Buy / Sell",
                    description = "See all options to buy or sell your crypto"
                )

                CoreActionItem(
                    iconRes = Res.drawable.ic_pera,
                    title = "Send",
                    description = "Algo or any ASA to another account"
                )

                CoreActionItem(
                    iconRes = Res.drawable.ic_pera,
                    title = "Receive",
                    description = "View or copy your account address"
                )

                CoreActionItem(
                    iconRes = Res.drawable.ic_pera,
                    title = "Stake",
                    description = "Earn rewards by staking your Algos"
                )

                CoreActionItem(
                    iconRes = Res.drawable.ic_pera,
                    title = "Browse DApps",
                    description = "Explore decentralized applications"
                )
            }
        }
    }
}

@Composable
private fun CoreActionItem(
    iconRes: DrawableResource,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(40.dp),
            painter = painterResource(iconRes),
            contentDescription = title
        )
        Spacer(modifier = Modifier.size(10.dp))

        Column {
            Text(
                text = title,
                style = PeraTheme.typography.body.regular.sans,
                maxLines = 2
            )
            Text(
                text = description,
                style = PeraTheme.typography.footnote.sans,
                maxLines = 3
            )
        }
    }
}
