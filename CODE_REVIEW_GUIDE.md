# Code Review Guide: Joint Account Feature

## Quick Overview

**Feature:** Multi-signature (Joint) Account support for Pera Wallet  
**Branch:** `multisig`  
**Scope:** ~150 files changed (110 new, 46 modified)

---

## Architecture Summary

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer (app)                      │
│  Fragments → Screens (Compose) → ViewModels              │
├─────────────────────────────────────────────────────────┤
│                   Domain Layer (app)                     │
│  UseCases (interface + impl) → Processors                │
├─────────────────────────────────────────────────────────┤
│                 Data Layer (common-sdk)                  │
│  Repositories → API/Database → Mappers                   │
└─────────────────────────────────────────────────────────┘
```

---

## Key Directories to Review

| Directory | What's There |
|-----------|--------------|
| `app/.../modules/addaccount/joint/` | Joint account creation, invitation, transaction signing |
| `app/.../modules/accountdetail/jointaccountdetail/` | Joint account detail screen |
| `app/.../core/transaction/` | Transaction signing helpers |
| `common-sdk/.../joint/` | Repository, API, data models |
| `common-sdk/.../inbox/` | Inbox for sign requests |

---

## Critical Files

### 1. Account Creation Flow
```
app/src/main/kotlin/com/algorand/android/modules/addaccount/joint/creation/
├── ui/addaccount/AddJointAccountFragment.kt      # Add participants
├── ui/createaccount/CreateJointAccountFragment.kt # Create account
├── domain/usecase/CreateJointAccountUseCase.kt   # Creation logic
└── mapper/JointAccountSelectionListItemMapper.kt # UI mapping
```

### 2. Transaction Signing
```
app/src/main/kotlin/com/algorand/android/modules/addaccount/joint/transaction/
├── ui/SignJointTransactionFragment.kt
├── viewmodel/SignJointTransactionViewModel.kt
└── domain/usecase/SignAndSubmitJointAccountSignatureUseCase.kt  ⚠️ Critical
```

### 3. Inbox (Sign Requests)
```
common-sdk/src/main/kotlin/com/algorand/wallet/inbox/
├── domain/usecase/GetInboxMessagesUseCase.kt
├── data/repository/InboxRepositoryImpl.kt
└── di/InboxModule.kt
```

### 4. New Account Type
```
app/.../models/AccountIconResource.kt           # Added JOINT type
app/.../modules/accountcore/ui/usecase/*.kt     # Icon/display handling
```

---

## Review Checklist

### Architecture & Patterns
- [ ] UseCases follow `Interface + UseCase` pattern
- [ ] Repositories follow `Interface + Impl` pattern
- [ ] ViewModels use `StateDelegate` / `EventDelegate`
- [ ] DI modules use correct scope (`SingletonComponent` vs `ViewModelComponent`)

### Code Quality
- [ ] No hardcoded strings (use `stringResource()`)
- [ ] No hardcoded colors (use `PeraTheme.colors.*`)
- [ ] Functions under 50 lines
- [ ] Classes marked `internal` where appropriate
- [ ] Imports used instead of fully qualified names

### Compose UI
- [ ] Screens have corresponding Preview files
- [ ] Previews use `@PreviewLightDark`
- [ ] Modifier is first parameter
- [ ] Theme accessed via `PeraTheme.*`

### Error Handling
- [ ] Network errors handled gracefully
- [ ] Loading states shown
- [ ] Error messages user-friendly

### Security
- [ ] No sensitive data logged
- [ ] Deep links validated before processing
- [ ] Transaction data verified before signing

---

## Known Issues (Backend Feedback)

See `BACKEND_FEEDBACK.md` for details:

1. ⚠️ Expired inbox items still listed as pending
2. ⚠️ Missing sign request creation time in API
3. ⚠️ Multiple members on same device - inbox issues
4. ⚠️ Signed transactions may not submit to blockchain

---

## Testing

### Run Tests
```bash
# Unit tests
./gradlew :app:testProdDebugUnitTest

# Specific test class
./gradlew :app:testProdDebugUnitTest --tests "*.CreateJointAccountUseCaseTest"
```

### Test Files Location
```
app/src/test/kotlin/com/algorand/android/modules/addaccount/joint/
```

### QA Scenarios
See `QA_JOINT_ACCOUNT_TESTING.md` (150+ test scenarios)

---

## Quick Commands

```bash
# Compile check
./gradlew :app:compileProdDebugKotlin

# Lint check
./gradlew :app:detektProdDebug
./gradlew :app:ktlintCheck

# All checks
./gradlew :app:detektProdDebug :app:ktlintCheck :app:compileProdDebugKotlin
```

---

## Questions to Consider

1. **Transaction Flow:** Is the multi-sig signing flow clear and secure?
2. **State Management:** Are all edge cases (expired, cancelled, error) handled?
3. **UX:** Is the joint account creation flow intuitive?
4. **Performance:** Any concerns with inbox polling (3.5s interval)?
5. **Offline:** How does the feature behave without network?

---

## Contact

For questions about implementation decisions, refer to:
- `AI_ASSISTED_DEVELOPMENT_STORY.md` - Development approach
- `JOINT_ACCOUNT_HAPPY_PATHS.md` - User journeys
- `.cursorrules` - Coding standards followed
