package com.xueh.comm_core.weight

import android.content.Context
import android.util.AttributeSet
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.ResourceUtils
import com.google.android.material.button.MaterialButton
import com.xueh.comm_core.R

class CommonTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialButton(context, attrs, R.style.common_textview_style) {
    init {
        setBackgroundColor(R.color.transparent)
        setRippleColorResource(R.color.transparent)
        insetBottom = 0
        insetTop = 0
    }
}