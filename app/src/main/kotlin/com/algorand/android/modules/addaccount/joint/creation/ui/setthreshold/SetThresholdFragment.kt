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

package com.algorand.android.modules.addaccount.joint.creation.ui.setthreshold

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountCreationEventTracker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

@AndroidEntryPoint
class SetThresholdFragment : DaggerBaseFragment(0), SetThresholdScreenListener {

    @Inject
    lateinit var jointAccountCreationEventTracker: JointAccountCreationEventTracker

    private val args: SetThresholdFragmentArgs by navArgs()

    override val fragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            SetThresholdScreen(
                numberOfAccounts = args.participantAddresses.size,
                listener = this
            )
        }
    }

    override fun onBackClick() {
        navBack()
    }

    override fun onContinueClick(threshold: Int) {
        lifecycleScope.launch { jointAccountCreationEventTracker.logOnbJointAccountThresholdContinuePress() }
        navToNameJointAccountFragment(threshold)
    }

    private fun navToNameJointAccountFragment(threshold: Int) {
        nav(
            SetThresholdFragmentDirections
                .actionSetThresholdFragmentToNameJointAccountFragment(
                    threshold = threshold,
                    participantAddresses = args.participantAddresses
                )
        )
    }
}
