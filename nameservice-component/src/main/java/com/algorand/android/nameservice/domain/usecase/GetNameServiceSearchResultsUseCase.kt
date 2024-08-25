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

package com.algorand.android.nameservice.domain.usecase

import com.algorand.android.nameservice.domain.model.NameServiceSearchResult
import com.algorand.android.nameservice.domain.repository.NameServiceRepository
import com.algorand.android.nameservice.domain.util.NameServiceUtils.isValidNFTDomain
import javax.inject.Inject

internal class GetNameServiceSearchResultsUseCase @Inject constructor(
    private val nameServiceRepository: NameServiceRepository
) : GetNameServiceSearchResults {

    override suspend fun invoke(query: String): List<NameServiceSearchResult> {
        val normalizedQuery = query.trim().lowercase()
        if (!normalizedQuery.isValidNFTDomain()) return emptyList()
        val nameServiceSearchResultList = mutableListOf<NameServiceSearchResult>()
        nameServiceRepository.getNameServiceSearchResults(normalizedQuery).map {
            nameServiceSearchResultList.addAll(it)
        }
        return nameServiceSearchResultList
    }
}
