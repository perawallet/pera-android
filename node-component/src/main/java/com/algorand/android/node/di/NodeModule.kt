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

package com.algorand.android.node.di

import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.node.data.mapper.NodeNameMapper
import com.algorand.android.node.data.mapper.NodeNameMapperImpl
import com.algorand.android.node.data.mapper.NodeStorageMapper
import com.algorand.android.node.data.mapper.NodeStorageMapperImpl
import com.algorand.android.node.data.repository.NodeRepositoryImpl
import com.algorand.android.node.data.storage.SelectedNodeLocalSource
import com.algorand.android.node.domain.repository.NodeRepository
import com.algorand.android.node.domain.usecase.GetActiveNode
import com.algorand.android.node.domain.usecase.GetActiveNodeAsFlow
import com.algorand.android.node.domain.usecase.GetActiveNodeNetworkSlug
import com.algorand.android.node.domain.usecase.GetAllNodes
import com.algorand.android.node.domain.usecase.InitializeActiveNode
import com.algorand.android.node.domain.usecase.IsSelectedNodeMainnet
import com.algorand.android.node.domain.usecase.IsSelectedNodeTestnet
import com.algorand.android.node.domain.usecase.SetSelectedNode
import com.algorand.android.node.domain.usecase.implementation.GetActiveNodeNetworkSlugUseCase
import com.algorand.android.node.domain.usecase.implementation.GetAllNodesUseCase
import com.algorand.android.node.domain.usecase.implementation.InitializeActiveNodeUseCase
import com.algorand.android.node.domain.usecase.implementation.IsSelectedNodeMainnetUseCase
import com.algorand.android.node.domain.usecase.implementation.IsSelectedNodeTestnetUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NodeModule {

    @Provides
    @Singleton
    fun provideNodeStorageMapper(mapper: NodeStorageMapperImpl): NodeStorageMapper = mapper

    @Provides
    @Singleton
    fun provideNodeRepository(selectedNodeLocalSource: SelectedNodeLocalSource): NodeRepository {
        return NodeRepositoryImpl(
            SingleInMemoryLocalCache(),
            selectedNodeLocalSource
        )
    }

    @Provides
    @Singleton
    fun provideGetActiveNode(repository: NodeRepository): GetActiveNode = GetActiveNode(repository::getActiveNode)

    @Provides
    @Singleton
    fun provideGetActiveNodeAsFlow(
        repository: NodeRepository
    ): GetActiveNodeAsFlow = GetActiveNodeAsFlow(repository::getActiveNodeAsFlow)

    @Provides
    @Singleton
    fun provideIsSelectedNodeMainnet(useCase: IsSelectedNodeMainnetUseCase): IsSelectedNodeMainnet = useCase

    @Provides
    @Singleton
    fun provideIsSelectedNodeTestnet(useCase: IsSelectedNodeTestnetUseCase): IsSelectedNodeTestnet = useCase

    @Provides
    @Singleton
    fun provideSetSelectedNode(repository: NodeRepository): SetSelectedNode = SetSelectedNode(repository::setActiveNode)

    @Provides
    @Singleton
    fun provideInitializeActiveNode(useCase: InitializeActiveNodeUseCase): InitializeActiveNode = useCase

    @Provides
    @Singleton
    fun provideNodeNameMapper(mapper: NodeNameMapperImpl): NodeNameMapper = mapper

    @Provides
    @Singleton
    fun provideGetAllNodes(useCase: GetAllNodesUseCase): GetAllNodes = useCase

    @Provides
    @Singleton
    fun provideGetActiveNodeNetworkSlug(
        useCase: GetActiveNodeNetworkSlugUseCase
    ): GetActiveNodeNetworkSlug = useCase
}
