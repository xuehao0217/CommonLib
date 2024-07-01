package com.xueh.comm_core.helper.xml.constraint

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout


/**
 * 创 建 人: xueh
 * 创建日期: 2019/7/12 16:14
 * 备注：
 */
class ExampleFlyinBounceHelper @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintHelper(context, attrs, defStyleAttr) {
    private var mContainer: ConstraintLayout? = null

    override fun updatePreLayout(container: ConstraintLayout) {
        if (mContainer !== container) {
            val views = getViews(container)
            for (view in views) {
                val animator = ObjectAnimator.ofFloat(view, "translationX", -2000f, 0f).setDuration(800)
                animator.start()
            }
        }
        mContainer = container
    }

}