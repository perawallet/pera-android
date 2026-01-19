# Joint Account - Happy Path Flows

This document describes the main user flows introduced with the Joint Account feature.

---

## Overview

Joint Accounts are multi-signature accounts that require multiple participants to approve transactions. This feature enables:
- Creating shared wallets between multiple parties
- Requiring M-of-N signatures for transactions (e.g., 2-of-3)
- Collaborative asset management

---

## Flow 1: Create a Joint Account

**Actor:** User who wants to create a new joint account

**Preconditions:**
- User has at least one local account in the app
- User knows the addresses of other participants (or has them as contacts)

**Steps:**

```
1. Home Screen
   └── Tap "+" (Add Account)
       └── Select "Create Joint Account"
           └── Joint Account Info Dialog
               └── Tap "Continue"

2. Select Participants Screen
   └── Select local accounts to include
   └── (Optional) Add external addresses manually
   └── (Optional) Add contacts as participants
   └── Tap "Continue" (minimum 2 participants required)

3. Set Threshold Screen
   └── Choose how many signatures required (e.g., 2 of 3)
   └── Tap "Continue"

4. Name Joint Account Screen
   └── Enter account name (optional)
   └── Tap "Create"

5. Success
   └── Joint account created and appears in account list
   └── Other participants receive invitation in their inbox
```

**Result:** Joint account is created on-chain and added to the user's wallet. Invitations are sent to all participants.

---

## Flow 2: Accept Joint Account Invitation

**Actor:** User who received an invitation to join a joint account

**Preconditions:**
- User has a local account that was added as a participant
- Creator has already created the joint account

**Steps:**

```
1. Home Screen
   └── Notice inbox badge (new item)
       └── Tap Inbox icon

2. Inbox Screen
   └── See "Joint Account Invitation" item
       └── Tap on invitation

3. Joint Account Detail Screen
   └── View joint account details:
       - Account address
       - Threshold (e.g., 2 of 3)
       - List of all participants
   └── Tap "Add" to accept

4. Name Joint Account Screen
   └── Enter account name (optional)
   └── Tap "Add"

5. Success
   └── Joint account added to wallet
   └── Can now view balance and initiate transactions
```

**Alternative:** User can tap "Ignore" to dismiss the invitation without adding the account.

---

## Flow 3: Accept Invitation via Deep Link

**Actor:** User who received a shared link to join a joint account

**Preconditions:**
- User has Pera Wallet installed
- User has a local account that is a participant

**Steps:**

```
1. Receive Deep Link
   └── Click link: perawallet://joint-account-import?address=XXXXX
       └── App opens

2. Joint Account Detail Screen
   └── App fetches invitation details from server
   └── View joint account info
   └── Tap "Add"

3. Name Joint Account Screen
   └── Enter account name
   └── Tap "Add"

4. Success
   └── Joint account added to wallet
```

---

## Flow 4: Send ALGO/Asset from Joint Account

**Actor:** User who wants to send funds from a joint account

**Preconditions:**
- User has the joint account in their wallet
- Joint account has sufficient balance
- User owns at least one participant account

**Steps:**

```
1. Home Screen
   └── Tap on Joint Account
       └── Account Detail Screen

2. Account Detail Screen
   └── Tap "Send"
       └── Select asset (ALGO or ASA)

3. Send Flow
   └── Enter recipient address
   └── Enter amount
   └── Review transaction details
   └── Tap "Confirm"

4. Sign Request Created
   └── If user can sign: Prompted to sign immediately
   └── Transaction signed with user's participant account
   └── Sign request sent to other participants

5. Waiting for Signatures
   └── Other participants see sign request in their inbox
   └── Each participant signs when ready

6. Transaction Complete
   └── Once threshold signatures collected
   └── Transaction submitted to blockchain
   └── Funds transferred
```

---

## Flow 5: Sign a Pending Transaction

**Actor:** User who needs to sign a transaction initiated by another participant

**Preconditions:**
- User has the joint account in their wallet
- Another participant initiated a transaction
- User's signature is required

**Steps:**

```
1. Home Screen
   └── Notice inbox badge
       └── Tap Inbox icon

2. Inbox Screen
   └── See "Signature Request" item
       - Shows: "Signature request to sign for [Account]"
       - Shows: "Pending transaction"
       - Shows: "X of Y signed"
       - Shows: Time remaining
   └── Tap on request

3. Sign Request Detail Screen
   └── View transaction details:
       - Type (Send, Opt-in, etc.)
       - Amount
       - Recipient
       - Fee
       - Who has signed
       - Who is pending
   └── Tap "Sign"

4. Sign Transaction
   └── For standard account: Signs immediately
   └── For Ledger account: Connect Ledger → Approve on device

5. Success
   └── Signature submitted
   └── If threshold reached: Transaction broadcasts automatically
   └── If more signatures needed: Waits for other participants
```

**Alternative:** User can tap "Decline" to reject the transaction.

---

## Flow 6: Sign with Ledger Hardware Wallet

**Actor:** User with a Ledger account that is a joint account participant

**Preconditions:**
- Ledger device is charged and nearby
- Algorand app installed on Ledger
- User's Ledger account is a participant

**Steps:**

```
1. Open Sign Request
   └── (From inbox or after initiating transaction)

2. Tap "Sign"
   └── App detects Ledger account required

3. Connect Ledger
   └── Turn on Ledger device
   └── Open Algorand app on Ledger
   └── App connects via Bluetooth

4. Review on Ledger
   └── Transaction details shown on Ledger screen
   └── Press both buttons to approve

5. Success
   └── Signature captured from Ledger
   └── Submitted to server
```

---

## Flow 7: Add Asset to Joint Account (Opt-in)

**Actor:** User who wants to add a new asset to joint account

**Preconditions:**
- Joint account exists
- Asset not yet opted-in

**Steps:**

```
1. Joint Account Detail Screen
   └── Tap "Add Asset" or "+"

2. Search/Select Asset
   └── Search for asset by name or ID
   └── Select asset
   └── Tap "Add"

3. Sign Request Created
   └── Opt-in transaction created
   └── User signs if they can
   └── Request sent to other participants

4. Collect Signatures
   └── Other participants sign via inbox

5. Success
   └── Once threshold reached
   └── Asset appears in joint account
```

---

## Flow 8: Share/Export Joint Account

**Actor:** User who wants to share joint account with another participant

**Preconditions:**
- User has the joint account in their wallet

**Steps:**

```
1. Joint Account Detail Screen
   └── Tap options menu (⋮)
       └── Select "Export/Share Account"

2. Export Screen
   └── Option A: Tap "Copy URL"
       └── Link copied to clipboard
   └── Option B: Tap "Share"
       └── Share sheet opens
       └── Send via message, email, etc.

3. Recipient
   └── Receives link: perawallet://joint-account-import?address=XXXXX
   └── Opens link → Flow 3 (Accept via Deep Link)
```

---

## Flow 9: View Joint Account Details

**Actor:** User who wants to view joint account configuration

**Steps:**

```
1. Home Screen
   └── Tap on Joint Account

2. Account Detail Screen
   └── View balance (ALGO + assets)
   └── View transaction history
   └── Tap account icon/info

3. Joint Account Info Screen
   └── View:
       - Account address
       - Threshold (e.g., 2 of 3)
       - List of participants with:
         - Name (if contact/local account)
         - Address
         - Whether they're on this device
   └── Can edit contact names for participants
```

---

## Flow 10: Add External Participant as Contact

**Actor:** User who wants to save an external participant address as a contact

**Preconditions:**
- Joint account exists with external participant (not a local account)

**Steps:**

```
1. Joint Account Detail Screen
   └── Tap on participant (external address)

2. Options
   └── Tap "Edit Contact" or "Add to Contacts"

3. Edit Contact Screen
   └── Enter contact name
   └── (Optional) Add profile image
   └── Tap "Save"

4. Success
   └── Participant now shows with contact name
   └── Contact available throughout the app
```

---

## Flow 11: Add Contact as Participant During Creation

**Actor:** User creating a joint account who wants to add a contact as participant

**Steps:**

```
1. Select Participants Screen (during joint account creation)
   └── Tap "Add from Contacts" or contact icon

2. Contacts List
   └── Browse saved contacts
   └── Select contact(s) to add as participants

3. Continue
   └── Selected contacts added to participant list
   └── Proceed with joint account creation
```

---

## Flow 12: NFD (NFDomains) Display for Participants

**Actor:** User viewing joint account with participants who have NFD names

**Preconditions:**
- Participant address has an NFD name registered (e.g., "alice.algo")

**Steps:**

```
1. Joint Account Detail Screen
   └── View participant list

2. NFD Resolution
   └── App automatically resolves NFD names for addresses
   └── Participants with NFD show:
       - NFD name (e.g., "alice.algo")
       - NFD avatar (if set)
       - Address (shortened)

3. Display Priority
   └── If participant has:
       - Local account name → Shows local name
       - Contact name → Shows contact name  
       - NFD name → Shows NFD name
       - None → Shows shortened address
```

---

## Flow 13: Add Participant Using NFD Name

**Actor:** User adding a participant by their NFD name instead of address

**Steps:**

```
1. Select Participants Screen
   └── Tap "Add Address"

2. Enter NFD Name
   └── Type NFD name (e.g., "alice.algo")
   └── App resolves NFD to Algorand address

3. Confirmation
   └── Shows resolved address
   └── Shows NFD avatar/info
   └── Tap "Add"

4. Continue
   └── Participant added with NFD info
   └── NFD name displayed throughout flow
```

---

## Flow Summary Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     JOINT ACCOUNT FLOWS                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐      Invitation      ┌──────────────┐        │
│  │   Creator    │ ─────────────────►   │ Participant  │        │
│  │  (Flow 1)    │                      │  (Flow 2/3)  │        │
│  └──────┬───────┘                      └──────┬───────┘        │
│         │                                     │                │
│         │         Joint Account Created       │                │
│         └──────────────┬──────────────────────┘                │
│                        │                                       │
│                        ▼                                       │
│              ┌─────────────────┐                               │
│              │  Joint Account  │                               │
│              │    (Active)     │                               │
│              └────────┬────────┘                               │
│                       │                                        │
│         ┌─────────────┼─────────────┐                         │
│         ▼             ▼             ▼                         │
│   ┌──────────┐  ┌──────────┐  ┌──────────┐                    │
│   │  Send    │  │  Add     │  │  Share   │                    │
│   │ (Flow 4) │  │  Asset   │  │ (Flow 8) │                    │
│   └────┬─────┘  │ (Flow 7) │  └──────────┘                    │
│        │        └────┬─────┘                                   │
│        │             │                                         │
│        └──────┬──────┘                                         │
│               ▼                                                │
│      ┌─────────────────┐                                       │
│      │  Sign Request   │                                       │
│      │    Created      │                                       │
│      └────────┬────────┘                                       │
│               │                                                │
│               ▼                                                │
│      ┌─────────────────┐         ┌─────────────────┐          │
│      │  Participants   │ ──────► │   Transaction   │          │
│      │  Sign (Flow 5/6)│         │   Completed     │          │
│      └─────────────────┘         └─────────────────┘          │
│                                                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## Account Types Supported as Participants

| Account Type | Can Create | Can Sign | Notes |
|--------------|------------|----------|-------|
| Algo25 (Standard) | ✅ | ✅ | Full support |
| HD Key | ✅ | ✅ | Full support |
| Ledger | ✅ | ✅ | Requires device connection |
| Watch | ❌ | ❌ | View only, cannot participate |
| Rekeyed | ✅ | ✅ | Uses auth account for signing |

---

---

## Current Limitations & Requirements

### Sync Requirements

| Limitation | Description | Impact |
|------------|-------------|--------|
| All participants must add account | Each participant must add the joint account to their wallet to sign | Cannot sign if joint account not added locally |
| No automatic sync | Joint account is stored locally, not synced across devices | Must add on each device separately |
| Invitation required | Participants need invitation (via inbox or deep link) to add account | Cannot add joint account without invitation data |
| Inbox dependency | Sign requests only appear if joint account is added locally | May miss sign requests if account not added |

### Ledger Hardware Wallet Limitations

| Limitation | Description | Workaround |
|------------|-------------|------------|
| Arbitrary data (note field) | Ledger may reject transactions with arbitrary data/notes | Enable "Blind Signing" in Ledger Algorand app settings |
| Blind signing required | Some joint account operations may require blind signing enabled | User must enable in Ledger settings before signing |
| Transaction review | Complex transactions may be difficult to verify on Ledger screen | Trust the app display, verify addresses carefully |
| Connection stability | Bluetooth connection may drop during multi-step signing | Stay close to device, retry if disconnected |
| App state | Ledger Algorand app must be open during entire signing process | Keep app open until signing completes |

### Transaction Limitations

| Limitation | Description | Notes |
|------------|-------------|-------|
| Expiration time | Sign requests expire after a set time | All participants must sign before expiration |
| No partial execution | If threshold not met before expiration, transaction fails | Must coordinate with all required signers |
| Sequential signing | Each signature must be submitted to server | Cannot collect signatures offline |
| Single transaction type | Each sign request is for one transaction | Cannot batch multiple transactions |

### WalletConnect Limitations

| Limitation | Description | Notes |
|------------|-------------|-------|
| No direct dApp signing | Joint accounts cannot directly sign WalletConnect requests | Multi-sig requires coordination |
| dApp compatibility | Most dApps don't support multi-sig flow | dApp must handle partial signatures |
| Session connection | Joint account can connect to dApp | But signing requires all participants |
| Transaction flow | dApp sends tx → requires multi-sig collection | Not standard WalletConnect flow |
| Real-time signing | WalletConnect expects immediate response | Multi-sig may timeout waiting for signatures |

### Account Limitations

| Limitation | Description | Notes |
|------------|-------------|-------|
| Minimum 2 participants | Cannot create joint account with single participant | Use standard account for single ownership |
| Fixed threshold | Threshold cannot be changed after creation | Must create new joint account for different threshold |
| Fixed participants | Participants cannot be added/removed after creation | Must create new joint account for different participants |
| On-chain account | Joint account exists on Algorand blockchain | Subject to minimum balance requirements |

---

## Quick Reference

| Action | Entry Point | Result |
|--------|-------------|--------|
| Create Joint Account | Add Account → Joint Account | New joint account + invitations sent |
| Accept Invitation | Inbox → Invitation → Add | Joint account added locally |
| Accept via Link | Open deep link → Add | Joint account added locally |
| Send from Joint | Joint Account → Send | Sign request created |
| Sign Request | Inbox → Sign Request → Sign | Signature submitted |
| Add Asset | Joint Account → Add Asset | Opt-in sign request created |
| Share Account | Joint Account → Export/Share | Deep link generated |
| Add Participant as Contact | Joint Account → Participant → Edit | Contact saved |
| Add Contact as Participant | Create Joint → Add from Contacts | Contact added to participants |
| View NFD Name | Joint Account → View Participant | NFD name displayed if available |
| Add via NFD | Create Joint → Enter NFD name | NFD resolved to address |
