package com.alfabank.qapp.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Base ViewModel for common operations
 */
abstract class SupportViewModel<E : UiEvent, S : ViewState> : ViewModel(), ViewModelProtocol<E, S> {

    private val sideEffectFilter = ArrayList<OperationResult.Exception.SideEffect>()
    override val sideEffects: List<OperationResult.Exception.SideEffect> = sideEffectFilter

    protected val sideEffectEmitter = MutableStateFlow<OperationResult.Exception.SideEffect?>(null)
    override val sideEffect: Flow<OperationResult.Exception.SideEffect?> = sideEffectEmitter

    override fun registerSideEffects(vararg effects: OperationResult.Exception.SideEffect) {
        sideEffectFilter.addAll(effects)
    }

    override fun onCleared() {
        sideEffectFilter.clear()
        super.onCleared()
    }
}

/**
 * IO ViewModel Protocol
 * Making user interaction by [UiEvent]s
 * Getting updates by [ViewState]s
 */
interface ViewModelProtocol<E : UiEvent, S : ViewState> {

    val sideEffects: List<OperationResult.Exception.SideEffect>
    fun registerSideEffects(vararg effects: OperationResult.Exception.SideEffect)

    val viewState: Flow<S?>
    fun onUiEvent(event: E)

    val sideEffect: Flow<OperationResult.Exception.SideEffect?>
}

/**
 * Describes User interaction events
 */
interface UiEvent

/**
 * Describes ViewModel events
 */
interface ViewState

/**
 * Generic result for any operation for all layers
 *
 * [Exception] is for passing non-business errors from data to presentation
 */
sealed class OperationResult<out S, out F> {
    data class Success<S>(val data: S) : OperationResult<S, Nothing>()
    data class Error<F>(val error: F) : OperationResult<Nothing, F>()
    data class Exception(val sideEffect: SideEffect) : OperationResult<Nothing, Nothing>() {
        interface SideEffect
    }
}