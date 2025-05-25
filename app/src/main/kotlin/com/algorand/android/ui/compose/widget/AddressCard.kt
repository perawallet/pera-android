package com.algorand.android.ui.compose.widget

import RekeyDividerSection
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType

@SuppressWarnings("LongMethod")
@Composable
fun AddressCard(
    modifier: Modifier,
    title: String,
    subTitle: String?,
    accountRegistrationType: AccountRegistrationType,
    accountRekeyRegistrationType: AccountRegistrationType?,
    icon: Int,
    iconBackgroundColor: Color,
    onAccountItemButtonClick: (AccountItemButton?) -> Unit,
) {
    val hdWallet = accountRegistrationType == AccountRegistrationType.HdKey

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = CardDefaults.outlinedShape,
        border = BorderStroke(2.dp, PeraTheme.colors.layer.grayLighter),
        colors = CardDefaults.outlinedCardColors(containerColor = PeraTheme.colors.layer.grayLightest),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            if (hdWallet) {
                HdWalletAccountView(
                    title = title,
                    subTitle = subTitle,
                    icon = icon,
                    iconBackgroundColor = iconBackgroundColor,
                    accountItemButton = AccountItemButton.COPY_CLICK,
                    onAccountItemButtonClick = onAccountItemButtonClick
                )
            } else {
                StandardAccount(
                    title = title,
                    subTitle = subTitle,
                    icon = icon,
                    iconBackgroundColor = iconBackgroundColor,
                    onAccountItemButtonClick = onAccountItemButtonClick
                )
            }
            accountRekeyRegistrationType?.let {
                AccountRekeyedTo(
                    title = title,
                    subTitle = subTitle,
                    icon = icon,
                    iconBackgroundColor = iconBackgroundColor,
                    accountRekeyType = it,
                    onAccountItemButtonClick = onAccountItemButtonClick,
                )
            }
        }
    }
}

@Composable
fun StandardAccount(
    title: String,
    subTitle: String?,
    icon: Int,
    iconBackgroundColor: Color,
    onAccountItemButtonClick: (AccountItemButton?) -> Unit
) {
    AccountItemView(
        title = title,
        subTitle = subTitle,
        isAddress = true,
        accountItemButton = AccountItemButton.COPY_CLICK,
        icon = icon,
        iconBackgroundColor = iconBackgroundColor,
        onAccountItemButtonClick = onAccountItemButtonClick
    )
}

@Composable
fun HdWalletAccountView(
    title: String,
    subTitle: String?,
    icon: Int,
    iconBackgroundColor: Color,
    accountItemButton: AccountItemButton,
    onAccountItemButtonClick: (AccountItemButton?) -> Unit
) {
    AccountItemView(
        title = title,
        accountItemButton = AccountItemButton.UNKNOWN,
        icon = R.drawable.ic_wallet,
        iconTintColor = PeraTheme.colors.text.main,
        iconBackgroundColor = PeraTheme.colors.layer.grayLighter,
        onAccountItemButtonClick = onAccountItemButtonClick
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(start = 20.dp)
                .height(65.dp)
                .width(1.dp)
                .background(color = PeraTheme.colors.text.main)
        )
        PeraIconHighlightedText(
            modifier = Modifier
                .padding(start = 37.dp)
                .clickable {
                    onAccountItemButtonClick(AccountItemButton.SCAN_ADDRESSES)
                },
            text = stringResource(R.string.scan_new_addresses)
        )
    }
    AccountItemView(
        title = stringResource(R.string.main_address),
        subTitle = subTitle,
        accountItemButton = accountItemButton,
        icon = icon,
        iconBackgroundColor = iconBackgroundColor,
        onAccountItemButtonClick = onAccountItemButtonClick
    )
}

@SuppressWarnings("LongMethod")
@Composable
fun AccountRekeyedTo(
    title: String,
    subTitle: String?,
    icon: Int,
    iconBackgroundColor: Color,
    accountRekeyType: AccountRegistrationType,
    onAccountItemButtonClick: (AccountItemButton?) -> Unit
) {
    val hdWallet = accountRekeyType == AccountRegistrationType.HdKey
    RekeyDividerSection()
    if (hdWallet) {
        HdWalletAccountView(
            title = title,
            subTitle = subTitle,
            icon = icon,
            iconBackgroundColor = iconBackgroundColor,
            accountItemButton = AccountItemButton.UNDO_REKEY,
            onAccountItemButtonClick = onAccountItemButtonClick
        )
    } else {
        AccountItemView(
            title = title,
            accountItemButton = AccountItemButton.UNDO_REKEY,
            icon = icon,
            isAddress = true,
            iconBackgroundColor = iconBackgroundColor,
            onAccountItemButtonClick = onAccountItemButtonClick
        )
    }
}

enum class AccountItemButton {
    UNKNOWN, UNDO_REKEY, COPY_CLICK, SCAN_ADDRESSES
}

const val ICON_HIGHLIGHTED_TEXT_RADIUS = 30

@Composable
fun PeraIconHighlightedText(modifier: Modifier = Modifier, text: String) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(percent = ICON_HIGHLIGHTED_TEXT_RADIUS))
            .background(PeraTheme.colors.button.square.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_scan_address),
            colorFilter = ColorFilter.tint(color = PeraTheme.colors.button.square.icon),
            contentDescription = text,
            modifier = Modifier
                .padding(start = 14.dp, top = 14.dp, bottom = 14.dp)
                .size(12.dp)
        )
        PeraBodyText(
            modifier = Modifier.padding(
                start = 7.dp, end = 7.dp, top = 3.dp, bottom = 3.dp
            ), text = text, color = PeraTheme.colors.button.square.icon
        )
    }
}
