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

package com.algorand.android.credentials.passkeys.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.provider.PendingIntentHandler
import androidx.fragment.app.FragmentActivity

internal class DefaultActivityCredentialRequestResolver : ActivityCredentialRequestResolver {

    private var activity: FragmentActivity? = null

    override fun initializeRequestResolver(activity: FragmentActivity) {
        this.activity = activity
    }

    override fun finishWithCreateCredentialError(message: String) {
        val intent = Intent()
        PendingIntentHandler.setCreateCredentialException(intent, CreateCredentialUnknownException(message))
        finishActivity(intent)
    }

    override fun finishWithGetCredentialError(message: String) {
        val intent = Intent()
        PendingIntentHandler.setGetCredentialException(intent, GetCredentialUnknownException(message))
        finishActivity(intent)
    }

    override fun finishWithCreateCredentialResponse(response: CreatePublicKeyCredentialResponse) {
        val intent = Intent()
        PendingIntentHandler.setCreateCredentialResponse(intent, response)
        finishActivity(intent)
    }

    override fun finishWithGetCredentialResponse(response: GetCredentialResponse) {
        val intent = Intent()
        PendingIntentHandler.setGetCredentialResponse(intent, response)
        finishActivity(intent)
    }

    private fun finishActivity(intent: Intent) {
        activity?.setResult(RESULT_OK, intent)
        activity?.finish()
        activity = null
    }
}
