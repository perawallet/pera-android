package com.algorand.android.module.asb.di

import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.module.asb.data.mapper.AlgorandSecureBackUpEntityMapper
import com.algorand.android.module.asb.data.mapper.AlgorandSecureBackUpEntityMapperImpl
import com.algorand.android.module.asb.data.repository.AsbRepositoryImpl
import com.algorand.android.module.asb.domain.repository.AsbRepository
import com.algorand.android.module.asb.domain.usecase.CreateAsbBackUpFileName
import com.algorand.android.module.asb.domain.usecase.CreateAsbCipherKey
import com.algorand.android.module.asb.domain.usecase.CreateAsbCipherText
import com.algorand.android.module.asb.domain.usecase.CreateAsbMnemonic
import com.algorand.android.module.asb.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.android.module.asb.domain.usecase.GetBackedUpAccountAddressesFlow
import com.algorand.android.module.asb.domain.usecase.GetBackedUpAccounts
import com.algorand.android.module.asb.domain.usecase.RemoveAccountAsbBackUpStatus
import com.algorand.android.module.asb.domain.usecase.SetAccountAsbBackUpStatus
import com.algorand.android.module.asb.domain.usecase.SetAccountsAsbBackedUp
import com.algorand.android.module.asb.domain.usecase.implementation.CreateAsbBackUpFileNameUseCase
import com.algorand.android.module.asb.domain.usecase.implementation.CreateAsbCipherKeyUseCase
import com.algorand.android.module.asb.domain.usecase.implementation.CreateAsbCipherTextUseCase
import com.algorand.android.module.asb.domain.usecase.implementation.CreateAsbMnemonicUseCase
import com.algorand.android.module.shareddb.asb.dao.AlgorandSecureBackUpDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AsbModule {

    @Provides
    @Singleton
    fun provideAsbRepository(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager,
        algorandSecureBackUpDao: AlgorandSecureBackUpDao,
        algorandSecureBackUpEntityMapper: AlgorandSecureBackUpEntityMapper
    ): AsbRepository {
        return AsbRepositoryImpl(
            encryptionManager,
            algorandSecureBackUpDao,
            algorandSecureBackUpEntityMapper
        )
    }

    @Provides
    @Singleton
    fun provideGetBackedUpAccountAddressesFlow(
        repository: AsbRepository
    ): GetBackedUpAccountAddressesFlow = GetBackedUpAccountAddressesFlow(repository::getBackedUpAccountAddressesFlow)

    @Provides
    @Singleton
    fun provideGetBackedUpAccounts(
        repository: AsbRepository
    ): GetBackedUpAccounts = GetBackedUpAccounts(repository::getBackedUpAccounts)

    @Provides
    @Singleton
    fun provideCreateAsbBackUpFileName(
        useCase: CreateAsbBackUpFileNameUseCase
    ): CreateAsbBackUpFileName = useCase

    @Provides
    @Singleton
    fun provideAlgorandSecureBackUpEntityMapper(
        mapper: AlgorandSecureBackUpEntityMapperImpl
    ): AlgorandSecureBackUpEntityMapper = mapper

    @Provides
    @Singleton
    fun provideSetAccountAsbBackUpStatus(
        repository: AsbRepository
    ): SetAccountAsbBackUpStatus = SetAccountAsbBackUpStatus(repository::setBackedUp)

    @Provides
    @Singleton
    fun provideGetAccountAsbBackUpStatus(
        repository: AsbRepository
    ): GetAccountAsbBackUpStatus = GetAccountAsbBackUpStatus(repository::getBackedUpStatus)

    @Provides
    @Singleton
    fun provideRemoveAccountAsbBackUpStatus(
        repository: AsbRepository
    ): RemoveAccountAsbBackUpStatus = RemoveAccountAsbBackUpStatus(repository::removeAccount)

    @Provides
    @Singleton
    fun provideSetAsbBackedUpAccounts(
        repository: AsbRepository
    ): SetAccountsAsbBackedUp = SetAccountsAsbBackedUp(repository::setBackedUpBulk)

    @Provides
    @Singleton
    fun provideCreateAsbCipherKey(useCase: CreateAsbCipherKeyUseCase): CreateAsbCipherKey = useCase

    @Provides
    @Singleton
    fun provideCreateAsbCipherText(useCase: CreateAsbCipherTextUseCase): CreateAsbCipherText = useCase

    @Provides
    @Singleton
    fun provideCreateAsbMnemonic(useCase: CreateAsbMnemonicUseCase): CreateAsbMnemonic = useCase
}
