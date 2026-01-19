# Joint Account QA Testing Guide

This document outlines all test scenarios for the Joint Account feature.

---

## Table of Contents
1. [Joint Account Creation](#1-joint-account-creation)
2. [Joint Account Invitation](#2-joint-account-invitation)
3. [Inbox & Notifications](#3-inbox--notifications)
4. [Transaction Signing](#4-transaction-signing)
5. [Ledger Hardware Wallet](#5-ledger-hardware-wallet)
6. [Deep Link Flows](#6-deep-link-flows)
7. [WalletConnect with Joint Account](#7-walletconnect-with-joint-account)
8. [Export/Share Account](#8-exportshare-account)
9. [Account Management](#9-account-management)
10. [Limitations & Constraints Testing](#10-limitations--constraints-testing)
11. [Edge Cases & Error Handling](#11-edge-cases--error-handling)
12. [Multi-Device Scenarios](#12-multi-device-scenarios)
13. [Performance & Stress Testing](#13-performance--stress-testing)

---

## 1. Joint Account Creation

### 1.1 Basic Creation Flow
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 1.1.1 | Create 2-of-2 joint account | 1. Add Account → Joint Account<br>2. Select 2 participant accounts<br>3. Set threshold to 2<br>4. Enter name<br>5. Confirm | Joint account created successfully, appears in account list |
| 1.1.2 | Create 2-of-3 joint account | 1. Select 3 participants<br>2. Set threshold to 2<br>3. Complete creation | Joint account created with 2-of-3 threshold |
| 1.1.3 | Create 3-of-3 joint account | Select 3 participants, threshold 3 | Joint account created |
| 1.1.4 | Create with minimum participants (2) | Select exactly 2 participants | Should work |
| 1.1.5 | Create with maximum participants | Select maximum allowed participants | Should work or show limit error |

### 1.2 Participant Selection
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 1.2.1 | Select local accounts only | Choose from local accounts | Works correctly |
| 1.2.2 | Add external address manually | Enter external Algorand address | Address added to participants |
| 1.2.3 | Add contact as participant | Select from contacts list | Contact added as participant with name |
| 1.2.4 | Mix of local + external participants | Select 1 local + 1 external address | Both added correctly |
| 1.2.5 | Duplicate participant prevention | Try to add same address twice | Should prevent/show error |
| 1.2.6 | Invalid address format | Enter invalid Algorand address | Show validation error |
| 1.2.7 | Add multiple contacts | Select several contacts as participants | All contacts added correctly |
| 1.2.8 | Contact without valid address | Select contact with invalid/empty address | Show error or prevent selection |
| 1.2.9 | Search contacts | Search for contact by name | Matching contacts shown |
| 1.2.10 | Add participant via NFD name | Enter "alice.algo" instead of address | NFD resolved, participant added |
| 1.2.11 | Invalid NFD name | Enter non-existent NFD | Error message shown |
| 1.2.12 | Paste NFD name | Paste NFD name from clipboard | NFD resolved correctly |

### 1.3 Threshold Selection
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 1.3.1 | Threshold = participants | Set threshold equal to participant count | Valid |
| 1.3.2 | Threshold < participants | Set threshold less than participant count | Valid |
| 1.3.3 | Threshold > participants | Try to set threshold higher than participants | Should be prevented |
| 1.3.4 | Threshold = 1 | Set threshold to 1 | Should this be allowed? Verify behavior |

### 1.4 Naming
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 1.4.1 | Custom name | Enter custom account name | Name saved and displayed |
| 1.4.2 | Empty name | Leave name empty | Default name assigned or validation error |
| 1.4.3 | Long name | Enter very long name (100+ chars) | Truncated or validation error |
| 1.4.4 | Special characters | Use emojis, symbols in name | Should handle gracefully |

---

## 2. Joint Account Invitation

### 2.1 Receiving Invitations
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 2.1.1 | Receive invitation as participant | Other user creates joint account with your address | Invitation appears in inbox |
| 2.1.2 | Accept invitation | Click "Add" on invitation | Joint account added locally |
| 2.1.3 | Reject/Ignore invitation | Click "Ignore" on invitation | Invitation dismissed, not added |
| 2.1.4 | View invitation details | Open invitation | Shows threshold, all participants |
| 2.1.5 | Multiple pending invitations | Receive multiple invitations | All appear in inbox separately |

### 2.2 Invitation Persistence
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 2.2.1 | Invitation persists after app restart | Receive invitation → close app → reopen | Invitation still in inbox |
| 2.2.2 | Invitation after accepting | Accept invitation | Invitation removed from inbox |
| 2.2.3 | Invitation after ignoring | Ignore invitation | Invitation removed from inbox |

---

## 3. Inbox & Notifications

### 3.1 Inbox Display
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 3.1.1 | Empty inbox | No pending items | Shows empty state |
| 3.1.2 | Joint account invitations | Pending invitations exist | Shows in inbox with correct info |
| 3.1.3 | Sign requests | Pending sign requests exist | Shows in inbox with status |
| 3.1.4 | Mixed inbox items | Invitations + sign requests + asset inbox | All displayed correctly |
| 3.1.5 | Inbox badge count | Multiple pending items | Badge shows correct count |

### 3.2 Sign Request Display
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 3.2.1 | Pending sign request | Sign request waiting for signatures | Shows "Pending", signature count (e.g., "1 of 4 signed") |
| 3.2.2 | Time remaining | Sign request with expiration | Shows time left (e.g., "≈ 2h left") |
| 3.2.3 | Expired sign request | Sign request past expiration | **[KNOWN ISSUE]** Currently still shows as "Pending" |
| 3.2.4 | Fully signed request | All signatures collected | Should show completed or be removed |

### 3.3 Inbox Refresh
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 3.3.1 | Pull to refresh | Pull down on inbox | Refreshes inbox items |
| 3.3.2 | Auto-refresh on open | Open inbox tab | Fetches latest items |
| 3.3.3 | Background refresh | App in background, new item arrives | Shows on next app open |

---

## 4. Transaction Signing

### 4.1 Initiating Transactions
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 4.1.1 | Send ALGO from joint account | Joint Account → Send → Enter amount → Confirm | Sign request created |
| 4.1.2 | Send ASA from joint account | Send asset from joint account | Sign request created |
| 4.1.3 | Opt-in to ASA | Add asset to joint account | Sign request created |
| 4.1.4 | Opt-out of ASA | Remove asset from joint account | Sign request created |

### 4.2 Signing Flow
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 4.2.1 | Sign with local account | Open sign request → Sign | Signature submitted |
| 4.2.2 | Sign with Ledger | Sign request requires Ledger account | Ledger signing flow works |
| 4.2.3 | Decline sign request | Click "Decline" | Request declined, removed from inbox |
| 4.2.4 | Sign partial (2-of-3) | 1st signer signs, 2nd signer signs | Each signature recorded |
| 4.2.5 | Complete signing | All required signatures collected | **[KNOWN ISSUE]** Transaction may not be submitted to blockchain |

### 4.3 Sign Request Details
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 4.3.1 | View transaction details | Open sign request | Shows amount, recipient, fees |
| 4.3.2 | View signer status | Open sign request | Shows who signed, who pending |
| 4.3.3 | View expiration | Open sign request | Shows time remaining |

### 4.4 Signing Edge Cases
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 4.4.1 | Sign expired request | Try to sign after expiration | Should show error |
| 4.4.2 | Sign already-signed request | Same account tries to sign twice | Should prevent/show already signed |
| 4.4.3 | Sign with insufficient balance | Joint account has insufficient funds | Error before or during signing |
| 4.4.4 | Network error during signing | Lose connection while signing | Graceful error handling |

---

## 5. Ledger Hardware Wallet

### 5.1 Joint Account Creation with Ledger
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 5.1.1 | Create with Ledger account as participant | Select Ledger account + standard account | Joint account created |
| 5.1.2 | Create with multiple Ledger accounts | Select 2+ Ledger accounts as participants | Joint account created |
| 5.1.3 | Create with only Ledger accounts | All participants are Ledger accounts | Joint account created |
| 5.1.4 | Mix Ledger + Algo25 + HD Key | Different account types as participants | Joint account created |

### 5.2 Signing with Ledger
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 5.2.1 | Sign request with Ledger | Open sign request → Connect Ledger → Approve | Signature submitted via Ledger |
| 5.2.2 | Ledger not connected | Try to sign, Ledger disconnected | Prompt to connect Ledger |
| 5.2.3 | Ledger app not open | Ledger connected but Algorand app not open | Prompt to open Algorand app |
| 5.2.4 | User rejects on Ledger | User presses reject on Ledger device | Transaction cancelled gracefully |
| 5.2.5 | Ledger timeout | User doesn't respond on Ledger | Timeout error with retry option |
| 5.2.6 | Wrong Ledger account | Connected Ledger doesn't have required account | Show error, prompt correct Ledger |

### 5.3 Ledger Connection Scenarios
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 5.3.1 | Bluetooth Ledger (Nano X) | Connect via Bluetooth | Signing works |
| 5.3.2 | USB Ledger (Nano S/S+) | Connect via USB-C/OTG | Signing works (if supported) |
| 5.3.3 | Ledger disconnects mid-signing | Ledger disconnects during sign | Error with reconnect option |
| 5.3.4 | Multiple Ledger devices | Multiple Ledgers paired | Correct Ledger selected |
| 5.3.5 | Ledger battery low | Low battery during signing | Warning or graceful handling |

### 5.4 Multi-Signature with Ledger
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 5.4.1 | 2-of-2: Ledger + Standard | Sign with standard first, then Ledger | Both signatures collected |
| 5.4.2 | 2-of-2: Both Ledger | Two different Ledger accounts need to sign | Can sign with both Ledgers |
| 5.4.3 | 2-of-3: Mixed accounts | Ledger + Standard + HD Key | Any 2 can complete signing |
| 5.4.4 | Same Ledger, multiple accounts | One Ledger has 2 participant accounts | Can sign for both accounts |

### 5.5 Ledger Edge Cases
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 5.5.1 | Ledger firmware update needed | Outdated Ledger firmware | Prompt to update or graceful error |
| 5.5.2 | Algorand app update needed | Outdated Ledger Algorand app | Prompt to update |
| 5.5.3 | Blind signing disabled | Transaction requires blind signing | Clear instructions to enable |
| 5.5.4 | Ledger in another app | Ledger busy with another app | Wait or error message |
| 5.5.5 | Large transaction on Ledger | Many operations in one transaction | Ledger can display/approve all |

### 5.6 Ledger Arbitrary Data / Blind Signing
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 5.6.1 | Transaction with note field | Sign tx with arbitrary data/note | May require blind signing enabled |
| 5.6.2 | Blind signing disabled - with note | Sign tx with note, blind signing OFF | Ledger rejects, show enable instructions |
| 5.6.3 | Blind signing enabled - with note | Sign tx with note, blind signing ON | Transaction signs successfully |
| 5.6.4 | App call transaction | Sign application call transaction | May require blind signing |
| 5.6.5 | Asset config transaction | Sign asset configuration tx | Verify Ledger can process |
| 5.6.6 | Clear instructions | User needs to enable blind signing | App shows clear step-by-step instructions |
| 5.6.7 | Verify Ledger settings | Check if blind signing is enabled | Inform user of current setting if possible |

### 5.7 Ledger + Rekeyed Accounts
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 5.7.1 | Rekeyed to Ledger as participant | Account rekeyed to Ledger is participant | Signing uses Ledger |
| 5.7.2 | Ledger rekeyed to standard | Ledger account rekeyed to standard | Signing uses new auth |
| 5.7.3 | Joint account with rekeyed participants | Mix of rekeyed accounts | Correct signing flow for each |

---

## 6. Deep Link Flows

### 6.1 Joint Account Import Deep Link
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 6.1.1 | Valid deep link - participant | Open `perawallet://joint-account-import?address=...` as participant | Shows invitation details |
| 6.1.2 | Valid deep link - non-participant | Open deep link, user is NOT a participant | Shows error or empty state |
| 6.1.3 | Invalid address in deep link | Open deep link with malformed address | Shows error |
| 6.1.4 | Deep link - account already added | Open deep link for already-added joint account | Shows account details (no Add button) |
| 6.1.5 | Deep link - app not installed | Open deep link without Pera installed | Redirects to app store |

### 6.2 Deep Link States
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 6.2.1 | Deep link - app in foreground | Click deep link while app open | Navigates to joint account |
| 6.2.2 | Deep link - app in background | Click deep link, app in background | App opens, navigates correctly |
| 6.2.3 | Deep link - app killed | Click deep link, app not running | App starts, navigates correctly |
| 6.2.4 | Deep link - not logged in | Click deep link, user not authenticated | Handle auth first, then navigate |

---

## 7. WalletConnect with Joint Account

### 7.1 WalletConnect Connection
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 7.1.1 | Connect joint account to dApp | Scan WC QR with joint account selected | Connection established |
| 7.1.2 | dApp shows joint account address | Connect and check dApp UI | Joint account address displayed |
| 7.1.3 | Multiple accounts including joint | Connect with standard + joint accounts | Both accounts available |
| 7.1.4 | Disconnect joint account | Disconnect WC session | Session terminated cleanly |

### 7.2 WalletConnect Transaction Requests
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 7.2.1 | dApp requests signature | dApp sends tx request to joint account | Shows multi-sig requirement |
| 7.2.2 | Sign request handling | Receive WC tx request for joint account | Creates sign request for participants |
| 7.2.3 | Immediate response | dApp expects immediate signature | Handle timeout/pending state |
| 7.2.4 | Transaction rejection | Reject WC transaction | dApp receives rejection |
| 7.2.5 | Partial signing | One participant signs via WC | Other participants notified |

### 7.3 WalletConnect Limitations Testing
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 7.3.1 | dApp timeout | dApp times out waiting for multi-sig | Graceful handling |
| 7.3.2 | Session expires during signing | WC session expires before all sign | Clear error messaging |
| 7.3.3 | dApp doesn't support multi-sig | Standard dApp + joint account tx | Show limitation message |
| 7.3.4 | Complex dApp transaction | DeFi swap via joint account | May not be supported |
| 7.3.5 | Multiple tx request | dApp sends batch transactions | Handle appropriately |

### 7.4 WalletConnect v2 Specifics
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 7.4.1 | WC v2 pairing | Pair with WC v2 dApp | Pairing works |
| 7.4.2 | WC v2 session | Establish session with joint account | Session established |
| 7.4.3 | WC v2 namespaces | Joint account in Algorand namespace | Properly configured |
| 7.4.4 | WC v2 events | dApp events to joint account | Events received |

---

## 8. Export/Share Account

### 8.1 Export Flow
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 8.1.1 | Copy export URL | Joint Account → Options → Export → Copy | URL copied to clipboard |
| 8.1.2 | Share export URL | Joint Account → Options → Export → Share | Share sheet opens with URL |
| 8.1.3 | Export URL format | Check exported URL | Format: `perawallet://joint-account-import?address=...` |

### 8.2 QR Code (if applicable)
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 8.2.1 | Generate QR code | Export joint account as QR | QR code displayed |
| 8.2.2 | Scan QR code | Scan joint account QR | Import flow initiated |

---

## 9. Account Management

### 9.1 Account Display
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 9.1.1 | Joint account in list | View accounts list | Joint account shows with correct icon/badge |
| 9.1.2 | Joint account balance | View joint account | Shows correct ALGO and ASA balances |
| 9.1.3 | Joint account details | Open joint account | Shows threshold, participants |

### 9.2 Account Actions
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 9.2.1 | Rename joint account | Edit account name | Name updated |
| 9.2.2 | Remove joint account | Remove from device | Account removed locally (not on-chain) |
| 9.2.3 | View participants | Open joint account details | All participants listed with names/addresses |
| 9.2.4 | Edit participant contact | Click participant → Edit | Can update contact name/image |

### 9.3 Asset Management
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 9.3.1 | View assets | Open joint account | All opted-in assets displayed |
| 9.3.2 | Add asset (opt-in) | Add asset to joint account | Creates sign request for opt-in |
| 9.3.3 | Remove asset (opt-out) | Remove asset from joint account | Creates sign request for opt-out |

### 9.4 Contacts Integration
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 9.4.1 | View participant as contact | Joint account with contact as participant | Shows contact name and image |
| 9.4.2 | Add participant as new contact | Tap external participant → Add to Contacts | Contact created with address |
| 9.4.3 | Edit participant contact | Tap participant → Edit Contact | Can update name/image |
| 9.4.4 | Participant already a contact | View participant who is saved contact | Shows existing contact info |
| 9.4.5 | Delete contact used as participant | Delete contact from contacts list | Participant shows address only |
| 9.4.6 | Update contact name | Change contact name in contacts | Participant name updates in joint account |
| 9.4.7 | Contact with profile image | Add contact with image as participant | Image shows in participant list |
| 9.4.8 | Local account vs contact | Same address as local account and contact | Prioritize local account display |

### 9.5 NFD (NFDomains) Integration
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 9.5.1 | Participant with NFD name | View participant who has NFD registered | Shows NFD name (e.g., "alice.algo") |
| 9.5.2 | NFD avatar display | Participant has NFD with avatar | Shows NFD avatar image |
| 9.5.3 | Add participant via NFD | Enter "alice.algo" as participant | Resolves to address, adds participant |
| 9.5.4 | Invalid NFD name | Enter non-existent NFD name | Shows error "NFD not found" |
| 9.5.5 | NFD resolution failure | Network error during NFD lookup | Graceful error handling |
| 9.5.6 | Display priority: Local > Contact > NFD | Participant has local name, contact, and NFD | Shows local account name first |
| 9.5.7 | Display priority: Contact > NFD | Participant has contact name and NFD | Shows contact name |
| 9.5.8 | Display priority: NFD > Address | Participant has only NFD, no contact | Shows NFD name |
| 9.5.9 | NFD name updates | Participant changes their NFD name | Updated name reflects in app |
| 9.5.10 | Multiple NFD segments | NFD with segments (e.g., "alice.wallet.algo") | Displays correctly |
| 9.5.11 | NFD in sign request | Sign request from participant with NFD | Shows NFD name in signer list |
| 9.5.12 | NFD in inbox | Inbox item from participant with NFD | Shows NFD name in inbox list |

---

## 10. Limitations & Constraints Testing

### 10.1 Sync Requirements
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 10.1.1 | Sign without local joint account | Try to sign request without joint account added | Cannot sign, prompt to add account |
| 10.1.2 | Inbox without local joint account | Check inbox without joint account added | Sign requests may not appear (known issue) |
| 10.1.3 | Different devices, same participant | Same account on 2 devices | Must add joint account on each device |
| 10.1.4 | Remove and re-add joint account | Remove joint account, then re-add via invitation | Should work, data restored |
| 10.1.5 | Offline account addition | Try to add joint account while offline | Should fail gracefully |

### 10.2 Transaction Expiration
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 10.2.1 | Sign just before expiration | Sign with seconds remaining | Should succeed if submitted in time |
| 10.2.2 | Sign after expiration | Try to sign expired request | Clear error message |
| 10.2.3 | Expiration during signing | Request expires while user is signing | Handle gracefully |
| 10.2.4 | Long Ledger signing | Ledger takes time, request expires | Handle expiration gracefully |
| 10.2.5 | Threshold not met before expiry | Only 1 of 2 signed before expiration | Transaction fails, clear messaging |

### 10.3 Account Constraints
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 10.3.1 | Try single participant | Attempt to create with 1 participant | Should be prevented |
| 10.3.2 | Maximum participants | Create with maximum allowed participants | Should work or show limit |
| 10.3.3 | Change threshold after creation | Look for option to change threshold | Not possible (by design) |
| 10.3.4 | Add participant after creation | Look for option to add participant | Not possible (by design) |
| 10.3.5 | Remove participant after creation | Look for option to remove participant | Not possible (by design) |
| 10.3.6 | Minimum balance | Joint account below minimum balance | Prevent transactions appropriately |

---

## 11. Edge Cases & Error Handling

### 11.1 Network Errors
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 11.1.1 | No internet - create joint account | Try to create without network | Appropriate error message |
| 11.1.2 | No internet - sign request | Try to sign without network | Appropriate error message |
| 11.1.3 | No internet - fetch inbox | Open inbox without network | Shows cached data or error |
| 11.1.4 | Timeout during operation | Slow network causes timeout | Graceful error, retry option |

### 11.2 Invalid States
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 11.2.1 | Participant account removed | Remove local account that's a participant | Handle gracefully |
| 11.2.2 | Joint account closed on-chain | Joint account closed externally | Show appropriate state |
| 11.2.3 | Corrupted local data | Local joint account data corrupted | Error handling, recovery option |

### 11.3 Concurrent Operations
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 11.3.1 | Multiple users sign simultaneously | 2 users sign at same time | Both signatures recorded |
| 11.3.2 | Create while offline, sync later | Create joint account offline | Syncs when online |

---

## 12. Multi-Device Scenarios

### 12.1 Same Account on Multiple Devices
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 12.1.1 | Sign request on both devices | Same participant account on 2 devices | Sign request appears on both |
| 12.1.2 | Sign on one device | Sign on Device A | Status updates on Device B |
| 12.1.3 | Add joint account on one device | Add on Device A | Should appear on Device B (if synced) |

### 12.2 Multiple Participants on Same Device
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 12.2.1 | Both participants on same phone | Account A and B on same device, both are participants | **[KNOWN ISSUE]** Inbox items may not display correctly |
| 12.2.2 | Sign with both accounts | Sign request needs both A and B | Can sign with both from same device |

---

## 13. Performance & Stress Testing

### 13.1 Load Testing
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 13.1.1 | Many joint accounts | Add 20+ joint accounts | App performs well |
| 13.1.2 | Many pending sign requests | 50+ pending sign requests | Inbox loads reasonably |
| 13.1.3 | Large participant list | Joint account with many participants | Details load correctly |

### 13.2 Memory & Battery
| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 13.2.1 | Extended inbox polling | Leave app open on inbox | No excessive battery drain |
| 13.2.2 | Background operation | App in background for hours | No memory leaks |

---

## Known Issues (Reference)

These issues are documented in `BACKEND_FEEDBACK.md`:

1. **Expired inbox items still listed as pending** - Expired sign requests show "Pending" with "≈ 0m left"
2. **Missing sign request creation time** - Cannot show "time ago" in inbox
3. **No batch signature submission** - Must submit signatures one request at a time
4. **Multiple participants on same device** - Inbox items not received correctly
5. **Inbox items require local joint account** - Can't receive invitations without joint account added
6. **Signed transactions not submitted** - Completed sign requests may not be broadcast to blockchain
7. **Export/Import security model unclear** - Need clarification on validation

---

## Test Environment Checklist

### Device & OS
- [ ] Test on Android (minimum SDK 28)
- [ ] Test on Android (latest SDK version)
- [ ] Test on various screen sizes (phone, tablet)
- [ ] Test light mode and dark mode

### Network Conditions
- [ ] Test with fast WiFi
- [ ] Test with slow network (3G simulation)
- [ ] Test with intermittent connectivity
- [ ] Test offline mode

### Account Types
- [ ] Test with Algo25 (standard) accounts
- [ ] Test with HD Key accounts
- [ ] Test with Watch accounts (as non-participant viewer)
- [ ] Test with multiple account types mixed

### Ledger Hardware Wallet
- [ ] Test with Ledger Nano X (Bluetooth)
- [ ] Test with Ledger Nano S/S+ (if USB supported)
- [ ] Test Ledger connection/disconnection scenarios
- [ ] Test Ledger timeout scenarios
- [ ] Test Ledger rejection scenarios
- [ ] Test with multiple Ledger accounts

### App State
- [ ] Test fresh install
- [ ] Test upgrade from previous version
- [ ] Test with existing joint accounts (migration)
- [ ] Test with large number of accounts (20+)

---

## Reporting Issues

When reporting issues, please include:
1. Device model and Android version
2. App version
3. Steps to reproduce
4. Expected vs actual behavior
5. Screenshots/screen recordings
6. Logs (if available)
