package com.my.githubtestapplication.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.my.githubtestapplication.base.BaseNetworkViewModel
import com.my.githubtestapplication.module.LogDBRepository
import com.my.githubtestapplication.module.LogRepository
import com.my.githubtestapplication.network.api.PostService
import com.my.githubtestapplication.network.makeDisposableSingleObserver
import com.my.githubtestapplication.network.response.CommentResponse
import com.my.githubtestapplication.network.response.Post
import com.my.githubtestapplication.network.response.PostResponse
import com.my.githubtestapplication.receiver.UserAlertReceiver
import com.my.githubtestapplication.receiver.UserAlertReceiver.ACTION_SHOW_SNACKBAR
import com.my.githubtestapplication.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

/**
 * Created by YourName on 2022/07/12.
 */

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val postService : PostService,
    private val logRepository: LogRepository,
    application : Application
) : BaseNetworkViewModel() {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val POST_API = "post_api"
    private val COMMENT_API = "comment_api"
    var endLogoAnimation = false
    private val postList = arrayListOf<Post>()
    private val commentList = arrayListOf<CommentResponse>()
    private var twoPare: Pair<Int?, String?> = Pair(1, null)
    // coroutine scope controller
    private val fetchPostScope = CoroutineScope(Job() + Dispatchers.Default)
    private val fetchCommentScope = CoroutineScope(Job() + Dispatchers.IO)
    private var allFetchScope : Job? = null
    private var launchTime: Long = 0
    private var withContextJob: Job? = null

    private var fetchJob = Job()
    private var fetchContext: CoroutineContext = fetchJob + Dispatchers.Default

    private val failEndMessages = "not complete"
    private val successEndMessage = "complete all step"
    private var _resultText = MutableLiveData<String?>(failEndMessages)
    val resultText: LiveData<String?> get() = _resultText

    override fun getLogName() = SplashViewModel::class.simpleName

    init {
        Log.e(tagName, "init() logRepository: $logRepository")
        Log.e(tagName, "init() postService: $postService")
    }
    fun fetchAllStart() {
        Log.e(tagName, "fetchAllStart() start")
        launchTime = Calendar.getInstance().timeInMillis
        //coroutine scope 내에서 하위 scope로 개별 실행된다.
        fetchJob = Job()
        fetchContext = fetchJob + Dispatchers.Default
        viewModelScope.launch(fetchContext + Dispatchers.IO) {
            launch {
                fetchPosts { fetchPostResult(it) }
            }
            launch {
                fetchComments { fetchCommentResult(it) }
            }
            launch {
                fetchTwoDocs()
            }
        }
    }

//    private fun fetchTwoApi() = viewModelScope.launch {
//        Log.e(tagName, "fetchTwoApi() start")
//        fetchTwoDocs()
//    }

    fun setResultText(text: String) {
        viewModelScope.launch(Dispatchers.Main) {
            this@SplashViewModel._resultText.value = text
        }
    }

    fun setAnimation(newState:Boolean) {
        if (endLogoAnimation != newState) {
            this.endLogoAnimation = newState
        }
        this.checkEndStep()
    }
    private fun asyncResult(number:Int, text:String) {
        Log.e(tagName, "asyncResult() number: $number text: $text")
        twoPare = Pair(number, text)
        this.checkEndStep()
    }

    private fun fetchPostResult(result : ArrayList<Post>) {
        Log.e(tagName, "fetchPostResult() result: ${result.size > 0}")
        this.postList.addAll(result)
        this.checkEndStep()
    }

    private fun fetchCommentResult(result : ArrayList<CommentResponse>) {
        Log.e(tagName, "fetchCommentResult() result: ${result.size > 0}")
        this.commentList.addAll(result)
        this.checkEndStep()
    }

    @Suppress("UNCHECKED_CAST")
    private fun fetchComments(result: (ArrayList<CommentResponse>) -> Unit) {
        Log.e(tagName, "fetchComments() start")
        cancelObserver(COMMENT_API)
        addObserver(COMMENT_API,
            postService.getModule().getComments().makeDisposableSingleObserver(
                onSuccess = {
                    try {
                        val data = (it as ArrayList<CommentResponse>)
                        data.let {
                            Log.e(tagName, "fetchComments : ${data[0].id}")
                            result(data)
                        }
                    } catch (ex: Exception) {

                    }
                },
                onError = {

                }
            )
        )
    }

    private fun fetchPosts(result: (ArrayList<Post>) -> Unit) {
        Log.e(tagName, "fetchPosts() start")
        cancelObserver(POST_API)
        addObserver(POST_API,
            postService.getModule().getPosts().makeDisposableSingleObserver(
                onSuccess = {
                    val data = (it as PostResponse).data
                    data.let {
                        Log.e(tagName, "fetchPosts : ${data[0].id}")
                        result(data)
                    }
                },
                onError = {

                }
            )
        )
    }

    private suspend fun fetchDoc1(): Int {
        delay(1000)
        return 2
    }

    private suspend fun fetchDoc2(): String {
        delay(2000)

        return "a"
    }

    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchTwoDocs() = viewModelScope.launch(fetchContext + Dispatchers.IO) {
        Log.e(tagName, "fetchTwoDocs() start")
        val deferreds = listOf( async { fetchDoc1() }, async { fetchDoc2() })
        deferreds.awaitAll()
        asyncResult(deferreds[0].await() as Int, deferreds[1].await() as String)
//        개별 호출
//        val fetch1 = async { fetchDoc1() }
//        val fetch2 = async { fetchDoc2() }
//
//        asyncResult(fetch1.await(), fetch2.await())
    }

    private fun checkEndStep(complete : () -> Unit = ::endStep, fail : () -> Unit = ::notEndStep) {
        viewModelScope.launch(fetchContext + Dispatchers.Default) {
            if (
                this@SplashViewModel.endLogoAnimation &&
                this@SplashViewModel.postList.size > 0 &&
                this@SplashViewModel.commentList.size > 0 &&
                (this@SplashViewModel.twoPare.first ?: 1) > 1 &&
                this@SplashViewModel.twoPare.second?.isNotEmpty() == true
            ) {
                complete()
            } else {
                fail()
            }
        }
    }

    private fun endStep() {
        fetchJob.cancel()
        Log.e(tagName, "endStep() endTime ${Calendar.getInstance().timeInMillis - launchTime}")
        setResultText(successEndMessage)
        UserAlertReceiver.sendAction(context, successEndMessage, ACTION_SHOW_SNACKBAR)
    }

    private fun notEndStep() {
        setResultText(failEndMessages)
        UserAlertReceiver.sendAction(context, failEndMessages)
    }

    suspend fun testWithContext(setResult : (String) -> Unit) {
        withContextJob = viewModelScope.launch(Dispatchers.IO) {
            for(i in 0..10) {
                delay(1_000)
                Log.e(tagName, "testWithContext() outter value i :$i")
//                val aaa = withContext(Dispatchers.Main) {
//                    for(j in 0..10) {
//                        delay(100)
//                        setResult(j.toString())
//                    }
//                    return@withContext "111"
//                }
//                Log.e(tagName, "testWithContext() withContext value aaa :$aaa")
                launch {
                    for(j in 0..10) {
                        delay(100)
                        Log.e(tagName, "testWithContext() innner value j :$j")
                        setResult(j.toString())
                    }
                }
            }
        }

    }

    fun stopContextJob() {
        withContextJob?.cancel()
    }

    override fun onCleared() {
        allFetchScope?.cancel()
        fetchPostScope.cancel()
        fetchCommentScope.cancel()
        Log.e(tagName, "onCleared()")
        super.onCleared()
    }
}