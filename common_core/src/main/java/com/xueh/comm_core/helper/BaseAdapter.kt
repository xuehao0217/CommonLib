package com.xueh.comm_core.helper;

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 创 建 人: xueh
 * 创建日期: 2020/11/11 18:57
 * 备注：
 */
abstract class BaseAdapter<T>(@LayoutRes layoutResId: Int,data:MutableList<T>?=null) :BaseQuickAdapter<T,BaseViewHolder>(layoutResId,data)

fun RecyclerView.linear():RecyclerView{
    layoutManager= LinearLayoutManager(this.context)
    return this
}
fun <T> RecyclerView.bindData(data:MutableList<T>,@LayoutRes layoutId:Int,bindItem:(holder:BaseViewHolder,item:T)->Unit):RecyclerView{
    adapter=object :BaseAdapter<T>(layoutId,data){
        override fun convert(holder: BaseViewHolder, item: T) {
            bindItem(holder,item)
        }
    }
    return this
}

fun <T> RecyclerView.onItemClick(itemClick:(data:T,view: View, pos: Int)->Unit):RecyclerView{
    adapter?.let {
        (it as BaseAdapter<T>).setOnItemClickListener { adapter, view, position ->
            itemClick(adapter.data[position] as T,view,position)
        }
    }
    return this
}