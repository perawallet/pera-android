# Joint Account Backend Feedback

This document contains feedback and issues identified during Android client implementation that require backend team attention.

---

## Critical Issues

### 1. Signed Joint Transactions Not Submitted to Blockchain

**Priority:** Critical

**Issue:** Successfully signed joint account transactions do not appear to be submitted to the Algorand blockchain. For example, when adding an asset (opt-in) to a joint account and all required signatures are collected, the asset does not appear in the account's asset list.

**Expected Behavior:** Once all required signatures (meeting the threshold) are collected, the transaction should be automatically submitted to the blockchain.

**Current Behavior:** Signatures are collected successfully, but the transaction is never broadcast.

**Impact:** Core functionality broken - joint accounts cannot perform any transactions.

---

### 2. Expired Inbox Items Still Listed as Pending

**Priority:** High

**Issue:** Expired sign request inbox items are still displayed as "pending" in the inbox list. They are not automatically removed or marked as expired.

**Expected Behavior:** 
- Expired items should be removed from the inbox OR
- Expired items should be marked with an "expired" status so the client can filter/display them appropriately

**Current Behavior:** Expired items remain in the inbox with "pending" status, confusing users who may attempt to sign expired requests.

---

### 3. Inbox Items Not Received Without Local Joint Account

**Priority:** High

**Issue:** Even if a user has the member/participant accounts locally, they do not receive inbox notifications (sign requests, invitations) unless they also have the joint account itself added to their local wallet.

**Expected Behavior:** Users should receive inbox items for any joint account where they are a participant, regardless of whether they have explicitly added the joint account locally.

**Current Behavior:** Inbox items are only delivered when the joint account is present in the user's local wallet.

**Workaround:** Users must first import/add the joint account before they can receive notifications.

---

## Feature Requests

### 4. Sign Request Creation Time

**Priority:** Medium

**Request:** Include the `created_at` timestamp in the sign request response.

**Reason:** The Android client needs to display the creation time in the inbox list to help users understand when a request was initiated and prioritize accordingly.

**Current State:** Creation time is not available in the API response.

**Suggested API Change:**
```json
{
  "id": "sign_request_id",
  "status": "pending",
  "created_at": "2026-01-20T10:30:00Z",  // Add this field
  "expires_at": "2026-01-21T10:30:00Z",
  ...
}
```

---

### 5. Batch Signature Submission

**Priority:** Medium

**Request:** Allow submitting multiple signatures in a single API call.

**Reason:** When a user has multiple local accounts that are participants in the same joint account, they should be able to sign and submit all signatures at once rather than making separate API calls for each signature.

**Current Implementation (Client-side workaround):**
```kotlin
// Currently we call addSignature for each local signer sequentially
for (signer in localSigners) {
    addJointAccountSignature(signRequestId, signer.address, signature)
}
```

**Suggested API Enhancement:**
```json
POST /joint-accounts/{address}/sign-requests/{id}/signatures/batch
{
  "signatures": [
    { "signer": "ADDRESS_1", "signature": "BASE64_SIG_1" },
    { "signer": "ADDRESS_2", "signature": "BASE64_SIG_2" }
  ]
}
```

**Benefits:**
- Reduced API calls
- Atomic operation (all signatures succeed or fail together)
- Better user experience

---

### 6. Multi-Member Same Device Inbox Issues

**Priority:** Medium

**Issue:** When multiple joint account members/participants exist on the same device, inbox items are not received correctly. The backend may need to review how inbox notifications are delivered in this scenario.

**Scenario:**
1. Device has Account A and Account B
2. Both A and B are participants in Joint Account X
3. A sign request is created for Joint Account X
4. Expected: Both A and B should see the inbox item
5. Actual: Inconsistent delivery - sometimes only one account receives the notification

**Request:** Review and fix the inbox notification delivery logic for multi-participant same-device scenarios.

---

## Clarifications Needed

### 7. Export/Share and Import Flow Security

**Priority:** Medium

**Questions:**
1. Can anyone add a joint account to their wallet using only the joint account address, without any invitation?
2. What validation occurs when importing a joint account via deep link?
3. Is there any authentication/authorization for joint account import?

**Current Android Implementation:**
- Joint accounts can be shared via deep link containing: address, threshold, participant addresses
- Any user with the deep link can import the joint account
- No server-side validation occurs during import (client-side only)

**Security Concerns:**
- Should there be an invitation/approval system?
- Should participants be notified when someone imports the joint account?
- Should there be a way to "lock" a joint account from new imports?

**Request:** Please clarify the intended security model for joint account sharing/importing.

---

## API Endpoint Summary

### Current Endpoints Used:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/joint-accounts` | POST | Create joint account |
| `/joint-accounts/{address}` | GET | Get joint account details |
| `/joint-accounts/{address}/sign-requests` | POST | Propose new sign request |
| `/joint-accounts/{address}/sign-requests/{id}` | GET | Get sign request details |
| `/joint-accounts/{address}/sign-requests/{id}/signatures` | POST | Add signature |
| `/inbox/search` | POST | Search inbox items |
| `/inbox/{id}` | DELETE | Delete inbox item |

### Suggested New/Modified Endpoints:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/joint-accounts/{address}/sign-requests/{id}/signatures/batch` | POST | Add multiple signatures |
| Sign request response | - | Add `created_at` field |

---

## Contact

For questions about this feedback, please contact the Android team.

**Last Updated:** January 20, 2026
