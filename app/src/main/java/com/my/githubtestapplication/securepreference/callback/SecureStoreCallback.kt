package com.my.githubtestapplication.securepreference.callback

/**
 * 로컬 store preference 완료 콜백.
 * 주의!!!!!!
 * 사용시 받듯이 호출되는 Coroutines Scope가 Default인점을 상기 시키고 UI 동작이나 ViewModel LiveData 업데이트 시에
 * 적절한 Scope를 설정해 주어야 한다.
 */
interface SecureStoreCallback {
    fun storeFinish()
}