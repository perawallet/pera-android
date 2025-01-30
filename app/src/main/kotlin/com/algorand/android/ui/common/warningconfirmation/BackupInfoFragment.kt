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

package com.algorand.android.ui.common.warningconfirmation

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.R
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.models.Account
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.common.warningconfirmation.BackupInfoFragmentDirections.Companion.actionBackupInfoFragmentToBackupPassphraseAccountNameNavigation
import com.algorand.android.ui.common.warningconfirmation.BackupInfoFragmentDirections.Companion.actionBackupInfoFragmentToWriteDownInfoFragment
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.browser.openRecoveryPassphraseSupportUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupInfoFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val backupInfoViewModel: BackupInfoViewModel by viewModels()

    private val args: BackupInfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()
    }

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_shield),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "shield",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = R.string.create_a_passphrase_backup)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            text = stringResource(
                id = R.string.creating_a_passphrase_backup
            ),
            modifier = modifier
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
        PeraPrimaryButton(
            onClick = { navToWriteDownFragment() },
            modifier = modifier,
            text = stringResource(id = R.string.i_understand)
        )

    @Composable
    override fun SecondaryButton(modifier: Modifier) {
        if (args.publicKeysOfAccountsToBackup.isEmpty()) {
            PeraSecondaryButton(
                onClick = { navToBackupPassphraseAccountNameNavigation() },
                modifier = modifier,
                text = stringResource(id = R.string.skip_for_now)
            )
        }
    }

    private fun navToWriteDownFragment() {
        val accountCreation = if (args.publicKeysOfAccountsToBackup.isEmpty()) {
            getAccountCreation()
        } else {
            null
        }

        backupInfoViewModel.logOnboardingIUnderstandClickEvent()
        nav(
            actionBackupInfoFragmentToWriteDownInfoFragment(
                args.publicKeysOfAccountsToBackup,
                accountCreation
            )
        )
    }

    private fun navToBackupPassphraseAccountNameNavigation() {
        val accountCreation = getAccountCreation()
        accountCreation?.let {
            nav(actionBackupInfoFragmentToBackupPassphraseAccountNameNavigation(it))
        }
    }

    private fun configureToolbar() {
        getAppToolbar()?.setEndButton(
            button = IconButton(
                R.drawable.ic_info,
                onClick = ::onInfoClick
            )
        )
    }

    private fun onInfoClick() {
        context?.openRecoveryPassphraseSupportUrl()
    }

    // TODO move this into util class
    private fun getAccountCreation(): AccountCreation? {
        try {
            val secretKeyByteArray: ByteArray?
            secretKeyByteArray = Sdk.generateSK()
            val publicKey = Sdk.generateAddressFromSK(secretKeyByteArray)
            val tempAccount = Account.create(
                publicKey = publicKey,
                detail = Account.Detail.Standard(secretKeyByteArray),
                isBackedUp = false
            )
            return AccountCreation(tempAccount, CreationType.CREATE)
        } catch (exception: Exception) {
            navBack()
        }
        return null
    }

    companion object {
        private const val ACCOUNT_CREATION_KEY = "accountCreation"
    }
}
