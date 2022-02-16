package com.xueh.comm_core.helper;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xueh.comm_core.weight.GridItemDecoration
import com.xueh.comm_core.weight.SpacesItemDecoration

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

fun RecyclerView.staggeredGrid(
    spanCount: Int,
    orientation: Int = StaggeredGridLayoutManager.VERTICAL
): RecyclerView {
    layoutManager = StaggeredGridLayoutManager(spanCount, orientation)
    return this
}

/**
 * 直接设置分割线颜色等，不设置drawable
 *
 * @param dividerColor         分割线颜色
 * @param dividerSpacing       分割线间距
 * @param leftTopPaddingDp     如果是横向 - 左边距
 *                             如果是纵向 - 上边距
 * @param rightBottomPaddingDp 如果是横向 - 右边距
 *                             如果是纵向 - 下边距
 */
fun RecyclerView.addLinearItemDecoration(
    dividerColor: Int,
    dividerSpacing: Int,
    leftTopPaddingDp: Float = 0f,
    rightBottomPaddingDp: Float = 0f
): RecyclerView {
    if (layoutManager is LinearLayoutManager) {
        addItemDecoration(
            SpacesItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            ).setParam(dividerColor, dividerSpacing, leftTopPaddingDp, rightBottomPaddingDp)
        )
    }
    return this
}

/**
 * @param edgeMargin   左右边距，单位dp
 * @param dividerWidth 中间间距，单位dp
 */
fun RecyclerView.addGridItemDecoration(edgeMargin: Float, dividerWidth: Float): RecyclerView {
    addItemDecoration(GridItemDecoration(edgeMargin, dividerWidth))
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

/*
//用法
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  super.onViewCreated(view, savedInstanceState)
  adapter.data.addAll(list)
  binding.recyclerView.adapter = adapter
}

private val adapter = baseQuickAdapter<ItemFooBinding, String> { item ->
  textView.text = item
}
 */


/*
        val onBindAdapter = binding.rv
            .grid(4).addGridItemDecoration(15f, 10f)
//            .linear().addLinearItemDecoration(R.color.white, 3, 15f)
            .onBindAdapter<ItemLayoutBinding, String> { item ->
                tvItem.text = item
            }.apply {
                setNewInstance(list)
            }
 */
abstract class BaseBindingQuickAdapter<T, VB : ViewBinding>(layoutResId: Int = -1) :
    BaseQuickAdapter<T, BaseBindingQuickAdapter.BaseBindingHolder>(layoutResId) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int) =
        BaseBindingHolder(ViewBindingUtil.inflateWithGeneric<VB>(this, parent))

    class BaseBindingHolder(private val binding: ViewBinding) : BaseViewHolder(binding.root) {
        constructor(itemView: View) : this(ViewBinding { itemView })

        @Suppress("UNCHECKED_CAST")
        fun <VB : ViewBinding> getViewBinding() = binding as VB
    }
}

inline fun <VB : ViewBinding, T> RecyclerView.onBindAdapter(crossinline convert: VB.(T) -> Unit) =
    object : BaseBindingQuickAdapter<T, VB>() {
        override fun convert(holder: BaseBindingHolder, item: T) {
            convert(holder.getViewBinding(), item)
        }
    }.also {
        adapter = it
    }