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

fun RecyclerView.linear() = also {
    it.layoutManager = LinearLayoutManager(this.context)
}

fun RecyclerView.linearH() = also {
    it.layoutManager =
        LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
}

fun RecyclerView.grid(spanCount: Int) = also {
    it.layoutManager = GridLayoutManager(context, spanCount)
}

fun RecyclerView.staggeredGrid(
    spanCount: Int,
    orientation: Int = StaggeredGridLayoutManager.VERTICAL
) = also {
    it.layoutManager = StaggeredGridLayoutManager(spanCount, orientation)
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
) = also {
    if (layoutManager is LinearLayoutManager) {
        addItemDecoration(
            SpacesItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            ).setParam(dividerColor, dividerSpacing, leftTopPaddingDp, rightBottomPaddingDp)
        )
    }
}

/**
 * @param edgeMargin   左右边距，单位dp
 * @param dividerWidth 中间间距，单位dp
 */
fun RecyclerView.addGridItemDecoration(edgeMargin: Float, dividerWidth: Float) = also {
    it.addItemDecoration(GridItemDecoration(edgeMargin, dividerWidth))
}


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