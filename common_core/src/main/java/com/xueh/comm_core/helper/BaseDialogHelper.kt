package com.xueh.comm_core.helper

import android.R
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.annotation.FloatRange
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.ConvertUtils
import java.lang.Exception


abstract class BaseDialogHelper : DialogFragment() {
    private var mGravity = Gravity.CENTER //对话框的位置
    private var mCanceledOnTouchOutside = true //是否触摸外部关闭
    private var mCanceledBack = true //是否返回键关闭
    private var mWidth = -1f //对话框宽度，范围：0-1；1整屏宽
    private var mAnimStyle = 0 //显示动画
    private var isDimEnabled = true
    private var mX = 0
    private var mY = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置 无标题 无边框
        setStyle(STYLE_NO_TITLE, 0)
    }


    override fun onStart() {
        dialog?.let {
            it.setCanceledOnTouchOutside(mCanceledOnTouchOutside)
            it.setCancelable(mCanceledBack)
            setDialogGravity(it) //设置对话框布局
        }
        super.onStart()
    }

    /**
     * 设置对话框底部显示
     *
     * @param dialog
     */
    private fun setDialogGravity(dialog: Dialog) {
        dialog.window?.let {
            it.setBackgroundDrawableResource(R.color.transparent)
            val dm = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(dm) //获取屏幕宽
            if (mWidth != -1f) {
                it.attributes.width = (dm.widthPixels * mWidth).toInt() //宽度按屏幕大小的百分比设置
            }
            val wlp = it.attributes
            wlp.gravity = mGravity
            wlp.x = mX
            wlp.y = ConvertUtils.dp2px(mY.toFloat())

            it.attributes = wlp

            if (mAnimStyle != 0) it.setWindowAnimations(mAnimStyle)
            if (isDimEnabled) it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) else it.clearFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )
        }

    }

    fun show(manager: FragmentManager) {
        show(manager, "")
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.isDestroyed) return
        if (!isAdded) {
            var isShow = true
            val transaction = manager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            isShow = try {
                super.show(transaction, tag)
                true
            } catch (e: Exception) {
                Log.e("BaseDialogHelper", "show dialogfragment出错：" + e.message)
                false
            }
            if (!isShow) {
                val fragmentTransaction = manager.beginTransaction()
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                fragmentTransaction.add(this, tag)
                fragmentTransaction.commitAllowingStateLoss()
                manager.executePendingTransactions()
            }
        }
    }

    fun remove() {
        val ft = requireFragmentManager().beginTransaction()
        ft.remove(this)
        ft.addToBackStack(null)
    }

    /**
     * 设置对话框位置
     * [默认][Gravity.CENTER]
     *
     * @param gravity 位置
     */
    protected fun setGravity(gravity: Int) {
        mGravity = gravity
    }

    /**
     * 设置对话框点击外部关闭
     *
     * @param cancel true允许
     */
    protected fun setCanceledOnTouchOutside(cancel: Boolean) {
        mCanceledOnTouchOutside = cancel
    }

    /**
     * 设置对话框返回键关闭关闭
     *
     * @param cancel true允许
     */
    protected fun setCanceledBack(cancel: Boolean) {
        mCanceledBack = cancel
    }

    /**
     * 设置对话框宽度
     *
     * @param width 0.0 - 1.0
     */
    fun setWidth(@FloatRange(from = 0.0, to = 1.0) width: Float) {
        mWidth = width
    }

    /**
     * 弹出对话框的动画
     *
     * @param animStyle StyleRes
     */
    protected fun setAnimations(animStyle: Int) {
        mAnimStyle = animStyle
    }

    /**
     * 设置背景是否昏暗，默认true
     *
     * @param dimEnabled true昏暗
     */
    protected fun setDimEnabled(dimEnabled: Boolean) {
        isDimEnabled = dimEnabled
    }

    protected fun setX(x: Int) {
        mX = x
    }

    protected fun setY(y: Int) {
        mY = y
    }
}