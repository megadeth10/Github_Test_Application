package com.my.githubtestapplication.scene

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.my.githubtestapplication.R
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.AllOf.allOf

/**
 * Created by YourName on 2022/07/19.
 */

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SplashActivityTest {
    @get: Rule
    var activityScenarioRule = ActivityScenarioRule(SplashActivity::class.java)

    @Test
    fun checkEndAnimation() {
        activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
        runBlocking {
            println("suspend start")
            resultCheck()
            println("suspend end")
        }

        println("view check")
        onView(withId(R.id.tv_result)).check(matches(allOf(isDisplayed(), withText("complete all step"))))
    }

    private suspend fun resultCheck() {
        delay(5_000)
        println("resultCheck end")
    }
}