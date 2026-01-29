@file:Suppress("EmptyFunctionBlock", "Unused")
/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.inbox.jointaccountinvitation.ui.preview

import androidx.compose.runtime.Composable
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.JointAccountInvitationDetailScreen
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.JointAccountInvitationDetailScreenListener
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.toShortenedAddress
import java.time.ZonedDateTime

@PeraPreviewLightDark
@Composable
fun JointAccountInvitationDetailScreenPreview() {
    PeraTheme {
        val listener = object : JointAccountInvitationDetailScreenListener {
            override fun onBackClick() {}
            override fun onAcceptClick() {}
            override fun onRejectClick() {}
            override fun onCopyAddress(address: String) {}
        }

        val invitation = JointAccountInvitationInboxItem(
            id = "preview_invitation_1",
            accountAddress = "DUA4ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ2345678902ETI",
            accountAddressShortened = "DUA4...2ETI",
            creationDateTime = ZonedDateTime.now(),
            timeDifference = 0L,
            isRead = false,
            threshold = 2,
            participantAddresses = listOf(
                "HZQ73CABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890PSDZZE",
                "tahir.algo",
                "CNSW64ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890C4HNPI"
            )
        )

        val accountDisplayNames = mapOf(
            invitation.accountAddress to AccountDisplayName(
                accountAddress = invitation.accountAddress,
                primaryDisplayName = invitation.accountAddressShortened,
                secondaryDisplayName = null
            ),
            invitation.participantAddresses[0] to AccountDisplayName(
                accountAddress = invitation.participantAddresses[0],
                primaryDisplayName = "HZQ73C...PSDZZE",
                secondaryDisplayName = "Joseph"
            ),
            invitation.participantAddresses[1] to AccountDisplayName(
                accountAddress = invitation.participantAddresses[1],
                primaryDisplayName = "tahir.algo",
                secondaryDisplayName = "DUA4...2ETI"
            ),
            invitation.participantAddresses[2] to AccountDisplayName(
                accountAddress = invitation.participantAddresses[2],
                primaryDisplayName = "CNSW64...C4HNPI",
                secondaryDisplayName = null
            )
        )

        val accountIcons = mapOf(
            invitation.accountAddress to AccountIconDrawablePreviews.getDefaultIconDrawablePreview(),
            invitation.participantAddresses[0] to AccountIconDrawablePreviews.getDefaultIconDrawablePreview(),
            invitation.participantAddresses[1] to AccountIconDrawablePreviews.getDefaultIconDrawablePreview(),
            invitation.participantAddresses[2] to AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
        )

        JointAccountInvitationDetailScreen(
            invitation = invitation,
            accountDisplayNames = accountDisplayNames,
            accountIcons = accountIcons,
            listener = listener
        )
    }
}

@PeraPreviewLightDark
@Composable
fun JointAccountInvitationDetailScreenWithManyParticipantsPreview() {
    PeraTheme {
        val listener = object : JointAccountInvitationDetailScreenListener {
            override fun onBackClick() {}
            override fun onAcceptClick() {}
            override fun onRejectClick() {}
            override fun onCopyAddress(address: String) {}
        }

        val invitation = JointAccountInvitationInboxItem(
            id = "preview_invitation_2",
            accountAddress = "DUA4ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ2345678902ETI",
            accountAddressShortened = "DUA4...2ETI",
            creationDateTime = ZonedDateTime.now(),
            timeDifference = 0L,
            isRead = false,
            threshold = 2,
            participantAddresses = listOf(
                "HZQ73CABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890PSDZZE",
                "JHF7ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890VE2A",
                "KLMN8ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890XYZ1",
                "NOPQ9ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABC2",
                "RSTU0ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890DEF3"
            )
        )

        val accountDisplayNames = invitation.participantAddresses.associateWith { address ->
            AccountDisplayName(
                accountAddress = address,
                primaryDisplayName = address.toShortenedAddress(),
                secondaryDisplayName = null
            )
        } + mapOf(
            invitation.accountAddress to AccountDisplayName(
                accountAddress = invitation.accountAddress,
                primaryDisplayName = invitation.accountAddressShortened,
                secondaryDisplayName = null
            )
        )

        val accountIcons = (listOf(invitation.accountAddress) + invitation.participantAddresses).associateWith {
            AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
        }

        JointAccountInvitationDetailScreen(
            invitation = invitation,
            accountDisplayNames = accountDisplayNames,
            accountIcons = accountIcons,
            listener = listener
        )
    }
}
