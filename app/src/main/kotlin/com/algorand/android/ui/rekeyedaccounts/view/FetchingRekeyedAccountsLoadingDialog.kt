@file:Suppress("MagicNumber")
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

package com.algorand.android.ui.rekeyedaccounts.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.algorand.android.ui.compose.widget.text.PeraBodyText

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun FetchingRekeyedAccountsLoadingDialog() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {}
            .background(color = PeraTheme.colors.background.backdropModal),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .size(width = 200.dp, height = 150.dp)
                .background(color = PeraTheme.colors.background.primary, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 40.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PeraCircularProgressIndicator(
                modifier = Modifier
                    .width(36.dp)
                    .height(36.dp)
            )
            PeraBodyText(
                text = stringResource(R.string.fetching_rekeyed_accounts),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp),
                color = PeraTheme.colors.text.main
            )
        }
    }
}
