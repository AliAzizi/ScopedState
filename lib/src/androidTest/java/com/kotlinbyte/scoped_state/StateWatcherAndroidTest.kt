package com.kotlinbyte.scoped_state

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StateWatcherAndroidTest {


    sealed class FakeState : StateWatcher.BaseState {
        object Error : FakeState()
        object Success : FakeState()
    }

    sealed class SplashScreenFakeSteps {
        object CheckAppVersion : SplashScreenFakeSteps()
        object Authorize : SplashScreenFakeSteps()
    }


    @Before
    fun setup() {


    }

    class SplashFakeFragment(val stateFlow: StateWatcher.ScopeBuilder<SplashScreenFakeSteps>) :
        Fragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            stateFlow.attach(lifecycle)
            super.onCreate(savedInstanceState)
        }
    }

    @Test
    fun shouldNotTriggerStepsAndEventsWhenNotInForeground() {
        val stateFlow =
            MutableScopedStateFlow.create<SplashScreenFakeSteps, SplashScreenFakeSteps.CheckAppVersion>(
                SplashScreenFakeSteps.CheckAppVersion::class.java
            )

        val mockedScopeBuilder: StateWatcher.ScopeBuilder<SplashScreenFakeSteps> =
            spyk(StateWatcher.ScopeBuilder(stateFlow))

        val scenario = launchFragmentInContainer(initialState = Lifecycle.State.INITIALIZED) {
            SplashFakeFragment(mockedScopeBuilder)
        }


        mockedScopeBuilder.scope<SplashScreenFakeSteps.CheckAppVersion, FakeState> {
            state<FakeState.Error> {

            }
        }



        runBlocking {
            launch {
                stateFlow.emitScopedState<SplashScreenFakeSteps.CheckAppVersion>(FakeState.Error)
            }
        }

        verify(exactly = 1) {
            mockedScopeBuilder.triggerEither(ScopedState.fromScope<SplashScreenFakeSteps, SplashScreenFakeSteps.CheckAppVersion>())
        }

    }
}