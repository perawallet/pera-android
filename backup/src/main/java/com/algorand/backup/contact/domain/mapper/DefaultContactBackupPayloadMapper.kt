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

package com.algorand.backup.contact.domain.mapper

import com.algorand.backup.contact.data.model.ContactBackupData
import com.algorand.backup.contact.data.model.ContactBackupWrapper
import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import kotlin.io.encoding.Base64

internal class DefaultContactBackupPayloadMapper @Inject constructor(
    private val gson: Gson
) : ContactBackupPayloadMapper {

    override fun serialize(payload: ContactBackupPayload): ByteArray {
        val wrapper = ContactBackupWrapper(
            type = CONTACT_TYPE,
            data = encodeData(ContactBackupData(payload.address, payload.name))
        )
        return gson.toJson(wrapper).toByteArray(Charsets.UTF_8)
    }

    override fun deserialize(bytes: ByteArray): ContactBackupPayload? {
        return try {
            val wrapper = gson.fromJson(String(bytes, Charsets.UTF_8), ContactBackupWrapper::class.java)
            mapToPayload(wrapper)
        } catch (e: Exception) {
            null
        }
    }

    private fun mapToPayload(wrapper: ContactBackupWrapper): ContactBackupPayload? {
        return when (wrapper.type) {
            CONTACT_TYPE -> decodeData<ContactBackupData>(wrapper.data)?.let {
                ContactBackupPayload(it.algoAddress, it.name)
            }
            else -> null
        }
    }

    private fun <T> encodeData(data: T): String {
        return Base64.encode(gson.toJson(data).toByteArray())
    }

    private inline fun <reified T> decodeData(data: String): T? {
        return try {
            val decoded = String(Base64.decode(data))
            gson.fromJson(decoded, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val CONTACT_TYPE = "contact"
    }
}
