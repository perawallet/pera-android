@file:Suppress("EmptyFunctionBlock", "Unused")
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

package com.algorand.android.modules.addaccount.joint.creation.ui.editname.preview

import androidx.compose.runtime.Composable
import com.algorand.android.R
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.modules.addaccount.joint.creation.ui.editname.EditAccountNameScreen
import com.algorand.android.modules.addaccount.joint.creation.ui.editname.EditAccountNameScreenListener
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme

@PeraPreviewLightDark
@Composable
fun EditAccountNameScreenPreview() {
    PeraTheme {
        val listener = object : EditAccountNameScreenListener {
            override fun onBackClick() {}
            override fun onDoneClick(name: String) {}
            override fun onRemoveClick() {}
        }
        EditAccountNameScreen(
            account = getMockAccount(),
            listener = listener
        )
    }
}

@PeraPreviewLightDark
@Composable
fun EditAccountNameScreenWithNamePreview() {
    PeraTheme {
        val listener = object : EditAccountNameScreenListener {
            override fun onBackClick() {}
            override fun onDoneClick(name: String) {}
            override fun onRemoveClick() {}
        }
        EditAccountNameScreen(
            account = getMockAccountWithName(),
            listener = listener
        )
    }
}

@PeraPreviewLightDark
@Composable
fun EditAccountNameScreenContactPreview() {
    PeraTheme {
        val listener = object : EditAccountNameScreenListener {
            override fun onBackClick() {}
            override fun onDoneClick(name: String) {}
            override fun onRemoveClick() {}
        }
        EditAccountNameScreen(
            account = getMockContact(),
            listener = listener
        )
    }
}

private fun getMockAccount(): SelectedJointAccountItem {
    return SelectedJointAccountItem(
        accountDisplayName = AccountDisplayName(
            accountAddress = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890",
            primaryDisplayName = "Account 1",
            secondaryDisplayName = "ABCD...7890"
        ),
        iconDrawablePreview = AccountIconDrawablePreview(
            backgroundColorResId = R.color.layer_gray_lighter,
            iconTintResId = R.color.text_gray,
            iconResId = AccountIconResource.CONTACT.iconResId
        ),
        isContact = false
    )
}

private fun getMockAccountWithName(): SelectedJointAccountItem {
    return SelectedJointAccountItem(
        accountDisplayName = AccountDisplayName(
            accountAddress = "BCDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890A",
            primaryDisplayName = "My Wallet",
            secondaryDisplayName = "Ledger Account"
        ),
        iconDrawablePreview = AccountIconDrawablePreview(
            backgroundColorResId = R.color.layer_gray_lighter,
            iconTintResId = R.color.text_gray,
            iconResId = AccountIconResource.CONTACT.iconResId
        ),
        isContact = false
    )
}

private fun getMockContact(): SelectedJointAccountItem {
    return SelectedJointAccountItem(
        accountDisplayName = AccountDisplayName(
            accountAddress = "CDEFGHIJKLMNOPQRSTUVWXYZ234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ234567890AB",
            primaryDisplayName = "John Doe",
            secondaryDisplayName = "CDEF...90AB"
        ),
        imageUri = null,
        isContact = true
    )
}
