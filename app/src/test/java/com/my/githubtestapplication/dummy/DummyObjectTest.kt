package com.my.githubtestapplication.dummy

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by YourName on 2022/06/21.
 */

@RunWith(JUnit4::class)
class DummyObjectTest {

    @Test
    fun testLoginLogic() {
        val correctId = "111111111"
        val correctPw = "12341234!A"
        val wrongId = "11111111111"
        val wrongPw = "12341234!A11"

        val result1 = DummyObject.checkLogIn(correctId, correctPw)
        val result2 = DummyObject.checkLogIn(wrongId, wrongPw)

        assertThat(result1).isFalse()
        assertThat(result2).isFalse()
        System.out.println("correct result: ${result1}")
        System.out.println("wrong result: ${result2}")
    }

}