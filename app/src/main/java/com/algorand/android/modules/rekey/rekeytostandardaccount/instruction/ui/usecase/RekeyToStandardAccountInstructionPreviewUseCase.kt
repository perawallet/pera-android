/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.rekey.rekeytostandardaccount.instruction.ui.usecase

import com.algorand.android.R
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.modules.rekey.rekeytostandardaccount.instruction.ui.decider.RekeyToStandardAccountIntroductionPreviewDecider
import com.algorand.android.modules.rekey.rekeytostandardaccount.instruction.ui.mapper.RekeyToStandardAccountIntroductionPreviewMapper
import com.algorand.android.modules.rekey.rekeytostandardaccount.instruction.ui.model.RekeyToStandardAccountIntroductionPreview
import javax.inject.Inject

class RekeyToStandardAccountInstructionPreviewUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail,
    private val rekeyToStandardAccountIntroductionPreviewMapper: RekeyToStandardAccountIntroductionPreviewMapper,
    private val rekeyToStandardAccountIntroductionPreviewDecider: RekeyToStandardAccountIntroductionPreviewDecider
) {

    suspend fun getInitialRekeyToStandardAccountInstructionPreview(
        accountAddress: String
    ): RekeyToStandardAccountIntroductionPreview {
        val accountDetail = getAccountDetail(accountAddress)
        val bannerDrawableResId = rekeyToStandardAccountIntroductionPreviewDecider.decideBannerDrawableResId(
            accountType = accountDetail.accountType
        )
        val descriptionAnnotatedString = rekeyToStandardAccountIntroductionPreviewDecider
            .decideDescriptionAnnotatedString(accountType = accountDetail.accountType)
        val expectationListItems = rekeyToStandardAccountIntroductionPreviewDecider.decideExpectationListItems(
            accountType = accountDetail.accountType
        )
        return rekeyToStandardAccountIntroductionPreviewMapper.mapToRekeyToStandardAccountInstructionPreview(
            bannerDrawableResId = bannerDrawableResId,
            titleAnnotatedString = AnnotatedString(stringResId = R.string.rekey_to_standard_account_lower_case),
            descriptionAnnotatedString = descriptionAnnotatedString,
            expectationListItems = expectationListItems,
            actionButtonAnnotatedString = AnnotatedString(stringResId = R.string.start_process)
        )
    }
}
