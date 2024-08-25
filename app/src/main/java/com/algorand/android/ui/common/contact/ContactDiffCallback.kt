package com.algorand.android.ui.common.contact

import androidx.recyclerview.widget.DiffUtil
import com.algorand.android.contacts.component.domain.model.Contact

class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {

    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}
