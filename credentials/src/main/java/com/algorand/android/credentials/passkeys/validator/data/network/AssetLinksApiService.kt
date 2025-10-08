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

package com.algorand.android.credentials.passkeys.validator.data.network

import retrofit2.http.GET
import retrofit2.http.Query

internal interface AssetLinksApiService {

    @GET("v1/assetlinks:check")
    suspend fun getAssetLinksCheckResult(
        @Query("source.web.site") websiteUrl: String,
        @Query("target.android_app.package_name") packageName: String,
        @Query("target.android_app.certificate.sha256_fingerprint") certification: String,
        @Query("relation") relation: String = "delegate_permission/common.handle_all_urls"
    ): String
}
