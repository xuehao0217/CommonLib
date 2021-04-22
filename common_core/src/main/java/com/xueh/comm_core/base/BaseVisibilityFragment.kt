package com.xueh.comm_core.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

/**
 * 创 建 人: xueh
 * 创建日期: 2021/4/22 12:30
 * 备注：
 */

interface OnFragmentVisibilityChangedListener {
    fun onFragmentVisibilityChanged(visible: Boolean)
}


/**
 *
 * 支持以下四种 case
 * 1. 支持 viewPager 嵌套 fragment，主要是通过 setUserVisibleHint 兼容，
 *  FragmentStatePagerAdapter BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 的 case，因为这时候不会调用 setUserVisibleHint 方法，在 onResume check 可以兼容
 * 2. 直接 fragment 直接 add， hide 主要是通过 onHiddenChanged
 * 3. 直接 fragment 直接 replace ，主要是在 onResume 做判断
 * 4. Fragment 里面用 ViewPager， ViewPager 里面有多个 Fragment 的，通过 setOnVisibilityChangedListener 兼容，前提是一级 Fragment 和 二级 Fragment 都必须继承  BaseVisibilityFragment, 且必须用 FragmentPagerAdapter 或者 FragmentStatePagerAdapter
 * 项目当中一级 ViewPager adapter 比较特殊，不是 FragmentPagerAdapter，也不是 FragmentStatePagerAdapter，导致这种方式用不了
 */
open class BaseVisibilityFragment : Fragment(), View.OnAttachStateChangeListener,
    OnFragmentVisibilityChangedListener {


    companion object {
        const val TAG = "BaseVisibilityFragment"
    }

    /**
     * ParentActivity是否可见
     */
    private var parentActivityVisible = false

    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    private var visible = false

    private var localParentFragment: BaseVisibilityFragment? =
        null
    private val listeners = ArrayList<OnFragmentVisibilityChangedListener>()

    fun addOnVisibilityChangedListener(listener: OnFragmentVisibilityChangedListener?) {
        listener?.apply {
            listeners.add(this)
        }
    }

    fun removeOnVisibilityChangedListener(listener: OnFragmentVisibilityChangedListener?) {
        listener?.apply {
            listeners.remove(this)
        }

    }

    override fun onAttach(context: Context) {
        info("onAttach")
        super.onAttach(context)
        val parentFragment = parentFragment
        if (parentFragment != null && parentFragment is BaseVisibilityFragment) {
            this.localParentFragment = parentFragment
            localParentFragment?.addOnVisibilityChangedListener(this)
        }
        checkVisibility(true)
    }

    override fun onDetach() {
        info("onDetach")
        localParentFragment?.removeOnVisibilityChangedListener(this)
        super.onDetach()
        checkVisibility(false)
        localParentFragment = null
    }

    override fun onResume() {
        info("onResume")
        super.onResume()
        onActivityVisibilityChanged(true)
    }


    override fun onPause() {
        info("onPause")
        super.onPause()
        onActivityVisibilityChanged(false)
    }

    /**
     * ParentActivity可见性改变
     */
    protected fun onActivityVisibilityChanged(visible: Boolean) {
        parentActivityVisible = visible
        checkVisibility(visible)
    }

    /**
     * ParentFragment可见性改变
     */
    override fun onFragmentVisibilityChanged(visible: Boolean) {
        checkVisibility(visible)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        info("onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        // 处理直接 replace 的 case
        view.addOnAttachStateChangeListener(this)
    }

    /**
     * 调用 fragment add hide 的时候回调用这个方法
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        checkVisibility(hidden)
    }

    /**
     * Tab切换时会回调此方法。对于没有Tab的页面，[Fragment.getUserVisibleHint]默认为true。
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        info("setUserVisibleHint = $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        checkVisibility(isVisibleToUser)
    }

    override fun onViewAttachedToWindow(v: View?) {
        info("onViewAttachedToWindow")
        checkVisibility(true)
    }

    override fun onViewDetachedFromWindow(v: View) {
        info("onViewDetachedFromWindow")
        v.removeOnAttachStateChangeListener(this)
        checkVisibility(false)
    }

    /**
     * 检查可见性是否变化
     *
     * @param expected 可见性期望的值。只有当前值和expected不同，才需要做判断
     */
    private fun checkVisibility(expected: Boolean) {
        if (expected == visible) return
        val parentVisible =
            if (localParentFragment == null) parentActivityVisible
            else localParentFragment?.isFragmentVisible() ?: false
        val superVisible = super.isVisible()
        val hintVisible = userVisibleHint
        val visible = parentVisible && superVisible && hintVisible
        info(
            String.format(
                "==> checkVisibility = %s  ( parent = %s, super = %s, hint = %s )",
                visible, parentVisible, superVisible, hintVisible
            )
        )
        if (visible != this.visible) {
            this.visible = visible
            onVisibilityChanged(this.visible)
        }
    }

    /**
     * 可见性改变
     */
    protected open fun onVisibilityChanged(visible: Boolean) {
        info("==> onVisibilityChanged = $visible")
        listeners.forEach {
            it.onFragmentVisibilityChanged(visible)
        }
    }

    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    fun isFragmentVisible(): Boolean {
        return visible
    }

    private fun info(s: String) {
        Log.i(TAG, "${this.javaClass.simpleName} ; $s ; this is $this")
    }


}
