package com.kotlinbyte.scoped_state

sealed class ScopedState<out L : TypeMatcher<*, *>, out R : StateWatcher.BaseState> {

    data class Scope<out L : TypeMatcher<*, *>>(val a: L) : ScopedState<L, Nothing>()

    data class State<out R : StateWatcher.BaseState>(val b: R) : ScopedState<Nothing, R>()

    data class Both<out L : TypeMatcher<*, *>, out R : StateWatcher.BaseState>(
        val a: L,
        val b: R
    ) : ScopedState<L, R>()

    val isScope get() = this is State<R>

    val isState get() = this is Scope<L>

    val isBoth get() = this is Both<L, R>


    fun fold(fnL: (L) -> Any, fnR: (R) -> Any, fnB: (L, R) -> Any): Any =
        when (this) {
            is Scope -> fnL(a)
            is State -> fnR(b)
            is Both -> fnB(a, b)
        }

    companion object {
        inline fun <SCOPE, reified S : SCOPE> fromScope() =
            Scope(TypeMatcher.create<SCOPE, S>())

        fun <R : StateWatcher.BaseState> fromScope(b: R) = State(b)

        fun <SCOPE, E : StateWatcher.BaseState> fromBoth(
            scope: TypeMatcher<SCOPE, SCOPE>,
            state: E
        ) = Both(scope, state)

        inline fun <SCOPE, reified S : SCOPE> fromBoth(
            state: StateWatcher.BaseState
        ) = fromBoth(TypeMatcher.create<SCOPE, S>(), state)

    }
}