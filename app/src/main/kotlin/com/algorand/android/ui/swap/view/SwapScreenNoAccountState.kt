/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.swap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.swap.topfive.view.TopSwapPairsContainer
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun SwapScreenNoAccountState(
    scope: CoroutineScope,
    swapViewModel: SwapViewModel,
    topSwapPairsViewModel: TopSwapPairsViewModel,
    onCreateAccountClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLighter, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 62.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.no_account_found),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.create_an_account_to_get),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.gray
            )
            Spacer(modifier = Modifier.height(32.dp))
            PeraPrimaryButton(
                onClick = onCreateAccountClick,
                text = stringResource(R.string.create_account),
                leftIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        tint = PeraTheme.colors.button.primary.text,
                        contentDescription = null
                    )
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    TopSwapPairsContainer(scope, swapViewModel, topSwapPairsViewModel)

    LaunchedEffect(Unit) {
        topSwapPairsViewModel.init()
    }
}
