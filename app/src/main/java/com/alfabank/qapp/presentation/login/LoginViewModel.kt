package com.alfabank.qapp.presentation.login

import androidx.lifecycle.viewModelScope
import com.alfabank.qapp.base.SupportViewModel
import com.alfabank.qapp.base.UiEvent
import com.alfabank.qapp.base.ViewState
import com.alfabank.qapp.data.model.LoginInputData
import com.alfabank.qapp.domain.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Performs login into one of two systems [Net/Spring]
 * Changes network koin-modules
 */
class LoginViewModel(private val loginUseCase: LoginUseCase) :
    SupportViewModel<LoginUiEvent, LoginViewState>() {

    private val loginStateFlow = MutableStateFlow<LoginViewState?>(null)
    override val viewState: Flow<LoginViewState?> = loginStateFlow

    override fun onUiEvent(event: LoginUiEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is LoginUiEvent.Login -> {
                    loginStateFlow.emit(LoginViewState.Loader(true))
                    val result = LoginViewState.Login(loginUseCase(event.inputData))
                    loginStateFlow.emit(result)
                }
            }
        }
    }
}

sealed class LoginViewState : ViewState {
    class Login(val result: LoginUseCase.LoginResult) : LoginViewState()
    class Loader(val showed: Boolean) : LoginViewState()
}

sealed class LoginUiEvent : UiEvent {
    class Login(val inputData: LoginInputData) : LoginUiEvent()
}