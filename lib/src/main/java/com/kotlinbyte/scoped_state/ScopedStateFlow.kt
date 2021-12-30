package com.kotlinbyte.scoped_state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface ScopedStateFlow<SCOPE> :
    StateFlow<ScopedState<StateWatcher.TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>>

class MutableScopedStateFlow<SCOPE> private constructor(
    private val stateFlow: MutableStateFlow<ScopedState<StateWatcher.TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>>
) :
    ScopedStateFlow<SCOPE>,
    MutableStateFlow<ScopedState<StateWatcher.TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>> by stateFlow {

    companion object {
        fun <SCOPE, S : SCOPE> create(
            scope: Class<S>
        ) = MutableScopedStateFlow<SCOPE>(
            MutableStateFlow(ScopedState.Scope(StateWatcher.TypeMatcher.create(scope)))
        )
    }

    suspend inline fun <reified S : SCOPE> emit() {
        emit(ScopedState.fromScope<SCOPE, S>())
    }

    suspend fun emit(e: StateWatcher.BaseState) {
        emit(ScopedState.fromScope(e))
    }


    suspend inline fun <reified S : SCOPE> emitScopedState(state: StateWatcher.BaseState) {
        emitScopedState(StateWatcher.TypeMatcher.create<SCOPE, S>(), state)
    }

    suspend inline fun emitScopedState(
        scope: StateWatcher.TypeMatcher<SCOPE, SCOPE>,
        state: StateWatcher.BaseState
    ) {
        emit(ScopedState.fromBoth(scope, state))
    }



    override suspend fun emit(value: ScopedState<StateWatcher.TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>) {
        stateFlow.emit(value)
    }

}
