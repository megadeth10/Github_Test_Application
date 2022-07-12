package com.my.githubtestapplication.network.response

/**
 * Created by YourName on 2022/07/12.
 */
data class PostResponse(
    val data: ArrayList<Post>
)

data class Post(
    val id: Int,
    val title: String,
    val author: String
)
