package com.my.githubtestapplication.network.api

import com.my.githubtestapplication.network.BaseService
import com.my.githubtestapplication.network.Network
import com.my.githubtestapplication.network.response.CommentResponse
import com.my.githubtestapplication.network.response.PostResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import javax.inject.Inject

/**
 * Created by YourName on 2022/07/12.
 */
interface PostApi {
    @GET("/posts")
    fun getPosts(): Single<PostResponse>

    @GET("/comments")
    fun getComments(): Single<ArrayList<CommentResponse>>
}

class PostService @Inject constructor(network : Network): BaseService<PostApi>(network) {
    override fun getInterface() = PostApi::class.java

}