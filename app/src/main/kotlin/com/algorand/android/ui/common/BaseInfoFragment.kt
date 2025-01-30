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

package com.algorand.android.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.algorand.android.core.DaggerBaseFragment

abstract class BaseInfoFragment : DaggerBaseFragment(0) {

    @Composable
    abstract fun Icon(modifier: Modifier)

    @Composable
    abstract fun Title(modifier: Modifier)

    @Composable
    abstract fun Description(modifier: Modifier)

    @Composable
    abstract fun PrimaryButton(modifier: Modifier)

    @Composable
    open fun Warning(modifier: Modifier) = Unit

    @Composable
    open fun SecondaryButton(modifier: Modifier) = Unit

    @Composable
    open fun TopStartButton(modifier: Modifier) = Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return ComposeView(requireContext()).apply {
            setContent {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // equivalent to ScrollView
                ) {
                    TopStartButton(modifier = Modifier.align(alignment = Alignment.Start))
                    Icon(
                        modifier = Modifier
                            .padding(start = 32.dp, top = 28.dp)
                            .align(alignment = Alignment.Start)
                            .fillMaxWidth(fraction = HALF_SIZE)
                            .aspectRatio(ratio = 1F),
                    )
                    Title(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp, top = 40.dp)
                    )
                    Description(
                        modifier = Modifier
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                    )
                    Warning(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp, bottom = 16.dp)
                    )
                    PrimaryButton(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    )
                    SecondaryButton(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

    companion object {
        const val HALF_SIZE = 0.5F
    }
}
