package com.xueh.comm_core.weight

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.TintTypedArray
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton
import com.xueh.comm_core.R
import com.xueh.comm_core.helper.px

class CommonMaterialButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialButton(context, attrs, R.style.common_textview_style) {
    init {
        insetBottom = 0
        insetTop = 0
        backgroundTintList =
            AppCompatResources.getColorStateList(context, R.color.white)

    }

    fun setOutLineCircleShape(
        cornerRadius: Int,
        strokeWidth: Int,
        @ColorRes strokeColorResourceId: Int
    ) {
        this.cornerRadius = cornerRadius.toFloat().px()
        this.strokeWidth = strokeWidth.toFloat().px()
        setStrokeColorResource(strokeColorResourceId)
    }

    fun setCircleShape(cornerRadius: Int, @ColorRes strokeColorResourceId: Int) {
        this.cornerRadius = cornerRadius.toFloat().px()
        backgroundTintList =
            AppCompatResources.getColorStateList(context, strokeColorResourceId)
    }

}