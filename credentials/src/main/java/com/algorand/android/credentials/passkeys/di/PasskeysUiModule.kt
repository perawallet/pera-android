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

package com.algorand.android.credentials.passkeys.di

import com.algorand.android.credentials.passkeys.ui.mapper.CreatePasskeyParamsMapper
import com.algorand.android.credentials.passkeys.ui.mapper.CreatePublicKeyCredentialResponseArgsMapper
import com.algorand.android.credentials.passkeys.ui.mapper.DefaultCreatePasskeyParamsMapper
import com.algorand.android.credentials.passkeys.ui.mapper.DefaultCreatePublicKeyCredentialResponseArgsMapper
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePublicKeyCredentialResponseProcessor
import com.algorand.android.credentials.passkeys.ui.viewmodel.DefaultCreatePublicKeyCredentialResponseProcessor
import com.algorand.android.credentials.passkeys.validator.CreatePasskeyIntentValidator
import com.algorand.android.credentials.passkeys.validator.DefaultCreatePasskeyIntentValidator
import com.algorand.android.credentials.passkeys.validator.DefaultGetPasskeyIntentValidator
import com.algorand.android.credentials.passkeys.validator.GetPasskeyIntentValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object PasskeysUiModule {

    @Provides
    fun provideCreatePasskeyParamsMapper(mapper: DefaultCreatePasskeyParamsMapper): CreatePasskeyParamsMapper = mapper

    @Provides
    fun provideCreatePublicKeyCredentialResponseArgsMapper(
        mapper: DefaultCreatePublicKeyCredentialResponseArgsMapper
    ): CreatePublicKeyCredentialResponseArgsMapper = mapper

    @Provides
    fun provideCreatePasskeyIntentValidator(
        validator: DefaultCreatePasskeyIntentValidator
    ): CreatePasskeyIntentValidator = validator

    @Provides
    fun provideGetPasskeyIntentValidator(
        validator: DefaultGetPasskeyIntentValidator
    ): GetPasskeyIntentValidator = validator

    @Provides
    fun provideCreatePublicKeyCredentialResponseProcessor(
        processor: DefaultCreatePublicKeyCredentialResponseProcessor
    ): CreatePublicKeyCredentialResponseProcessor = processor
}
