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

package com.algorand.android.modules.addaccount.joint.creation.ui.editname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.addaccount.joint.creation.ui.editname.viewmodel.EditAccountNameViewModel
import com.algorand.android.modules.addaccount.joint.creation.ui.editname.viewmodel.EditAccountNameViewModel.ViewState
import com.algorand.android.ui.compose.extensions.createComposeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountNameFragment : DaggerBaseFragment(0), EditAccountNameScreenListener {

    private val args: EditAccountNameFragmentArgs by navArgs()
    private val viewModel: EditAccountNameViewModel by viewModels()

    override val fragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            val viewState by viewModel.state.collectAsStateWithLifecycle()
            when (val state = viewState) {
                is ViewState.Loading -> Unit
                is ViewState.Content -> {
                    EditAccountNameScreen(
                        account = state.account,
                        listener = this
                    )
                }
            }
        }
    }

    override fun onBackClick() {
        navBack()
    }

    override fun onDoneClick(name: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set(RESULT_UPDATED_NAME, name)
            set(RESULT_ADDRESS, args.accountAddress)
        }
        navBack()
    }

    override fun onRemoveClick() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            RESULT_REMOVED_ADDRESS,
            args.accountAddress
        )
        navBack()
    }

    companion object {
        const val RESULT_UPDATED_NAME = "result_updated_name"
        const val RESULT_ADDRESS = "result_address"
        const val RESULT_REMOVED_ADDRESS = "result_removed_address"
    }
}
