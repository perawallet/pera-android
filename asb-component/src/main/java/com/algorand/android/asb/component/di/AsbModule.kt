package com.algorand.android.asb.component.di

import com.algorand.android.asb.component.data.mapper.*
import com.algorand.android.asb.component.data.repository.AsbRepositoryImpl
import com.algorand.android.asb.component.domain.repository.AsbRepository
import com.algorand.android.asb.component.domain.usecase.*
import com.algorand.android.asb.component.domain.usecase.implementation.*
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.shared_db.asb.dao.AlgorandSecureBackUpDao
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*

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
