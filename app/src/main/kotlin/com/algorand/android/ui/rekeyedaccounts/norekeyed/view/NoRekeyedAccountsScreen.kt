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

package com.algorand.android.ui.rekeyedaccounts.norekeyed.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraTitleText

@Composable
fun NoRekeyedAccountsScreen(onNavBack: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(onNavBack) },
        containerColor = PeraTheme.colors.background.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 32.dp)
        ) {
            PeraIcon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = R.drawable.ic_wallet),
                contentDescription = "icon",
            )
            Spacer(modifier = Modifier.height(24.dp))
            PeraTitleText(text = stringResource(R.string.no_rekeyed_accounts_found))
            Spacer(modifier = Modifier.height(12.dp))
            PeraBodyText(text = stringResource(R.string.we_have_scanned_the_blockchain))
            Spacer(modifier = Modifier.weight(1f))
            PeraSecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.continue_text),
                onClick = onNavBack
            )
        }
    }
}

@Composable
private fun TopBar(onBackIconClick: () -> Unit) {
    IconButton(
        onClick = onBackIconClick
    ) {
        PeraIcon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_left_arrow),
            contentDescription = "back",
        )
    }
}

@Preview
@Composable
private fun PreviewNoRekeyedAccountsScreen() {
    NoRekeyedAccountsScreenPreview()
}
