/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.lockpreference

import android.content.SharedPreferences
import com.algorand.android.utils.preference.DEFAULT_LOCK_PREFERENCE_COUNT
import com.algorand.android.utils.preference.DONT_SHOW_AGAIN_COUNT
import com.algorand.android.utils.preference.getLockPreferenceCount
import com.algorand.android.utils.preference.isPasswordChosen
import com.algorand.android.utils.preference.setLockPreferenceCount
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyLocalAccount
import javax.inject.Inject

class AutoLockSuggestionManager @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val isThereAnyLocalAccount: IsThereAnyLocalAccount
) {

    private var isStarted = false
    private var lockPreferenceCount = DEFAULT_LOCK_PREFERENCE_COUNT

    private fun shouldSuggest(): Boolean {
        return (lockPreferenceCount >= SUGGESTION_TRIGGER_COUNT) && sharedPref.isPasswordChosen().not()
    }

    suspend fun shouldSuggestAutoLock(): Boolean {
        if (isStarted) return false
        isStarted = true

        if (sharedPref.isPasswordChosen().not() && isThereAnyLocalAccount()) {
            increaseLaunchCount()
        }

        return shouldSuggest().also { shouldSuggest ->
            if (shouldSuggest) resetLaunchCount()
        }
    }

    private fun increaseLaunchCount() {
        lockPreferenceCount = sharedPref.getLockPreferenceCount()
        if (lockPreferenceCount != DONT_SHOW_AGAIN_COUNT && lockPreferenceCount < SUGGESTION_TRIGGER_COUNT) {
            lockPreferenceCount++
            sharedPref.setLockPreferenceCount(lockPreferenceCount)
        }
    }

    private fun resetLaunchCount() {
        sharedPref.setLockPreferenceCount(DEFAULT_LOCK_PREFERENCE_COUNT)
    }

    companion object {
        private const val SUGGESTION_TRIGGER_COUNT = 5
    }
}
