# Testing Guidelines for Pera Wallet

This document outlines the testing rules and guidelines for Pera Wallet. Following these rules will help ensure that our tests are consistent, easy to understand, and maintainable. Please adhere to these guidelines when writing tests for this project.

## Testing Frameworks and Tools

- **JUnit**: Used for unit testing.
- **Espresso**: Used for UI testing.
- **Netmock**: Used for network testing
- **Mockito**: Used for mocking dependencies.
- **Detekt/Ktlint**: Used for code style checks and linting.

## Package Structure

Tests should mirror the package structure of the application code. For example:

```
app/src/main/java/com/algorand/android/feature
app/src/test/java/com/algorand/android/feature
app/src/androidTest/java/com/algorand/android/feature
```


This helps maintain a clear relationship between the test classes and the classes they test.

## Test Naming Conventions

- **Unit Tests**: Follow the pattern `EXPECT expected result WHEN condition`.
  - `EXPECT account with given id WHEN exists in DB` 
- **Class names**: Follow the pattern `ClassNameTest`.
  - If the class being tested is `AccountRepository`, then test class should be `AccountRepositoryTest`.
- **Variable name**: Use `sut` (System Under Test) as the variable name for the class being tested.
  - `private val sut = AccountRepositoryImpl()`


## Creating Test Arguments

When creating test arguments, use `fixture` to generate test objects, ensuring consistency and reducing redundancy in your test setups.


## Writing Effective Tests

- Write tests that are **independent** and **isolated**.
- Ensure tests are **fast** to run and **reliable**.
- Use **Given**-**When**-**Then** structure for readability:
    - `Given`: the initial context (setup).
    - `When`: the action being tested.
    - `Then`: the expected outcome.


## Mocking and Dependencies

- Use **Mockito** for mocking dependencies.
- Use **Fake** or **Stub** objects when more appropriate than mocks, especially for complex dependencies.
- Avoid using real network calls in tests. Use mock responses to control network behavior.


## Best Practices

- Keep tests **simple** and **focused** on a single functionality.
- Avoid testing implementation details; focus on behavior and outcomes.
- Refactor tests to remove duplication and improve readability.
- Regularly review and update tests to reflect changes in the codebase.

## Example Test

```kotlin

interface AccountRepository {
    fun getAccount(): Account
}

interface AccountApiService {
    fun getAccount(): AccountDto
}

interface AccountMapper {
    fun map(accountDto: AccountDto): Account
}

class AccountRepositoryImplTest {
    
    private val accountApiService: AccountApiService = mock()
    private val accountMapper: AccountMapper = mock()
    
    private val sut = AccountRepositoryImpl(accountApiService, accountMapper)
    
    @Test
    fun `EXPECT account WHEN response is success`() {
        whenever(accountApiService.getAccount()).thenReturn(ACCOUNT_DTO)
        whenever(accountMapper.map(ACCOUNT_DTO)).thenReturn(ACCOUNT)
        
        val result = sut.getAccount()
        
        assertEquals(ACCOUNT, result)
    }
    
    private companion object {
        private val ACCOUNT = fixtureOf<Account>()
        private val ACCOUNT_DTO = fixtureOf<AccountDto>()
    }
}
```

## Contributing

Please ensure that your tests adhere to the guidelines outlined in this document. For any questions or clarifications, feel free to reach out.