# Summary of Rules & Changes Across Branches 01, 02, and 03

---

## New Rules Added to `.cursorrules`

### Branch 01

| Rule                               | Description                                                            |
|------------------------------------|------------------------------------------------------------------------|
| **Theme-aware icons**              | Use `@color/text_main` instead of hardcoded hex colors in drawable XML |
| **Format placeholders in strings** | Use `%1$s` for dynamic content to support localization                 |
| **NDK abiFilters in build type**   | Place in debug/release build types, not just defaultConfig             |

### Branch 02

| Rule                                              | Description                                                     |
|---------------------------------------------------|-----------------------------------------------------------------|
| **Split interface and implementation**            | `interface Foo` and `class FooImpl` in separate files           |
| **Interface+impl pattern for mappers**            | `internal interface FooMapper` + `internal class FooMapperImpl` |
| **Update tests when changing structure**          | Update tests to use correct classes after refactoring           |
| **common-sdk data layer is internal**             | API services, mappers, request/response models are `internal`   |
| **common-sdk domain layer is public**             | Repository interfaces and use cases are `public`                |
| **App module must NOT use common-sdk data layer** | Never import data layer classes in app module                   |
| **App module can wrap common-sdk repositories**   | Create wrapper that converts `PeraResult` to `Result`           |

### Branch 03

| Rule                                           | Description                                            |
|------------------------------------------------|--------------------------------------------------------|
| **Data models must be internal**               | All `data/model/` classes must be `internal`           |
| **Domain models no DTO suffix**                | Use `JointAccount`, not `JointAccountDTO`              |
| **Descriptive input/output naming**            | Use `*Input` suffix for input models                   |
| **Avoid unnecessary use case implementations** | Provide via DI lambda if just delegating to repository |
| **When to create use case implementation**     | Only when there's actual business logic                |
| **Mock ALL dependencies in tests**             | Don't test multiple classes together                   |
| **Avoid unnecessary @Before setup**            | Use inline initialization for simple mocks             |
| **Use companion object for test constants**    | Place constants in `private companion object`          |
| **Use .copy() for test variations**            | Use `.copy()` instead of creating new objects          |

---

## Code Changes by Branch

### Branch 01 - Basic Setup & Resources

- Fixed icon colors to use theme-aware colors
- Fixed string resources to use format placeholders
- Fixed NDK abiFilters configuration in build.gradle.kts

### Branch 02 - Architecture & Module Boundaries

- Split mapper interfaces and implementations into separate files
- Made data layer classes `internal` in common-sdk
- Created app module repository wrappers for common-sdk repositories
- Added `PeraResult` to `Result` conversion

### Branch 03 - Domain Models, Use Cases & Tests

#### Domain Model Renames (removed DTO suffix)

| Old Name                                | New Name                       |
|-----------------------------------------|--------------------------------|
| `JointAccountDTO`                       | `JointAccount`                 |
| `CreateJointAccountDTO`                 | `CreateJointAccountInput`      |
| `JointSignRequestDTO`                   | `JointSignRequest`             |
| `ProposeJointSignRequestDTO`            | `CreateSignRequestInput`       |
| `SearchSignRequestsDTO`                 | `SearchSignRequestsInput`      |
| `SignRequestTransactionListResponseDTO` | `AddSignatureInput`            |
| `ParticipantSignatureDTO`               | `ParticipantSignature`         |
| `SignRequestWithFullSignatureDTO`       | `SignRequestWithFullSignature` |

#### Use Case Simplification

| Deleted                               | Replacement               |
|---------------------------------------|---------------------------|
| `AddJointAccountSignatureUseCase`     | DI lambda                 |
| `ProposeJointSignRequestUseCase`      | DI lambda                 |
| `GetSignRequestWithSignaturesUseCase` | Logic moved to repository |

#### Repository Changes

- Added `getSignRequestWithSignatures()` method to `JointAccountRepository`
- Moved mapping logic from use case to `JointAccountRepositoryImpl`

#### Test Improvements

- All test files now use `private companion object` for constants
- All tests use `.copy()` for variations
- `JointSignRequestMapperTest` now mocks `JointAccountDTOMapper`
- Deleted use case tests (logic moved to repository)

#### File Structure (Use Cases)

Split `JointAccountTransactionUseCases.kt` into 3 separate files:

- `AddJointAccountSignature.kt`
- `GetSignRequestWithSignatures.kt`
- `ProposeJointSignRequest.kt`

---

## Commits Summary (Branch 03)

1. Renamed domain models (removed DTO suffix)
2. Split use case interfaces into separate files
3. Simplified use cases by removing unnecessary implementations
4. Added new rules to prevent common PR review issues
5. Fixed test to use companion object for constants
