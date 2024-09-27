/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.utils.app

import com.algorand.android.BuildConfig
import com.algorand.android.module.foundation.app.AppFlavor
import com.algorand.android.module.foundation.app.GetAppFlavor
import javax.inject.Inject

class GetAppFlavorImpl @Inject constructor() : GetAppFlavor {

    override fun invoke(): AppFlavor {
        return if (BuildConfig.FLAVOR == "staging") {
            AppFlavor.STAGING
        } else {
            AppFlavor.PRODUCTION
        }
    }
}
