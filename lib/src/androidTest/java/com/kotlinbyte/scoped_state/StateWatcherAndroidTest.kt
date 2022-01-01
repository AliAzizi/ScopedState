package com.kotlinbyte.scoped_state

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StateWatcherAndroidTest {


    sealed class FakeState : StateWatcher.BaseState {
        object Error : FakeState()
        object Success : FakeState()
    }

    sealed class SplashScreenFakeScope {
        object CheckAppVersion : SplashScreenFakeScope()
        object Authorize : SplashScreenFakeScope()
    }


    class SplashFakeFragment(val stateFlow: StateWatcher.ScopeBuilder<SplashScreenFakeScope>) :
        Fragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            stateFlow.attach(lifecycle)
            super.onCreate(savedInstanceState)
        }
    }

    private lateinit var stateFlow: MutableScopedStateFlow<SplashScreenFakeScope>
    private lateinit var scenario: FragmentScenario<SplashFakeFragment>
    private lateinit var scopeBuilder: StateWatcher.ScopeBuilder<SplashScreenFakeScope>

    @Before
    fun setup() {
        stateFlow = MutableScopedStateFlow.create(
            SplashScreenFakeScope.CheckAppVersion::class.java
        )

        scopeBuilder = spyk(StateWatcher.ScopeBuilder(stateFlow))

        scenario = launchFragmentInContainer(initialState = Lifecycle.State.INITIALIZED) {
            SplashFakeFragment(scopeBuilder)
        }
    }

    @Test
    fun shouldTriggerStepsAndEventsWhenNotInForeground() {

        scenario.moveToState(Lifecycle.State.STARTED)

        scopeBuilder.scope<SplashScreenFakeScope.CheckAppVersion, FakeState> {
            state<FakeState.Error> {

            }
        }

        runBlocking {
            launch {
                stateFlow.emit<SplashScreenFakeScope.CheckAppVersion>(FakeState.Error)
            }
        }

        verify(exactly = 1) {
            scopeBuilder.triggerEither(ScopedState.fromScope<SplashScreenFakeScope, SplashScreenFakeScope.CheckAppVersion>())
        }

    }


    @Test
    fun shouldNotTriggerStepsAndEventsWhenNotInForeground() {

        scopeBuilder.scope<SplashScreenFakeScope.CheckAppVersion, FakeState> {
            state<FakeState.Error> {

            }
        }

        runBlocking {
            launch {
                stateFlow.emit<SplashScreenFakeScope.CheckAppVersion>(FakeState.Error)
            }
        }

        verify(exactly = 0) {
            scopeBuilder.triggerEither(ScopedState.fromScope<SplashScreenFakeScope, SplashScreenFakeScope.CheckAppVersion>())
        }

    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}