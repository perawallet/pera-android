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

package com.algorand.android.ui.swap.di

import com.algorand.android.ui.swap.tracking.DefaultSwapConfirmationEventTracker
import com.algorand.android.ui.swap.tracking.DefaultSwapHistoryEventTracker
import com.algorand.android.ui.swap.tracking.DefaultSwapHistoryWidgetEventTracker
import com.algorand.android.ui.swap.tracking.DefaultSwapScreenEventTracker
import com.algorand.android.ui.swap.tracking.DefaultSwapTopPairsEventTracker
import com.algorand.android.ui.swap.tracking.SwapConfirmationEventTracker
import com.algorand.android.ui.swap.tracking.SwapHistoryEventTracker
import com.algorand.android.ui.swap.tracking.SwapHistoryWidgetEventTracker
import com.algorand.android.ui.swap.tracking.SwapScreenEventTracker
import com.algorand.android.ui.swap.tracking.SwapTopPairsEventTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object SwapTrackingModule {

    @Provides
    fun provideSwapHistoryWidgetEventTracker(
        tracker: DefaultSwapHistoryWidgetEventTracker
    ): SwapHistoryWidgetEventTracker = tracker

    @Provides
    fun provideSwapTopPairsEventTracker(tracker: DefaultSwapTopPairsEventTracker): SwapTopPairsEventTracker = tracker

    @Provides
    fun provideSwapScreenEventTracker(tracker: DefaultSwapScreenEventTracker): SwapScreenEventTracker = tracker

    @Provides
    fun provideSwapHistoryEventTracker(tracker: DefaultSwapHistoryEventTracker): SwapHistoryEventTracker = tracker

    @Provides
    fun provideSwapConfirmationEventTracker(
        tracker: DefaultSwapConfirmationEventTracker
    ): SwapConfirmationEventTracker = tracker
}
