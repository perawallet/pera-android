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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.icon.PeraIconRoundShape
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraTitleText
import com.algorand.wallet.account.utils.toShortenedAddress

@SuppressWarnings("LongMethod")
@Composable
fun AddressCard(
    name: String,
    address: String,
    hdWallet: Boolean = false,
    rekey: Boolean = false,
    onCopyClick: () -> Unit,
    onHdScanNewAddressesClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                16.dp
            ),
        shape = CardDefaults.outlinedShape,
        border = BorderStroke(0.dp, Color.Transparent),
        colors = CardDefaults.outlinedCardColors(containerColor = PeraTheme.colors.layer.grayLightest),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeraIconRoundShape(
                    modifier = Modifier,
                    imageVector = ImageVector.vectorResource(R.drawable.ic_wallet),
                    contentDescription = stringResource(R.string.algo_wallet),
                    iconBackgroundColor = if (hdWallet.not())
                        PeraTheme.colors.icon.trusted.background
                    else PeraTheme.colors.layer.grayLighter
                )
                val nameText = hdWallet.let {
                    when (it) {
                        true -> name
                        false -> address
                    }
                }
                Box(modifier = Modifier.weight(1F)) {
                    PeraTitleText(
                        modifier = Modifier
                            .widthIn(max = 200.dp, min = 20.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        text = nameText
                    )
                }
                if (hdWallet.not()) {
                    PeraIcon(
                        modifier = Modifier
                            .padding(end = 30.dp)
                            .clickable {
                                onCopyClick()
                            },
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = PeraTheme.colors.text.gray),
                    )
                }
            }
            if (hdWallet) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .padding(start = 36.dp)
                            .height(65.dp)
                            .width(1.dp)
                            .background(color = PeraTheme.colors.text.main)
                    ) {
                        // just empty
                    }
                    PeraIconHighlightedText(
                        modifier = Modifier
                            .padding(start = 37.dp)
                            .clickable {
                                onHdScanNewAddressesClick()
                            },
                        text = stringResource(R.string.scan_new_addresses)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PeraIconRoundShape(
                        modifier = Modifier.padding(start = 16.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_wallet_address),
                        contentDescription = stringResource(R.string.address),
                        iconBackgroundColor = PeraTheme.colors.icon.trusted.background
                    )
                    Column(modifier = Modifier.weight(1F)) {
                        PeraTitleText(
                            modifier = Modifier
                                .widthIn(max = 200.dp, min = 20.dp)
                                .padding(start = 16.dp, end = 16.dp),
                            text = stringResource(R.string.main_address)
                        )
                        PeraBodyText(
                            modifier = Modifier
                                .widthIn(max = 200.dp, min = 20.dp)
                                .padding(start = 16.dp, end = 16.dp),
                            text = address.toShortenedAddress(),
                            maxLines = 1
                        )
                    }
                    PeraIcon(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                onCopyClick()
                            },
                        painter = painterResource(id = R.drawable.ic_copy),
                        colorFilter = ColorFilter.tint(color = PeraTheme.colors.text.grayLighter),
                        contentDescription = ""
                    )
                }
            }
            if (rekey) {
                RekeyDividerSection()
            }
        }
    }
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
                start = 7.dp,
                end = 7.dp,
                top = 3.dp,
                bottom = 3.dp
            ),
            text = text,
            maxLines = 1,
            color = PeraTheme.colors.button.square.icon
        )
    }
}

@PreviewLightDark
@Composable
fun PeraIconHighlightedTextPreview() {
    PeraTheme {
        PeraIconHighlightedText(
            modifier = Modifier.padding(start = 37.dp),
            text = stringResource(R.string.scan_new_addresses)
        )
    }
}

@PreviewLightDark
@Composable
fun StandardAccountPreview() {
    PeraTheme {
        AddressCard(
            name = "HD Wallet #1",
            address = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            hdWallet = false,
            onCopyClick = { },
            onHdScanNewAddressesClick = { }
        )
    }
}

@PreviewLightDark
@Composable
fun HDAccountPreview() {
    PeraTheme {
        AddressCard(
            name = "HD Wallet #1",
            address = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            hdWallet = true,
            onCopyClick = { },
            onHdScanNewAddressesClick = { }
        )
    }
}

@PreviewLightDark
@Composable
fun HdRekeyPreview() {
    PeraTheme {
        AddressCard(
            name = "HD Wallet #1",
            address = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            hdWallet = true,
            rekey = true,
            onCopyClick = { },
            onHdScanNewAddressesClick = { }
        )
    }
}
