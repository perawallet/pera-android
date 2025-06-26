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

package com.algorand.android.ui.spotbanner.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
internal fun BackupPassphraseSpotBanner(modifier: Modifier) {
    Row(
        modifier = modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(48.dp)
                .background(PeraTheme.colors.helper.negativeLighter, shape = CircleShape)
                .padding(12.dp),
            painter = painterResource(R.drawable.ic_rekey_shield),
            tint = PeraTheme.colors.helper.negative,
            contentDescription = null
        )
        SpotBannerText(
            text = stringResource(R.string.you_need_to_backup_passphrase),
            color = PeraTheme.colors.helper.negative
        )
        Icon(
            modifier = Modifier
                .size(36.dp)
                .background(PeraTheme.colors.layer.grayLighter, shape = CircleShape)
                .padding(8.dp),
            painter = painterResource(R.drawable.ic_right_arrow),
            tint = PeraTheme.colors.text.gray,
            contentDescription = null
        )
    }
}
