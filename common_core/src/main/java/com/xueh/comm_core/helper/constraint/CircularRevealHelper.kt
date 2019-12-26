//package com.sunlands.comm_core.helper.constraint
//
//import android.content.Context
//import android.os.Build
//import android.support.constraint.ConstraintHelper
//import android.support.constraint.ConstraintLayout
//import android.util.AttributeSet
//import android.view.ViewAnimationUtils
//import androidx.constraintlayout.widget.ConstraintHelper
//import androidx.constraintlayout.widget.ConstraintLayout
//
///**
// * 创 建 人: xueh
// * 创建日期: 2019/7/12 16:13
// * 备注：
// */
//class CircularRevealHelper @JvmOverloads constructor(
//        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : ConstraintHelper(context, attrs, defStyleAttr) {
//
//    override fun updatePostLayout(container: ConstraintLayout) {
//        super.updatePostLayout(container)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val views = getViews(container)
//            for (view in views) {
//                val anim = ViewAnimationUtils.createCircularReveal(view, view.width / 2,
//                        view.height / 2, 0f,
//                        Math.hypot((view.height / 2).toDouble(), (view.width / 2).toDouble()).toFloat())
//                anim.duration = 800
//                anim.start()
//            }
//        }
//    }
//}