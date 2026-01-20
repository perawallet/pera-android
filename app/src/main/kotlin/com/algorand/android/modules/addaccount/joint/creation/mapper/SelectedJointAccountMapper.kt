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

package com.algorand.android.modules.addaccount.joint.creation.mapper

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject

class SelectedJointAccountMapper @Inject constructor() {

    fun mapFromAccountItem(item: JointAccountSelectionListItem.AccountItem): SelectedJointAccountItem {
        return SelectedJointAccountItem(
            accountDisplayName = AccountDisplayName(
                accountAddress = item.address,
                primaryDisplayName = item.displayName,
                secondaryDisplayName = item.secondaryDisplayName
            ),
            iconDrawablePreview = item.iconDrawablePreview,
            isContact = false
        )
    }

    fun mapFromContactItem(item: JointAccountSelectionListItem.ContactItem): SelectedJointAccountItem {
        return SelectedJointAccountItem(
            accountDisplayName = AccountDisplayName(
                accountAddress = item.address,
                primaryDisplayName = item.displayName,
                secondaryDisplayName = item.address.toShortenedAddress()
            ),
            imageUri = item.imageUri,
            isContact = true
        )
    }

    fun mapFromNfdItem(item: JointAccountSelectionListItem.NfdItem): SelectedJointAccountItem {
        return SelectedJointAccountItem(
            accountDisplayName = AccountDisplayName(
                accountAddress = item.address,
                primaryDisplayName = item.domainName,
                secondaryDisplayName = item.address.toShortenedAddress()
            ),
            isContact = false
        )
    }

    fun mapFromSelectionList(
        address: String,
        accountList: List<JointAccountSelectionListItem>
    ): SelectedJointAccountItem? {
        for (item in accountList) {
            when (item) {
                is JointAccountSelectionListItem.AccountItem -> {
                    if (item.address == address) return mapFromAccountItem(item)
                }
                is JointAccountSelectionListItem.ContactItem -> {
                    if (item.address == address) return mapFromContactItem(item)
                }
                is JointAccountSelectionListItem.NfdItem -> {
                    if (item.address == address) return mapFromNfdItem(item)
                }
                is JointAccountSelectionListItem.ExternalAddressItem -> Unit
            }
        }
        return null
    }
}
