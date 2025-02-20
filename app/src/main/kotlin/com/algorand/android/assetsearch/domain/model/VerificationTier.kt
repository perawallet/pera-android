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

package com.algorand.android.assetsearch.domain.model

@Deprecated(
    message = "Legacy verification tier model. Will be replaced with common-sdk model.",
    replaceWith = ReplaceWith("VerificationTier", "com.algorand.wallet.asset.domain.model.VerificationTier")
)
enum class VerificationTier {
    VERIFIED,
    UNVERIFIED,
    TRUSTED,
    SUSPICIOUS
}
