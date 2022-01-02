package com.kotlinbyte.scoped_state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface ScopedStateFlow<SCOPE> :
    StateFlow<ScopedState<TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>>


class MutableScopedStateFlow<SCOPE> private constructor(
    private val stateFlow: MutableStateFlow<ScopedState<TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>>
) :
    ScopedStateFlow<SCOPE>,
    MutableStateFlow<ScopedState<TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>> by stateFlow {

    suspend fun emit(state: StateWatcher.BaseState) {
        emit(ScopedState.fromScope(state))
    }

    suspend fun emit(
        scope: TypeMatcher<SCOPE, SCOPE>,
        state: StateWatcher.BaseState
    ) {
        emit(ScopedState.fromBoth(scope, state))
    }

    suspend inline fun <reified S : SCOPE> emit() {
        emit(ScopedState.fromScope<SCOPE, S>())
    }

    @JvmName("emitScopedState")
    suspend inline fun <reified S : SCOPE> emit(state: StateWatcher.BaseState) {
        emit(TypeMatcher.create<SCOPE, S>(), state)
    }

    override suspend fun emit(value: ScopedState<TypeMatcher<SCOPE, SCOPE>, StateWatcher.BaseState>) {
        stateFlow.emit(value)
    }

    companion object {
        fun <SCOPE, S : SCOPE> create(
            scope: Class<S>
        ) = MutableScopedStateFlow<SCOPE>(
            MutableStateFlow(ScopedState.Scope(TypeMatcher.create(scope)))
        )

        fun <SCOPE, S : SCOPE> create(
            scope: Class<S>,
            state: StateWatcher.BaseState
        ) = MutableScopedStateFlow<SCOPE>(
            MutableStateFlow(ScopedState.Both(TypeMatcher.create(scope), state))
        )

        inline fun <SCOPE, reified S : SCOPE> create() = create<SCOPE, S>(S::class.java)

        inline fun <SCOPE, reified S : SCOPE> create(e: StateWatcher.BaseState) =
            create<SCOPE, S>(S::class.java, e)
    }

    inline fun <reified S : SCOPE, STATE: StateWatcher.BaseState> withScope(noinline block: MutableScopedStateFlow<SCOPE>.WithScope<SCOPE, STATE>.() -> Unit) {
        WithScope<SCOPE, STATE>(TypeMatcher.create<SCOPE, S>()).block()
    }

    inner class WithScope<out S : SCOPE, STATE: StateWatcher.BaseState>(private val typeMatcher: TypeMatcher<SCOPE, S>) {
        suspend fun emit(
            state: STATE
        ) {
            emit(typeMatcher, state)
        }
    }
}