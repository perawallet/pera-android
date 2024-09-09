package com.algorand.android.account.localaccount.di

import com.algorand.android.account.localaccount.data.database.dao.Algo25Dao
import com.algorand.android.account.localaccount.data.database.dao.LedgerBleDao
import com.algorand.android.account.localaccount.data.database.dao.LedgerUsbDao
import com.algorand.android.account.localaccount.data.database.dao.NoAuthDao
import com.algorand.android.account.localaccount.data.mapper.entity.Algo25EntityMapper
import com.algorand.android.account.localaccount.data.mapper.entity.Algo25EntityMapperImpl
import com.algorand.android.account.localaccount.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.android.account.localaccount.data.mapper.entity.LedgerBleEntityMapperImpl
import com.algorand.android.account.localaccount.data.mapper.entity.LedgerUsbEntityMapper
import com.algorand.android.account.localaccount.data.mapper.entity.LedgerUsbEntityMapperImpl
import com.algorand.android.account.localaccount.data.mapper.entity.NoAuthEntityMapper
import com.algorand.android.account.localaccount.data.mapper.entity.NoAuthEntityMapperImpl
import com.algorand.android.account.localaccount.data.mapper.model.algo25.Algo25Mapper
import com.algorand.android.account.localaccount.data.mapper.model.algo25.Algo25MapperImpl
import com.algorand.android.account.localaccount.data.mapper.model.ledgerble.LedgerBleMapper
import com.algorand.android.account.localaccount.data.mapper.model.ledgerble.LedgerBleMapperImpl
import com.algorand.android.account.localaccount.data.mapper.model.ledgerusb.LedgerUsbMapper
import com.algorand.android.account.localaccount.data.mapper.model.ledgerusb.LedgerUsbMapperImpl
import com.algorand.android.account.localaccount.data.mapper.model.noauth.NoAuthMapper
import com.algorand.android.account.localaccount.data.mapper.model.noauth.NoAuthMapperImpl
import com.algorand.android.account.localaccount.data.repository.Algo25AccountRepositoryImpl
import com.algorand.android.account.localaccount.data.repository.LedgerBleAccountRepositoryImpl
import com.algorand.android.account.localaccount.data.repository.LedgerUsbAccountRepositoryImpl
import com.algorand.android.account.localaccount.data.repository.NoAuthAccountRepositoryImpl
import com.algorand.android.account.localaccount.domain.repository.Algo25AccountRepository
import com.algorand.android.account.localaccount.domain.repository.LedgerBleAccountRepository
import com.algorand.android.account.localaccount.domain.repository.LedgerUsbAccountRepository
import com.algorand.android.account.localaccount.domain.repository.NoAuthAccountRepository
import com.algorand.android.account.localaccount.domain.usecase.GetLedgerBleAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccountCountFlow
import com.algorand.android.account.localaccount.domain.usecase.implementation.GetLocalAccountCountFlowUseCase
import com.algorand.android.encryption.Base64Manager
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.encryption.di.TINK_ENCRYPTION_MANAGER
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalAccountsModule {

    @Provides
    @Singleton
    fun provideAlgo25AccountRepository(
        algo25Dao: Algo25Dao,
        algo25EntityMapper: Algo25EntityMapper,
        algo25Mapper: Algo25Mapper,
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): Algo25AccountRepository {
        return Algo25AccountRepositoryImpl(algo25Dao, algo25EntityMapper, algo25Mapper, encryptionManager)
    }

    @Provides
    @Singleton
    fun provideLedgerBleAccountRepository(
        ledgerBleDao: LedgerBleDao,
        ledgerBleEntityMapper: LedgerBleEntityMapper,
        ledgerBleMapper: LedgerBleMapper,
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): LedgerBleAccountRepository {
        return LedgerBleAccountRepositoryImpl(ledgerBleDao, ledgerBleEntityMapper, ledgerBleMapper, encryptionManager)
    }

    @Provides
    @Singleton
    fun provideLedgerUsbAccountRepository(
        ledgerUsbDao: LedgerUsbDao,
        ledgerUsbEntityMapper: LedgerUsbEntityMapper,
        ledgerUsbMapper: LedgerUsbMapper,
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): LedgerUsbAccountRepository {
        return LedgerUsbAccountRepositoryImpl(ledgerUsbDao, ledgerUsbEntityMapper, ledgerUsbMapper, encryptionManager)
    }

    @Provides
    @Singleton
    fun provideNoAuthAccountRepository(
        noAuthDao: NoAuthDao,
        noAuthEntityMapper: NoAuthEntityMapper,
        noAuthMapper: NoAuthMapper,
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): NoAuthAccountRepository {
        return NoAuthAccountRepositoryImpl(noAuthDao, noAuthEntityMapper, noAuthMapper, encryptionManager)
    }

    @Provides
    @Singleton
    fun provideAlgo25EntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) deterministicEncryptionManager: EncryptionManager,
        @Named(TINK_ENCRYPTION_MANAGER) nondeterministicEncryptionManager: EncryptionManager,
        base64Manager: Base64Manager
    ): Algo25EntityMapper {
        return Algo25EntityMapperImpl(deterministicEncryptionManager, nondeterministicEncryptionManager, base64Manager)
    }

    @Provides
    @Singleton
    fun provideLedgerBleEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): LedgerBleEntityMapper {
        return LedgerBleEntityMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideLedgerUsbEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): LedgerUsbEntityMapper {
        return LedgerUsbEntityMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideNoAuthEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): NoAuthEntityMapper {
        return NoAuthEntityMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideAlgo25Mapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) deterministicEncryptionManager: EncryptionManager,
        @Named(TINK_ENCRYPTION_MANAGER) nondeterministicEncryptionManager: EncryptionManager,
        base64Manager: Base64Manager
    ): Algo25Mapper {
        return Algo25MapperImpl(deterministicEncryptionManager, nondeterministicEncryptionManager, base64Manager)
    }

    @Provides
    @Singleton
    fun provideLedgerBleMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): LedgerBleMapper {
        return LedgerBleMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideLedgerUsbMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): LedgerUsbMapper {
        return LedgerUsbMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideNoAuthMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): NoAuthMapper {
        return NoAuthMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideGetLocalAccountCountFlow(useCase: GetLocalAccountCountFlowUseCase): GetLocalAccountCountFlow = useCase

    @Provides
    @Singleton
    fun provideGetLedgerBleAccount(repository: LedgerBleAccountRepository): GetLedgerBleAccount {
        return GetLedgerBleAccount(repository::getAccount)
    }
}
