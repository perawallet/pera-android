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

package com.algorand.android.ui.compose.widget.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.PeraToolbarLinkText
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@PeraPreviewLightDark
@Composable
fun PeraToolbarBasicPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "Menu"
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarWithStartIconPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "Add Account",
                startContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_left_arrow,
                        modifier = Modifier.clickableNoRipple {}
                    )
                }
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarWithEndIconsPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "Menu",
                endContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_qr_scan,
                        modifier = Modifier.clickableNoRipple {}
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_settings,
                        modifier = Modifier.clickableNoRipple {}
                    )
                }
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarWithStartAndEndIconsPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "Review Transaction",
                startContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_close,
                        modifier = Modifier.clickableNoRipple {}
                    )
                },
                endContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_settings,
                        modifier = Modifier.clickableNoRipple {}
                    )
                }
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarWithLongTextPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "This is a very long toolbar title that might wrap"
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarEmptyTextPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "",
                startContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_left_arrow,
                        modifier = Modifier.clickableNoRipple {}
                    )
                },
                endContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_share,
                        modifier = Modifier.clickableNoRipple {}
                    )
                }
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarWithTwoLineTextPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "Algo Wallet",
                secondaryText = "DUA4...2ESM",
                startContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_left_arrow,
                        modifier = Modifier.clickableNoRipple {}
                    )
                },
                endContainer = {
                    AccountIcon(
                        modifier = Modifier.size(28.dp),
                        iconDrawablePreview = AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
                    )
                }
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarLargeTextPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "Title",
                textStyle = com.algorand.android.ui.compose.widget.PeraToolbarTextStyle.Large,
                startContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_plus,
                        modifier = Modifier.clickableNoRipple {}
                    )
                },
                endContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_plus,
                        modifier = Modifier.clickableNoRipple {}
                    )
                }
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarWithRightLabelPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                text = "Placeholder",
                startContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_plus,
                        modifier = Modifier.clickableNoRipple {}
                    )
                },
                endContainer = {
                    PeraToolbarLinkText(text = "Label")
                }
            )
            PreviewSeparator()
        }
    }
}

@PeraPreviewLightDark
@Composable
fun PeraToolbarWithCustomCenterPreview() {
    PeraTheme {
        Column {
            PeraToolbar(
                startContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_plus,
                        modifier = Modifier.clickableNoRipple {}
                    )
                },
                centerContainer = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Placeholder",
                            style = PeraTheme.typography.body.regular.sansMedium,
                            color = PeraTheme.colors.text.main
                        )
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            tint = PeraTheme.colors.helper.positive
                        )
                    }
                },
                endContainer = {
                    PeraToolbarIcon(
                        iconResId = R.drawable.ic_plus,
                        modifier = Modifier.clickableNoRipple {}
                    )
                }
            )
            PreviewSeparator()
        }
    }
}

@Composable
private fun PreviewSeparator() {
    Spacer(
        modifier = Modifier
            .background(color = Color.Black)
            .height(12.dp)
            .fillMaxWidth()
    )
}
