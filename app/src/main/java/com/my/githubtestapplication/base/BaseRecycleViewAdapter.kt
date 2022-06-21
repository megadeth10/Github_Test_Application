package com.my.githubtestapplication.base

import androidx.recyclerview.widget.RecyclerView
import com.my.githubtestapplication.callback.ILogCallback
import com.my.githubtestapplication.util.Log

abstract class BaseRecycleViewAdapter<VH: RecyclerView.ViewHolder, V>:
    RecyclerView.Adapter<VH>(), ILogCallback {
    protected var tag = BaseRecycleViewAdapter::class.simpleName
    protected var list = ArrayList<V>(0)

    init {
        this.tag = this.getLogName()
    }

    /**
     * 전체 리스트 갱신
     */
    open fun setAll(list: ArrayList<V>){
        this.list = list.clone() as ArrayList<V>
        notifyDataSetChanged()
    }

    /**
     * 특정 위치에서 데이터를 추가함
     */
    open fun addAll(index : Int = 0, list: ArrayList<V>){
        this.list.addAll(index, list)
        notifyItemRangeInserted(index, list.size)
    }

    /**
     * 부분 데이터변경
     */
    open fun setData(index : Int = 0, data: V){
        this.list.set(index, data)
        notifyItemChanged(index)
    }

    fun getAll() : ArrayList<V> = list

    override fun getItemCount() = this.list.size

    /**
     * for Unique stableIds
     */
    override fun getItemId(position: Int): Long {
        return try {
            getAll()[position].hashCode().toLong()
        } catch (e : Exception) {
            super.getItemId(position)
        }
    }

    fun setAllNotifyOffset(list: ArrayList<V>, firstIndex: Int, lastIndex: Int) {
        this.list = list.clone() as ArrayList<V>

        /**
         * ref: https://sodp2.tistory.com/16
         * java.lang.IndexOutOfBoundsException: Inconsistency detected.
         * 발생으로 Last index와 list 길이를 비교하여 다르면 전체 refresh로 예외 추가함.
         */
        if (this.list.size > lastIndex && firstIndex >= 0 && lastIndex >= 0) {
            var firstOffset = firstIndex - 1
            if (firstOffset < 0) {
                firstOffset = 0
            }
            var itemCount = lastIndex + 1
            if (this.list.size < itemCount) {
                itemCount = this.list.size - 1
            }
            Log.e(tag, "setAllNotifyOffset() firstOffset: $firstOffset lastIndex: $lastIndex itemCount: $itemCount size: ${this.list.size}")
            this.notifyItemRangeChanged(firstOffset, itemCount)
        } else {
            Log.e(tag, "setAllNotifyOffset() notifyDataSetChanged()")
            this.notifyDataSetChanged()
        }
    }
}