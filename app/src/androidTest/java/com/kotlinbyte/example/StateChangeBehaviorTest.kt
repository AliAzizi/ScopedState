package com.kotlinbyte.example

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.test.ext.junit.rules.activityScenarioRule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
@LargeTest
class StateChangeBehaviorTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<CurrencyActivity>()


    @Test
    fun counter_state_change() {

        for (i in 1..100) {
            if (i % 2 == 0) {

                onView(withId(R.id.increment)).perform(doubleClick())
                onView(withId(R.id.increment2)).perform(doubleClick())

            } else {
                onView(withId(R.id.decrement)).perform(doubleClick())
                onView(withId(R.id.decrement2)).perform(doubleClick())
            }

        }

        onView(withId(R.id.counter))

        onView(withId(R.id.counter)).check(matches(ViewMatchers.withText("0")))
        onView(withId(R.id.counter2)).check(matches(ViewMatchers.withText("0")))
    }


}