package com.my.githubtestapplication.util

import android.animation.*
import android.view.View
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.SingleSubject

object AnimationUtil {
    // animation Time
    const val UI_ANIMATION_EXPAND_TIME = 200L
    /**
     * 오류 알림 애니메이션
     */
    fun warningAnimation(view : View, callback : (() -> Unit)? = null) {
        SingleSubject.just("")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t1, t2 ->
                Warning.stopWarning()
                Warning.animateWarning(view.id, view)
                callback?.invoke()
            }
    }

    class Wobble {
        companion object {
            private var mWobbleAnimators : ArrayList<HashMap<String, ObjectAnimator>> = ArrayList()
            private fun createWobble(v : View, repeatCount : Int) : ObjectAnimator {
                v.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                val animator = ObjectAnimator()
                animator.duration = 180
                animator.repeatMode = ValueAnimator.REVERSE
                animator.repeatCount = repeatCount
                animator.setPropertyName("rotation")
                animator.target = v
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation : Animator?) {
                        v.setLayerType(View.LAYER_TYPE_NONE, null)
                        v.rotation = 0f
                    }
                })
                return animator
            }

            fun animateWobble(animatedId : String, v : View?, repeatCount : Int = ValueAnimator.INFINITE) {
                if (v == null) {
                    return
                }

                SingleSubject.just(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { t1, t2 ->
                        val animator : ObjectAnimator = createWobble(v, repeatCount)
                        animator.setFloatValues(2f, -2f)
                        animator.start()
                        mWobbleAnimators.add(hashMapOf(animatedId to animator))
                    }
            }

            fun stopWobble(animatedId : String? = null) {
                SingleSubject.just(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { t1, t2 ->
                        //해당 아이디 삭제
                        animatedId?.let { id ->
                            mWobbleAnimators.forEach { map ->
                                map[id]?.let {
                                    it.cancel()
                                }
                            }

                            mWobbleAnimators = mWobbleAnimators.filterNot { map ->
                                map[id] != null
                            } as ArrayList<HashMap<String, ObjectAnimator>>
                            return@subscribe
                        }

                        //전체 삭제
                        mWobbleAnimators.forEach { map ->
                            map.values.forEach {
                                it.cancel()
                            }
                        }

                        mWobbleAnimators.clear()
                    }
            }
        }
    }

    class Warning {
        companion object {
            private val mWarningAnimators : HashMap<Int, ObjectAnimator> = HashMap<Int, ObjectAnimator>()
            private fun createWarning(viewId : Int, v : View, range : Array<Float>) : ObjectAnimator {
                v.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                val animatorX = ObjectAnimator().apply { setPropertyName("translationX") }
                animatorX.duration = 100
                animatorX.setFloatValues(*range.toFloatArray())
                animatorX.target = v
                animatorX.repeatCount = 2
                animatorX.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation : Animator?) {
                        v.setLayerType(View.LAYER_TYPE_NONE, null)
                        v.translationX = 0f
                        mWarningAnimators.remove(viewId)
                    }
                })

                return animatorX
            }

            fun animateWarning(viewId : Int, v : View, range : Array<Float> = arrayOf(Util.pxFromDp(v.context, 2f), -1 * Util.pxFromDp(v.context, 2f))) {
                if (mWarningAnimators.containsKey(viewId)) {
                    return
                }
                SingleSubject.just(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { t1, t2 ->
                        val animator : ObjectAnimator = createWarning(viewId, v, range)

                        animator.start()
                        mWarningAnimators.put(viewId, animator)
                    }
            }

            fun stopWarning() {
                SingleSubject.just(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { t1, t2 ->
                        for (warningAnimator in mWarningAnimators.values) {
                            warningAnimator.cancel()
                        }

                        mWarningAnimators.clear()
                    }
            }
        }
    }

    class Expand {
        companion object {
            private val mExpandAnimators : HashMap<Int, AnimatorSet> = HashMap<Int, AnimatorSet>()
            private fun createExpand(viewId : Int, v : View, range : Array<Float>) : AnimatorSet {
                v.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                val animatorX = ObjectAnimator().apply { setPropertyName("scaleX") }
                val animatorY = ObjectAnimator().apply { setPropertyName("scaleY") }

                val arrayAnim = arrayOf(animatorX, animatorY)
                arrayAnim.forEach {
                    it.duration = UI_ANIMATION_EXPAND_TIME
                    it.setFloatValues(*range.toFloatArray())
                    it.target = v
                    it.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation : Animator?) {
                            v.setLayerType(View.LAYER_TYPE_NONE, null)
                            mExpandAnimators.remove(viewId)
                        }
                    })
                }

                return AnimatorSet().apply { playTogether(*arrayAnim) }
            }

            fun animateExpand(viewId : Int?, v : View?, range : Array<Float> = arrayOf(1f, 1.5f, 1f)) {
                if (viewId == null || v == null || mExpandAnimators.containsKey(viewId)) {
                    return
                }

                SingleSubject.just(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { t1, t2 ->
                        val animator : AnimatorSet = createExpand(viewId, v, range)

                        animator.start()
                        mExpandAnimators.put(viewId, animator)
                    }
            }

            fun stopExpand() {
                SingleSubject.just(true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { t1, t2 ->
                        for (wobbleAnimator in mExpandAnimators.values) {
                            wobbleAnimator.cancel()
                        }

                        mExpandAnimators.clear()
                    }
            }
        }
    }

}