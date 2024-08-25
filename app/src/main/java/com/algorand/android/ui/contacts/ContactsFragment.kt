/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.contacts

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.algorand.android.R
import com.algorand.android.contacts.component.domain.model.Contact
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.databinding.FragmentContactsBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ScreenState
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.contact.ContactAdapter
import com.algorand.android.utils.hideKeyboard
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFragment : DaggerBaseFragment(R.layout.fragment_contacts) {

    private val endPlusIconButton by lazy { IconButton(R.drawable.ic_plus, onClick = ::addContactClick) }

    private val toolbarConfiguration = ToolbarConfiguration(
        backgroundColor = R.color.primary_background,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack,
        titleResId = R.string.contacts
    )

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration,
        firebaseEventScreenId = FIREBASE_EVENT_SCREEN_ID
    )

    private val contactsViewModel: ContactsViewModel by viewModels()

    private val binding by viewBinding(FragmentContactsBinding::bind)

    private var contactAdapter: ContactAdapter? = null

    private val contactListObserver = Observer<List<Contact>> { contactList ->
        contactAdapter?.submitList(contactList)
        setEndButtonVisibility(isVisible = contactList.isNotEmpty())
        with(binding) {
            searchBar.isVisible = contactList.isNotEmpty() || searchBar.text.isNotEmpty()
            contactsRecyclerView.isVisible = contactList.isNotEmpty() || searchBar.text.isNotEmpty()
            updateScreenState(contactList.isEmpty(), searchBar.text.isNotEmpty())
        }
    }

    private val onContactClick: (contact: Contact) -> Unit = { contact ->
        view?.hideKeyboard()
        nav(ContactsFragmentDirections.actionContactsFragmentToContactInfoFragment(contact))
    }

    private val onContactQrClick: (contact: Contact) -> Unit = { contact ->
        nav(ContactsFragmentDirections.actionGlobalShowQrNavigation(contact.name, contact.address))
    }

    private val emptyScreenState by lazy {
        ScreenState.CustomState(
            icon = R.drawable.ic_contacts,
            title = R.string.you_havent,
            description = R.string.you_can_make_the,
            buttonText = R.string.add_contact
        )
    }
    private val notFoundScreenState by lazy {
        ScreenState.CustomState(
            title = R.string.no_contact_found,
            description = R.string.try_different_contact_name
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchQueryEditTextWatcher()
        initObservers()
        setupToolbar()
    }

    private fun setupToolbar() {
        getAppToolbar()?.run {
            setEndButton(button = endPlusIconButton)
            setButtonVisibilityById(buttonId = endPlusIconButton.id, isVisible = false)
        }
    }

    private fun setEndButtonVisibility(isVisible: Boolean) {
        getAppToolbar()?.setButtonVisibilityById(buttonId = endPlusIconButton.id, isVisible = isVisible)
    }

    private fun initObservers() {
        contactsViewModel.contactsListLiveData.observe(viewLifecycleOwner, contactListObserver)
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(onContactClick, onContactQrClick)
        binding.contactsRecyclerView.adapter = contactAdapter
    }

    private fun setupSearchQueryEditTextWatcher() {
        binding.searchBar.setOnTextChanged { query ->
            contactsViewModel.updateContactsListLiveDataWithSearchQuery(query)
        }
    }

    private fun addContactClick() {
        nav(ContactsFragmentDirections.actionGlobalContactAdditionNavigation())
    }

    private fun updateScreenState(shouldShown: Boolean, isQueryStated: Boolean) {
        with(binding.screenStateView) {
            isVisible = shouldShown
            if (shouldShown) {
                if (isQueryStated) {
                    setupUi(notFoundScreenState)
                    clearNeutralButtonClickListener()
                } else {
                    setupUi(emptyScreenState)
                    setOnNeutralButtonClickListener(::addContactClick)
                }
            }
        }
    }

    companion object {
        private const val FIREBASE_EVENT_SCREEN_ID = "screen_contacts"
    }
}
