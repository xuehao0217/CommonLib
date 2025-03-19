package com.xueh.comm_core.helper

import android.graphics.Color
import android.view.Gravity
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * 创 建 人: xueh
 * 创建日期: 2022/7/20
 * 备注：
 */
fun showToast(context: String, setDurationIsLong: Boolean = false) {
    ThreadUtils.runOnUiThread {
        ToastUtils.make().setBgColor(Color.parseColor("#CC000000")).setTextColor(Color.WHITE)
            .setGravity(Gravity.CENTER, 0,0).setTextSize(16)
            .setDurationIsLong(setDurationIsLong).show(context)
    }
}