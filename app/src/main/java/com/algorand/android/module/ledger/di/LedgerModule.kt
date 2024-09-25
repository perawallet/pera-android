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

package com.algorand.android.module.ledger.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import androidx.core.content.ContextCompat
import com.algorand.android.module.ledger.domain.helper.IsBluetoothBondingRequired
import com.algorand.android.module.ledger.domain.helper.IsBluetoothBondingRequiredImpl
import com.algorand.android.module.ledger.domain.helper.LedgerBleScanner
import com.algorand.android.module.ledger.domain.helper.LedgerBleScannerImpl
import com.algorand.android.module.ledger.domain.helper.LedgerBleSearchTimer
import com.algorand.android.module.ledger.domain.helper.LedgerBleSearchTimerImpl
import com.algorand.android.module.ledger.domain.helper.PacketizeLedgerPayload
import com.algorand.android.module.ledger.domain.helper.PacketizeLedgerPayloadImpl
import com.algorand.android.module.ledger.domain.payload.CreateSendPublicKeyRequestPayload
import com.algorand.android.module.ledger.domain.payload.CreateSendPublicKeyRequestPayloadImpl
import com.algorand.android.module.ledger.domain.payload.CreateSignTransactionRequestPayload
import com.algorand.android.module.ledger.domain.payload.CreateSignTransactionRequestPayloadImpl
import com.algorand.android.module.ledger.domain.payload.CreateVerifyPublicKeyRequestPayload
import com.algorand.android.module.ledger.domain.payload.CreateVerifyPublicKeyRequestPayloadImpl
import com.algorand.android.module.ledger.manager.LedgerBleManager
import com.algorand.android.module.ledger.manager.LedgerBleOperationManager
import com.algorand.android.module.ledger.manager.LedgerBleSearchManager
import com.algorand.android.module.ledger.manager.implementation.LedgerBleManagerImpl
import com.algorand.android.module.ledger.manager.implementation.LedgerBleOperationManagerImpl
import com.algorand.android.module.ledger.manager.implementation.LedgerBleSearchManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LedgerModule {

    @Singleton
    @Provides
    fun provideBluetoothManager(@ApplicationContext appContext: Context): BluetoothManager? {
        return ContextCompat.getSystemService<BluetoothManager>(appContext, BluetoothManager::class.java)
    }

    @Singleton
    @Provides
    fun provideBluetoothLeScanner(
        bluetoothManager: BluetoothManager?,
    ): BluetoothLeScanner? {
        return bluetoothManager?.adapter?.bluetoothLeScanner
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager?): BluetoothAdapter? {
        return bluetoothManager?.adapter
    }

    @Provides
    fun provideLedgerBleScanner(scanner: LedgerBleScannerImpl): LedgerBleScanner = scanner

    @Provides
    fun provideLedgerBleSearchTimer(
        impl: LedgerBleSearchTimerImpl
    ): LedgerBleSearchTimer = impl

    @Provides
    @Singleton
    fun providePacketizeLedgerPayload(impl: PacketizeLedgerPayloadImpl): PacketizeLedgerPayload = impl

    @Provides
    @Singleton
    fun provideCreateSignTransactionRequestPayload(
        impl: CreateSignTransactionRequestPayloadImpl
    ): CreateSignTransactionRequestPayload = impl

    @Provides
    @Singleton
    fun provideCreateSendPublicKeyRequestPayload(
        impl: CreateSendPublicKeyRequestPayloadImpl
    ): CreateSendPublicKeyRequestPayload = impl

    @Provides
    @Singleton
    fun provideCreateVerifyPublicKeyRequestPayload(
        impl: CreateVerifyPublicKeyRequestPayloadImpl
    ): CreateVerifyPublicKeyRequestPayload = impl

    @Provides
    fun provideLedgerBleManager(
        @ApplicationContext context: Context,
        packetizeLedgerPayload: PacketizeLedgerPayload
    ): LedgerBleManager {
        return LedgerBleManagerImpl(
            context,
            packetizeLedgerPayload,
            false // TODO Fix here
        )
    }

    @Provides
    fun provideLedgerBleSearchManager(impl: LedgerBleSearchManagerImpl): LedgerBleSearchManager = impl

    @Provides
    fun provideLedgerBleOperationManager(impl: LedgerBleOperationManagerImpl): LedgerBleOperationManager = impl

    @Provides
    @Singleton
    fun provideIsBluetoothBondingRequired(impl: IsBluetoothBondingRequiredImpl): IsBluetoothBondingRequired = impl
}
