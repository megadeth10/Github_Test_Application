package com.my.githubtestapplication.dummy

/**
 * Created by YourName on 2022/06/21.
 */
object DummyObject {

    fun checkLogIn(id : String, pw : String) : Boolean {
        val userId = "111111111"
        val userPw = "12341234!A"

        return id == userId && pw == userPw
    }
}