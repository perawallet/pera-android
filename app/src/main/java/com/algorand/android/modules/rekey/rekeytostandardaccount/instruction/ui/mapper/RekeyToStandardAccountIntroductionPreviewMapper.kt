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

package com.algorand.android.modules.rekey.rekeytostandardaccount.instruction.ui.mapper

import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.modules.rekey.rekeytostandardaccount.instruction.ui.model.RekeyToStandardAccountIntroductionPreview
import javax.inject.Inject

class RekeyToStandardAccountIntroductionPreviewMapper @Inject constructor() {

    fun mapToRekeyToStandardAccountInstructionPreview(
        bannerDrawableResId: Int,
        titleAnnotatedString: AnnotatedString,
        descriptionAnnotatedString: AnnotatedString?,
        actionButtonAnnotatedString: AnnotatedString,
        expectationListItems: List<AnnotatedString>
    ): RekeyToStandardAccountIntroductionPreview {
        return RekeyToStandardAccountIntroductionPreview(
            bannerDrawableResId = bannerDrawableResId,
            titleAnnotatedString = titleAnnotatedString,
            featureTag = null,
            descriptionAnnotatedString = descriptionAnnotatedString,
            actionButtonAnnotatedString = actionButtonAnnotatedString,
            expectationListItems = expectationListItems
        )
    }
}
