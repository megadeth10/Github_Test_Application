package com.my.githubtestapplication.scene

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.my.githubtestapplication.R
import org.hamcrest.Matchers.allOf

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by YourName on 2022/06/21.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>
    @Before
    fun setUp() {
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.moveToState(Lifecycle.State.RESUMED)
    }

    @After
    fun tearDown() {
        activityScenario.close()
    }

    @Test
    fun testLoginStep() {
        val correctId = "111111111"
        val correctPw = "12341234!A"
        val successText = "success!"

        val wrongId = "11111111111"
        val wrongPw = "12341234!A11"
        val failText = "fail!"

        onView(withId(R.id.et_id)).check(matches(isDisplayed()))
        onView(withId(R.id.et_pw)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))

        onView(withId(R.id.et_id)).perform(typeText(wrongId))
        onView(withId(R.id.et_pw)).perform(typeText(wrongPw))
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.tv_result)).check(matches(allOf(isDisplayed(), withText(failText))))
    }
}