package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme

@SuppressWarnings("LongMethod")
@Composable
fun AddressCard(
    name: String,
    address: String,
    hdWallet: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp
            ),
        onClick = onClick,
        shape = CardDefaults.outlinedShape,
        border = CardDefaults.outlinedCardBorder(enabled = true),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeraIconRoundShape(
                    modifier = Modifier.padding(start = 16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_wallet),
                    contentDescription = stringResource(R.string.algo_wallet)
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
                            .widthIn(max = 140.dp, min = 20.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        text = nameText
                    )
                }
                if (hdWallet.not()) {
                    PeraIcon(
                        modifier = Modifier.padding(end = 30.dp),
                        painter = painterResource(id = R.drawable.ic_copy),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onTertiaryContainer),
                        contentDescription = ""
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
                            .background(color = MaterialTheme.colorScheme.onTertiaryContainer)
                    ) {
                        // just empty
                    }
                    PeraIconHighlightedText(
                        modifier = Modifier.padding(start = 37.dp),
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
                        color = MaterialTheme.colorScheme.surfaceDim,
                        backgroundColor = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.6F),
                        contentDescription = stringResource(R.string.address)
                    )
                    Column(modifier = Modifier.weight(1F)) {
                        PeraTitleText(
                            modifier = Modifier
                                .widthIn(max = 140.dp, min = 20.dp)
                                .padding(start = 16.dp, end = 16.dp),
                            text = stringResource(R.string.main_address)
                        )
                        PeraBodyText(
                            modifier = Modifier
                                .widthIn(max = 140.dp, min = 20.dp)
                                .padding(start = 16.dp, end = 16.dp),
                            text = address,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun StandardAccountPreview() {
    PeraTheme {
        AddressCard(
            name = "HD Wallet #1",
            address = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            hdWallet = true,
            onClick = { }
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
            onClick = { }
        )
    }
}
