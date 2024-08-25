@file:Suppress("TooManyFunctions")
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

package com.algorand.android.utils.preference

import android.content.SharedPreferences
import com.algorand.android.models.Account
import com.algorand.android.sharedpref.EncryptedPinLocalSource.Companion.ENCRYPTED_PIN_KEY
import com.algorand.android.utils.encryptString
import com.google.crypto.tink.Aead
import com.google.gson.Gson

// <editor-fold defaultstate="collapsed" desc="Constants">

private const val ALGORAND_ACCOUNTS_KEY = "algorand_accounts"
private const val DEFAULT_NODE_LIST_VERSION = "default_node_list_version"
private const val NOTIFICATION_ACTIVATED_KEY = "notification_activated"
private const val QR_TUTORIAL_SHOWN_KEY = "qr_tutorial_shown_key"
private const val TD_COPY_TUTORIAL_SHOWN_KEY = "transaction_detail_copy_shown_key"
private const val FILTER_TUTORIAL_SHOWN_KEY = "filter_tutorial_shown_key"
private const val APP_REVIEW_START_COUNT_KEY = "app_review_start_count_key"
private const val REGISTER_SKIP_KEY = "register_skip_key"
private const val FIRST_REQUEST_WALLET_CONNECT_REQUEST_KEY = "first_request_wallet_connect_request"
const val SETTINGS = "algorand_settings"

// </editor-fold>

fun SharedPreferences.removeAll() {
    edit()
        .clear()
        .putBoolean(QR_TUTORIAL_SHOWN_KEY, isQrTutorialShown())
        .putBoolean(TD_COPY_TUTORIAL_SHOWN_KEY, isTransactionDetailCopyTutorialShown())
        .putBoolean(FILTER_TUTORIAL_SHOWN_KEY, isFilterTutorialShown())
        .apply()
}

// <editor-fold defaultstate="collapsed" desc="Accounts">

fun SharedPreferences.saveAlgorandAccounts(gson: Gson, accountsList: List<Account>, aead: Aead) {
    edit().putString(ALGORAND_ACCOUNTS_KEY, aead.encryptString(gson.toJson(accountsList))).apply()
}

fun SharedPreferences.getEncryptedAlgorandAccounts() = getString(ALGORAND_ACCOUNTS_KEY, null)

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Password">

// Use EncryptedPinUseCase instead and remove this extension function
fun SharedPreferences.isPasswordChosen() = getString(ENCRYPTED_PIN_KEY, null) != null

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Notification">
fun SharedPreferences.isNotificationActivated() = getBoolean(NOTIFICATION_ACTIVATED_KEY, true)

fun SharedPreferences.setNotificationPreference(enableNotifications: Boolean) {
    edit().putBoolean(NOTIFICATION_ACTIVATED_KEY, enableNotifications).apply()
}

// </editor-fold>

// </editor-fold>

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="QRTutorialShown">

fun SharedPreferences.setQrTutorialShown() {
    edit().putBoolean(QR_TUTORIAL_SHOWN_KEY, true).apply()
}

fun SharedPreferences.isQrTutorialShown() = getBoolean(QR_TUTORIAL_SHOWN_KEY, false)

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="TDCopyShown">

fun SharedPreferences.isTransactionDetailCopyTutorialShown() = getBoolean(TD_COPY_TUTORIAL_SHOWN_KEY, false)

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="TDCopyShown">

fun SharedPreferences.setFilterTutorialShown() {
    edit().putBoolean(FILTER_TUTORIAL_SHOWN_KEY, true).apply()
}

fun SharedPreferences.isFilterTutorialShown() = getBoolean(FILTER_TUTORIAL_SHOWN_KEY, false)

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="NotificationRefreshDate">

fun SharedPreferences.setAppReviewStartCount(appReviewStartCount: Int) {
    edit().putInt(APP_REVIEW_START_COUNT_KEY, appReviewStartCount).apply()
}

fun SharedPreferences.getAppReviewStartCount(): Int {
    return getInt(APP_REVIEW_START_COUNT_KEY, 0)
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="NotificationRefreshDate">

fun SharedPreferences.setRegisterSkip() {
    edit().putBoolean(REGISTER_SKIP_KEY, true).apply()
}

fun SharedPreferences.getRegisterSkip(): Boolean {
    return getBoolean(REGISTER_SKIP_KEY, false)
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Wallet Connect">

fun SharedPreferences.setFirstWalletConnectRequestBottomSheetShown() {
    edit().putBoolean(FIRST_REQUEST_WALLET_CONNECT_REQUEST_KEY, true).apply()
}

fun SharedPreferences.getFirstWalletConnectRequestBottomSheetShown(): Boolean {
    return getBoolean(FIRST_REQUEST_WALLET_CONNECT_REQUEST_KEY, false)
}

// </editor-fold>
