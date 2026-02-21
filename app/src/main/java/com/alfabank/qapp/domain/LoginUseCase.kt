package com.alfabank.qapp.domain

import com.alfabank.qapp.base.IOUseCase
import com.alfabank.qapp.data.model.LoginInputData

/**
 * Validates login fields
 * Performs login request
 */
class LoginUseCase(private val loginGateway: LoginGateway) :
    IOUseCase<LoginInputData, LoginUseCase.LoginResult> {

    override suspend fun invoke(input: LoginInputData): LoginResult {

        val loginField = ValidationField.Login(input.username)
        val passwordField = ValidationField.Password(input.password)

        return when {
            loginField.validate() != null -> LoginResult.Error(loginField)
            passwordField.validate() != null -> LoginResult.Error(passwordField)
            else -> {
                if (loginGateway.login(input)) {
                    LoginResult.Success
                } else {
                    LoginResult.Error()
                }
            }
        }
    }

    sealed class ValidationField(private val value: String) {
        class Login(value: String) : ValidationField(value)
        class Password(value: String) : ValidationField(value)

        fun validate(): Reason? {
            return when {
                value.isBlank() -> Reason.Empty
                value.length > MAX_FIELD_LENGTH -> Reason.MaxLength
                else -> null
            }
        }

        enum class Reason {
            Empty,
            MaxLength
        }

        companion object {
            private const val MAX_FIELD_LENGTH = 50
        }
    }

    sealed class LoginResult {
        object None : LoginResult()
        class Error(val validationField: ValidationField? = null) : LoginResult()
        object Success : LoginResult()
    }
}