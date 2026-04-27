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

package com.algorand.backup.contact.domain.usecase

import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.backup.contact.domain.model.ContactChangeEvent
import javax.inject.Inject

internal class BackupContactChangeProcessor @Inject constructor() {

    private var baseline: Map<String, ContactBackupPayload>? = null

    fun reset() {
        baseline = null
    }

    fun process(contacts: List<ContactBackupPayload>): ContactChangeEvent {
        val current = contacts.associateBy { it.address }
        val previous = baseline
        baseline = current

        if (previous == null) return ContactChangeEvent.InitialSnapshot

        val added = current.keys - previous.keys
        val removed = previous.keys - current.keys
        val renamed = current.keys.intersect(previous.keys).filter { address ->
            current[address]?.name != previous[address]?.name
        }.toSet()

        if (added.isEmpty() && removed.isEmpty() && renamed.isEmpty()) return ContactChangeEvent.NoChange

        return ContactChangeEvent.Changed(added, removed, renamed)
    }
}
