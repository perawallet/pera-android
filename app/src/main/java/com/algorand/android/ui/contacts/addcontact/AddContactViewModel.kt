/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.contacts.addcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.module.contacts.domain.usecase.GetContactByAddress
import com.algorand.android.module.contacts.domain.usecase.SaveContact
import com.algorand.android.models.OperationState
import com.algorand.android.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val saveContact: SaveContact,
    private val getContactByAddress: GetContactByAddress
) : ViewModel() {

    private val _contactOperationFlow = MutableStateFlow<Event<OperationState<Contact>>?>(null)
    val contactOperationFlow: StateFlow<Event<OperationState<Contact>>?> get() = _contactOperationFlow

    private val _contractSearchingFlow = MutableStateFlow<Event<OperationState<Contact?>>?>(null)
    val contractSearchingFlow: StateFlow<Event<OperationState<Contact?>>?> get() = _contractSearchingFlow

    fun insertContactToDatabase(contact: Contact) {
        viewModelScope.launch {
            saveContact(contact)
            _contactOperationFlow.emit(Event(OperationState.Create(contact)))
        }
    }

    fun checkIsContactExist(contactDatabaseAddress: String) {
        viewModelScope.launch {
            val contact = getContactByAddress(contactDatabaseAddress)
            _contractSearchingFlow.emit(Event(OperationState.Read(contact)))
        }
    }
}
