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

package com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.decider.RekeyToJointAccountIntroductionPreviewDecider
import com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.mapper.RekeyToJointAccountIntroductionPreviewMapper
import com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.model.RekeyToJointAccountIntroductionPreview
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import javax.inject.Inject

class RekeyToJointAccountInstructionPreviewUseCase @Inject constructor(
    private val rekeyToJointAccountIntroductionPreviewMapper: RekeyToJointAccountIntroductionPreviewMapper,
    private val rekeyToJointAccountIntroductionPreviewDecider: RekeyToJointAccountIntroductionPreviewDecider,
    private val getAccountType: GetAccountType
) {

    suspend fun getInitialRekeyToJointAccountInstructionPreview(
        accountAddress: String
    ): RekeyToJointAccountIntroductionPreview {
        val accountType = getAccountType(accountAddress)
        val bannerDrawableResId = rekeyToJointAccountIntroductionPreviewDecider.decideBannerDrawableResId(
            accountType = accountType
        )
        val descriptionAnnotatedString = rekeyToJointAccountIntroductionPreviewDecider
            .decideDescriptionAnnotatedString(accountType = accountType)
        val expectationListItems = rekeyToJointAccountIntroductionPreviewDecider.decideExpectationListItems(
            accountType = accountType
        )
        return rekeyToJointAccountIntroductionPreviewMapper.mapToRekeyToJointAccountInstructionPreview(
            bannerDrawableResId = bannerDrawableResId,
            titleAnnotatedString = AnnotatedString(stringResId = R.string.rekey_to_joint_account_lower_case),
            descriptionAnnotatedString = descriptionAnnotatedString,
            expectationListItems = expectationListItems,
            actionButtonAnnotatedString = AnnotatedString(stringResId = R.string.start_process)
        )
    }
}
