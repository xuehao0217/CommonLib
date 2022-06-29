package com.xueh.comm_core.weight

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.*
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.px

/**
实现圆
app:shapeAppearance="@style/CircleStyle"

实现圆角
app:shapeAppearance="@style/RoundedStyle"

变宽颜色和变宽宽度
app:strokeColor="@color/red"
app:strokeWidth="4dp"
 */
class ShapeImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ShapeableImageView(context, attrs) {

    fun setCircleStyle(
        strokeWidth: Float,
        @ColorRes strokeColorResourceId: Int,
        bolck: (ShapeAppearanceModel.Builder) -> Unit
    ) {
        this.strokeWidth = strokeWidth.px().toFloat()
        this.strokeColor = AppCompatResources.getColorStateList(context, strokeColorResourceId)
        shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
            bolck.invoke(this)
//            setTopLeftCorner(RoundedCornerTreatment())
//            setTopLeftCornerSize(80f)
//            setBottomRightCorner(RoundedCornerTreatment())
//            setBottomRightCornerSize(80f)
//            setTopRightCorner(RoundedCornerTreatment())
//            setTopRightCornerSize(80f)
//            setBottomLeftCorner(RoundedCornerTreatment())
//            setBottomLeftCornerSize(80f)
        }.build()
    }


    fun setCornerRadius(corner: Float) {
        shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
            setAllCornerSizes(corner.px().toFloat())
        }.build()
    }
}