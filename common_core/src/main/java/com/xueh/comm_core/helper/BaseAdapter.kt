package com.xueh.comm_core.helper;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 创 建 人: xueh
 * 创建日期: 2020/11/11 18:57
 * 备注：
 */
abstract class BaseAdapter<T>(@LayoutRes layoutResId: Int, data: MutableList<T>? = null) :
    BaseQuickAdapter<T, BaseViewHolder>(layoutResId, data)

fun RecyclerView.linear(): RecyclerView {
    layoutManager = LinearLayoutManager(this.context)
    return this
}

fun RecyclerView.linearH(): RecyclerView {
    layoutManager =
        LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
    return this
}

fun RecyclerView.grid(spanCount: Int): RecyclerView {
    layoutManager = GridLayoutManager(context, spanCount)
    return this
}

fun <T> RecyclerView.bindData(
    data: MutableList<T>? = null,
    @LayoutRes layoutId: Int,
    bindItem: (holder: BaseViewHolder, item: T) -> Unit
): RecyclerView {
    adapter = object : BaseAdapter<T>(layoutId, data) {
        override fun convert(holder: BaseViewHolder, item: T) {
            bindItem(holder, item)
        }
    }
    return this
}


fun <T> RecyclerView.itemClick(itemClick: (data: T, view: View, pos: Int) -> Unit): RecyclerView {
    adapter?.let {
        (it as BaseAdapter<T>).setOnItemClickListener { adapter, view, position ->
            itemClick(adapter.data[position] as T, view, position)
        }
    }
    return this
}

fun <T> RecyclerView.getAdapter() = adapter as BaseAdapter<T>


//-----------------------------------------ViewBinding--------------------------------------------------------------//
//不使用反射版本
abstract class BaseBindingQuickAdapter<T, VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
    layoutResId: Int = -1
) :
    BaseQuickAdapter<T, BaseBindingQuickAdapter.BaseBindingHolder>(layoutResId) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int) =
        BaseBindingHolder(inflate(LayoutInflater.from(parent.context), parent, false))

    class BaseBindingHolder(private val binding: ViewBinding) : BaseViewHolder(binding.root) {
        constructor(itemView: View) : this(ViewBinding { itemView })

        @Suppress("UNCHECKED_CAST")
        fun <VB : ViewBinding> getViewBinding() = binding as VB
    }
}

/*
//rv.linear().binding(ItemBinding::inflate,list) { vb, s ->
//    vb.tvItem.text=s
//}
 */
fun <T, VB : ViewBinding> RecyclerView.bindingData(
    inflate: (LayoutInflater, ViewGroup, Boolean) -> VB,
    list: MutableList<T>? = null,
    itembind: (VB, T) -> Unit
) : BaseBindingQuickAdapter<T, VB>{
    adapter = object : BaseBindingQuickAdapter<T, VB>(inflate) {
        override fun convert(holder: BaseBindingHolder, item: T) {
            holder.getViewBinding<VB>().apply {
                itembind.invoke(this, item)
            }
        }

    }.apply {
        setNewInstance(list)
    }
    return getBindAdapter()
}

fun <T, VB : ViewBinding> RecyclerView.getBindAdapter() = adapter as BaseBindingQuickAdapter<T, VB>