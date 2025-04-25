# Secure Key Management for Self-Custody Wallet

## Principles

- **Sensisble information should be encrypted at rest**
- **ONLY** the necessary information for the operation should be decrypted and loaded into memory
- **Sensitive information should be cleared from memory as soon as possible**
    - In some environments, such as JVM, some trust needs to be placed on the garbage collector. Ideally, this could be minimized with a compartmentalized design, where the application & key management don't share the same memory space.
- **Compartmentalize** run-time enviroments of application vs key management. (e.g. separate processes, containers, etc). 
    - This is for a future iteration
    - Dedicated design will be needed for this

## KMS Storage for xHDWallets

```mermaid
---
title: AddressDatabase
---
erDiagram
    hd_seed {
        Int seed_id PK
        ByteArray encrypted_mnemonic_entropy
        String entropy_custom_name
        ByteArray encrypted_seed
    }

    hd_keys {
        String address PK
        ByteArray public_key
        ByteArray encrypted_private_key
        Int seed_id FK
        Int account
        Int change
        Int key_index
        Enum derivation_type
    }
    
    hd_keys ||--o{ hd_seed : links
```


## Flows

### Importing Mnemonic

- User imports mnemonic
- With BIP39 lib, calculates entropy and seed
- Store seed and entropy in `hd_seed` table
- Clear from memory

#### BIP44 Address Scanning

- Apply BIP44 recovery algorithm to recover addresses
    - Reference: https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki#account-discovery
- Store addresses found in `hd_keys` table
- Use backend endpoint: `GET ...` to understand if an address has history or not

### Display or Verify Mnemonic
- Decrypt `encrypted_mnemonic_entropy` from hd_seed table
- Using BIP39; Convert it to mnemonic, display / verify
- Clear from memory

### Deriving new keys / addresses

- Decrypt `encrypted_seed` from hd_seed table
- Use `seed` with xHDWallets lib to derive keys
- Store keys in `hd_keys` table
- Clear `seed` from memory

### Signing with Keys / Addresses

To compartmentalize the risk of a compromised application or system, we want to avoid loading `seed` or `entropy` into memory. Instead, when we need to sign, only a single key is loaded into memory. This key is used to sign the transaction and we should attempt to clear it from memory as soon as possible.

- Load & Decrypt `encrypted_private_key` from `hd_keys` table
- Sign
- Clear

## Encryption

- Use AES256-GCM for encryption / decryption
- Use a symmetric key generated from the device's secure Enclave / HSM

```mermaid
C4Context
    Boundary(b0, "Android", "") {

        Boundary(b1, "Pera Wallet", "Application") {
            ContainerDb(ns, "EncryptedKeyStorage", "Persistence")
            Component(km, "Key Management", "In-Memory/Run-time")
        }

        Boundary(b2, "Secure Storage", "") {
            Container(se, "Secure Enclave", "Service")
            Component(hsm, "Hardware Security Module", "Service")
        }

        Rel(se, hsm, "Encrypt / Decrypt", "AES256-GCM")
        Rel(km, se, "use", "AndroidKeyStore")
        BiRel(ns, km, "write/read", "dbConnection")
    }

    UpdateRelStyle(se, hsm, $textColor="green", $lineColor="blue", $offsetX="-55", $offsetY="50")
    UpdateRelStyle(km, se, $textColor="green", $lineColor="blue", $offsetX="-55", $offsetY="-40")
    UpdateRelStyle(ns, km, $textColor="green", $lineColor="blue", $offsetX="-35", $offsetY="-40")

    
    UpdateElementStyle(se, $fontColor="white", $bgColor="blue", $borderColor="blue")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```