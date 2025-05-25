package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.icon.PeraIconRoundShape
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraTitleText
import com.algorand.wallet.account.utils.toShortenedAddress

@Composable
fun AccountItemView(
    title: String,
    subTitle: String? = null,
    isAddress: Boolean = false,
    accountItemButton: AccountItemButton,
    icon: Int,
    iconTintColor: Color? = null,
    iconBackgroundColor: Color,
    onAccountItemButtonClick: ((AccountItemButton?) -> Unit?)? = null
) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeraIconRoundShape(
            modifier = Modifier,
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            iconTintColor = iconTintColor,
            iconBackgroundColor = iconBackgroundColor,
        )
        Column(modifier = Modifier.weight(1F)) {
            PeraTitleText(
                modifier = Modifier
                    .widthIn(max = 200.dp, min = 20.dp)
                    .padding(start = 16.dp, end = 16.dp),
                text = if (isAddress) title.toShortenedAddress() else title
            )
            subTitle?.let {
                PeraBodyText(
                    modifier = Modifier
                        .widthIn(max = 200.dp, min = 20.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    text = subTitle.toShortenedAddress(),
                )
            }
        }
        when (accountItemButton) {
            AccountItemButton.COPY_CLICK -> {
                PeraIcon(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable {
                            onAccountItemButtonClick?.let { it(AccountItemButton.COPY_CLICK) }
                        },
                    painter = painterResource(id = R.drawable.ic_copy),
                    tintColor = PeraTheme.colors.text.grayLighter,
                    contentDescription = ""
                )
            }

            AccountItemButton.UNDO_REKEY -> {
                PeraBodyText(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            onAccountItemButtonClick?.let { it(AccountItemButton.UNDO_REKEY) }
                        },
                    text = stringResource(R.string.undo_rekey),
                    color = PeraTheme.colors.button.square.icon
                )
            }

            AccountItemButton.SCAN_ADDRESSES -> {}
            AccountItemButton.UNKNOWN -> {}
        }
    }
}
