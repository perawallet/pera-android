/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.warning.ui.usecase

import com.algorand.android.modules.assetinbox.send.warning.ui.mapper.Arc59SendSummaryWarningPreviewMapper
import com.algorand.android.modules.assetinbox.send.warning.ui.model.Arc59SendSummaryWarningNavArgs
import com.algorand.android.modules.assetinbox.send.warning.ui.model.Arc59SendSummaryWarningPreview
import javax.inject.Inject

class Arc59SendSummaryWarningPreviewUseCase @Inject constructor(
    private val arc59SendSummaryWarningPreviewMapper: Arc59SendSummaryWarningPreviewMapper
) {

    fun getArc59SendSummaryWarningPreview(
        arc59SendSummaryWarningNavArgs: Arc59SendSummaryWarningNavArgs
    ): Arc59SendSummaryWarningPreview {
        return arc59SendSummaryWarningPreviewMapper(arc59SendSummaryWarningNavArgs)
    }
}
