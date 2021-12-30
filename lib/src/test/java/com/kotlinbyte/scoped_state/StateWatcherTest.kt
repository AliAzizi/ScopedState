package com.kotlinbyte.scoped_state

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import kotlinx.coroutines.flow.StateFlow
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StateWatcherTest {

    data class Response<T>(val data: T)
    data class FakeNetworkResponseModel<T>(val status: Int, val response: Response<T>)

    sealed class FakeState : StateWatcher.BaseState {
        object Error : FakeState()
        object Success : FakeState()
        data class SuccessWithResult<T>(val data: FakeNetworkResponseModel<T>) : FakeState()
    }

    sealed class SplashScreenFakeScope {
        object CheckAppVersion : SplashScreenFakeScope()
        object Authorize : SplashScreenFakeScope()
    }

    @Test
    fun `Generics matching should return false`() {
        val typeMatcher = StateWatcher.TypeMatcher
            .create<FakeState, FakeState.Error>(FakeState.Error::class.java)

        assert(!typeMatcher.matches(FakeState.Success))
    }

    @Test
    fun `Generics matching should return true`() {
        val typeMatcher = StateWatcher.TypeMatcher
            .create<FakeState, FakeState.Error>(FakeState.Error::class.java)

        assert(typeMatcher.matches(FakeState.Error))
    }

    @Test
    fun `Generics should not be equal`() {
        val typeMatcher1 = StateWatcher.TypeMatcher.create<FakeState, FakeState.Success>()
        val typeMatcher2 = StateWatcher.TypeMatcher.create<FakeState, FakeState.Error>()

        assert(typeMatcher1 != typeMatcher2)
    }

    @Test
    fun `Generics should be equal`() {
        val typeMatcher1 = StateWatcher.TypeMatcher.create<FakeState, FakeState.Success>()
        val typeMatcher2 = StateWatcher.TypeMatcher.create<FakeState, FakeState.Success>()

        assert(typeMatcher1 == typeMatcher2)
    }

    @Test
    fun `Scope state matcher should find specified state`() {
        val fakeScope = StateWatcher.ScopeBuilder.Scope<FakeState>()

        fakeScope.state<FakeState.Error> {
            //Nothing
        }

        assert(fakeScope.stateDefinitions.filter { it.key.matches(FakeState.Error) }.isNotEmpty())
    }

    @Test
    fun `Scope state matcher should call specified state`() {
        val fakeScope = StateWatcher.ScopeBuilder.Scope<FakeState>()
        var called = false

        fakeScope.state<FakeState.Error> {
            //Nothing
        }

        fakeScope.state<FakeState.Success> {
            called = true
        }

        fakeScope.stateDefinitions
            .filter { it.key.matches(FakeState.Success) }
            .firstNotNullOf { it.value }(FakeState.Success)

        assert(called)
    }

    @Test
    fun `Scope state matcher should call specified state with same value`() {
        val fakeScope = StateWatcher.ScopeBuilder.Scope<FakeState>()

        val fakeResponse = Response("Hello android i am from web")
        val fakeNetWorkModel = FakeNetworkResponseModel(200, fakeResponse)
        var isEqual = false

        fakeScope.state<FakeState.Error> {
            //Nothing
        }

        fakeScope.state<FakeState.SuccessWithResult<String>> {
            isEqual =
                (it.data.status == fakeNetWorkModel.status) && (it.data.response.data == it.data.response.data)
        }

        fakeScope.stateDefinitions
            .filter { it.key.matches(FakeState.SuccessWithResult(fakeNetWorkModel)) }
            .firstNotNullOf { it.value }(FakeState.SuccessWithResult(fakeNetWorkModel))

        assert(isEqual)
    }


    @Test
    fun `Scope state matcher trigger() should call specified state with same value`() {
        val fakeScope = StateWatcher.ScopeBuilder.Scope<FakeState>()

        val fakeResponse = Response("Hello android i am from web")
        val fakeNetWorkModel = FakeNetworkResponseModel(200, fakeResponse)
        var isEqual = false

        fakeScope.state<FakeState.Error> {
            //Nothing
        }

        fakeScope.state<FakeState.SuccessWithResult<String>> {
            isEqual =
                (it.data.status == fakeNetWorkModel.status) && (it.data.response.data == it.data.response.data)
        }

        fakeScope.triggerState(FakeState.SuccessWithResult(fakeNetWorkModel))

        assert(isEqual)
    }

    @Test
    fun `Scope builder should match specified scope`() {
        val flow: StateFlow<ScopedState<StateWatcher.TypeMatcher<SplashScreenFakeScope, SplashScreenFakeScope>, FakeState>> =
            mockk()
        val scopeBuilder = StateWatcher.ScopeBuilder(flow)

        scopeBuilder.scope<SplashScreenFakeScope.CheckAppVersion, FakeState> {

        }

        assert(
            scopeBuilder.scopeDefinitions.filter {
                it.key.matches(SplashScreenFakeScope.CheckAppVersion)
            }.isNotEmpty() && scopeBuilder.scopeDefinitions.filter {
                it.key.matches(SplashScreenFakeScope.Authorize)
            }.isEmpty()
        )
    }

    @Test
    fun `ScopeBuilder should change current scope correctly`() {
        val flow: StateFlow<ScopedState<StateWatcher.TypeMatcher<SplashScreenFakeScope, SplashScreenFakeScope>, FakeState>> =
            mockk()
        val scopeBuilder = StateWatcher.ScopeBuilder( flow)

        scopeBuilder.scope<SplashScreenFakeScope.CheckAppVersion, FakeState> {

        }

        scopeBuilder.scope<SplashScreenFakeScope.Authorize, FakeState> {

        }

        scopeBuilder.trigger<SplashScreenFakeScope.Authorize>()

        assert(
            scopeBuilder.currentScope != null && scopeBuilder.currentScope!!.matches(
                SplashScreenFakeScope.Authorize
            )
        )

    }


    @Test
    fun `ScopeBuilder should call specified scope and state`() {
        val flow: StateFlow<ScopedState<StateWatcher.TypeMatcher<SplashScreenFakeScope, SplashScreenFakeScope>, FakeState>> =
            mockk()
        val scopeBuilder = StateWatcher.ScopeBuilder( flow)
        var isCalled = false

        scopeBuilder.scope<SplashScreenFakeScope.CheckAppVersion, FakeState> {
            state<FakeState.Error> {
                isCalled = false
            }
        }

        scopeBuilder.scope<SplashScreenFakeScope.Authorize, FakeState> {
            state<FakeState.Error> {
                isCalled = true
            }
        }


        scopeBuilder.triggerBoth<SplashScreenFakeScope, SplashScreenFakeScope.Authorize>(
            FakeState.Error
        )


        assert(isCalled)

    }


    @Test
    fun `ScopeBuilder either scope either state should call specified scope and state`() {
        val flow: StateFlow<ScopedState<StateWatcher.TypeMatcher<SplashScreenFakeScope, SplashScreenFakeScope>, FakeState>> =
            mockk()
        val scopeBuilder = StateWatcher.ScopeBuilder( flow)
        var isCalled = false

        scopeBuilder.scope<SplashScreenFakeScope.CheckAppVersion, FakeState> {
            state<FakeState.Error> {
                isCalled = false
            }
        }

        scopeBuilder.scope<SplashScreenFakeScope.Authorize, FakeState> {
            state<FakeState.Error> {
                isCalled = true
            }
        }


        scopeBuilder.triggerBoth<SplashScreenFakeScope, SplashScreenFakeScope.Authorize>(FakeState.Error)


        assert(isCalled)

    }


}