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

package com.algorand.android.assetutils

// Backend accepts ALGO with asset id 0. Remove this line if they accepts to change ALGO ID as -7
fun getSafeAssetIdForRequest(assetId: Long): Long {
    return if (assetId == AssetConstants.ALGO_ID) 0 else assetId
}

// Backend returns ALGO with asset id 0. Remove this line if they accepts to change ALGO ID as -7
fun getSafeAssetIdForResponse(assetId: Long?): Long? {
    return if (assetId == 0L) AssetConstants.ALGO_ID else assetId
}