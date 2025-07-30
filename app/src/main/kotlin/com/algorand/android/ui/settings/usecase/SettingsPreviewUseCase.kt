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

package com.algorand.android.ui.settings.usecase

import com.algorand.android.ui.settings.mapper.SettingsPreviewMapper
import com.algorand.android.ui.settings.model.SettingsPreview
import com.algorand.wallet.analytics.domain.usecase.GetFirebaseInstanceIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SettingsPreviewUseCase @Inject constructor(
    private val settingsPreviewMapper: SettingsPreviewMapper,
    private val getFirebaseInstanceIdUseCase: GetFirebaseInstanceIdUseCase,
) {

    fun getSettingsPreviewFlow(): Flow<SettingsPreview> = flow {
        val preview = settingsPreviewMapper.mapToSettingsPreview(
            firebaseInstanceId = getFirebaseInstanceIdUseCase()
        )
        emit(preview)
    }
}
