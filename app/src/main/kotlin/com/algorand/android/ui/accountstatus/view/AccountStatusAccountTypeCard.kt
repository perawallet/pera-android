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

package com.algorand.android.ui.accountstatus.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.AccountStatusTypeDetail
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.RekeyAuthDetail
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraAccountItem
import com.algorand.android.utils.AccountIconDrawable

@Composable
internal fun AccountStatusAccountTypeCard(
    content: AccountStatusDetailViewModel.ViewState.Content,
    onUndoRekeyClick: () -> Unit,
    onCopyAddress: (String) -> Unit,
    onScanAddressesClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PeraTheme.colors.background.primary,
            contentColor = PeraTheme.colors.background.primary
        ),
        border = BorderStroke(2.dp, PeraTheme.colors.layer.grayLighter)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (content.detail is AccountStatusTypeDetail.HdKey) {
                HdKeyWalletItem(content.detail, onScanAddressesClick)
            }
            MainAccountItem(content, onCopyAddress)
        }
        if (content.rekeyAuthDetail != null) {
            RekeyedDetail(content.rekeyAuthDetail, onCopyAddress, onUndoRekeyClick)
        }
    }
}

@Composable
private fun HdKeyWalletItem(
    hdKeyDetail: AccountStatusTypeDetail.HdKey,
    onScanAddressesClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier.size(40.dp),
            bitmap = AccountIconDrawable.create(
                context = LocalContext.current,
                accountIconDrawablePreview = hdKeyDetail.iconDrawablePreview,
                sizeResId = R.dimen.spacing_xxxxlarge
            ).toBitmap().asImageBitmap(),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = hdKeyDetail.walletName,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
    }
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Box(
            modifier = Modifier
                .size(width = 1.dp, height = 64.dp)
                .background(color = PeraTheme.colors.button.strokeColor)
        )
        Spacer(modifier = Modifier.width(36.dp))
        Row(
            modifier = Modifier
                .background(color = PeraTheme.colors.button.square.background, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
                .clickable { onScanAddressesClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_scan_address),
                contentDescription = null,
                tint = PeraTheme.colors.button.square.icon
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.scan_new_addresses),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.button.square.icon
            )
        }
    }
}

@Composable
private fun MainAccountItem(content: AccountStatusDetailViewModel.ViewState.Content, onCopyAddress: (String) -> Unit) {
    AccountItem(
        iconDrawable = content.iconDrawablePreview,
        displayName = content.accountDisplayName,
        onCopyAddress = { onCopyAddress(content.accountDisplayName.accountAddress) },
        endButtonContainer = {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCopyAddress(content.accountDisplayName.accountAddress) },
                painter = painterResource(R.drawable.ic_copy),
                contentDescription = null,
                tint = PeraTheme.colors.text.grayLighter
            )
        }
    )
}

@Composable
private fun RekeyedDetail(
    rekeyAuthDetail: RekeyAuthDetail,
    onCopyAddress: (String) -> Unit,
    onUndoRekeyClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(color = PeraTheme.colors.button.strokeColor)
        )
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.rekeyed_to).uppercase(),
            style = PeraTheme.typography.caption.sansMedium,
            color = PeraTheme.colors.text.grayLighter,
        )
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(color = PeraTheme.colors.button.strokeColor)
        )
    }
    AccountItem(
        modifier = Modifier.padding(16.dp),
        iconDrawable = rekeyAuthDetail.authAddressIcon,
        onCopyAddress = { onCopyAddress(rekeyAuthDetail.authAddressDisplayName.accountAddress) },
        displayName = rekeyAuthDetail.authAddressDisplayName,
        endButtonContainer = {
            if (rekeyAuthDetail.canSignTransaction) {
                Text(
                    modifier = Modifier.clickable { onUndoRekeyClick() },
                    text = stringResource(R.string.undo_rekey),
                    color = PeraTheme.colors.status.positive,
                    style = PeraTheme.typography.footnote.sansMedium
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = PeraTheme.colors.status.negative
                )
            }
        }
    )
}

@Composable
private fun AccountItem(
    modifier: Modifier = Modifier,
    iconDrawable: AccountIconDrawablePreview,
    displayName: AccountDisplayName,
    onCopyAddress: (String) -> Unit,
    endButtonContainer: @Composable (() -> Unit)?
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PeraAccountItem(
            modifier = Modifier.weight(1f),
            iconDrawablePreview = iconDrawable,
            displayName = displayName,
            onCopyAddress = onCopyAddress
        )
        if (endButtonContainer != null) {
            Spacer(modifier = Modifier.width(16.dp))
            endButtonContainer()
        }
    }
}
