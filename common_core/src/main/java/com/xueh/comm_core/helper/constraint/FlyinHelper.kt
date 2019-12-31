package com.sunlands.comm_core.helper.constraint

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF

import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * 创 建 人: xueh
 * 创建日期: 2019/7/12 16:14
 * 备注：
 */
class FlyinHelper @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintHelper(context, attrs, defStyleAttr) {

    override fun updatePreLayout(container: ConstraintLayout?) {
        super.updatePreLayout(container)
    }

    override fun updatePostLayout(container: ConstraintLayout) {
        super.updatePostLayout(container)
        val centerPoint = calculateCenterPoint(container)
        val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(800)
        animator.addUpdateListener { animation ->
            val animatedFraction = animation.animatedFraction
            updateTranslation(centerPoint, animatedFraction, container)
        }
        animator.start()
    }

    private fun updateTranslation(centerPoint: PointF, animatedFraction: Float, container: ConstraintLayout) {

        val views = getViews(container)
        for (view in views) {

            val viewCenterX = (view.left + view.right) / 2
            val viewCenterY = (view.top + view.bottom) / 2


            val startTranslationX = if (viewCenterX < centerPoint.x) -2000f else 2000f
            val startTranslationY = if (viewCenterY < centerPoint.y) -2000f else 2000f


            view.translationX = (1 - animatedFraction) * startTranslationX
            view.translationY = (1 - animatedFraction) * startTranslationY
        }
    }

    private fun calculateCenterPoint(container: ConstraintLayout?): PointF {
        var leftMin: Float = Float.MAX_VALUE
        var topMin = Float.MAX_VALUE
        var rightMax = Float.MIN_VALUE
        var bottomMax = Float.MIN_VALUE


        val views = getViews(container)
        for (view in views) {
            leftMin = if (view.left < leftMin) view.left.toFloat() else leftMin
            topMin = if (view.top < topMin) view.top.toFloat() else topMin
            rightMax = if (view.right > rightMax) view.right.toFloat() else rightMax
            bottomMax = if (view.bottom > bottomMax) view.bottom.toFloat() else bottomMax
        }
        return PointF((rightMax + leftMin) / 2, (bottomMax + topMin) / 2)
    }


}