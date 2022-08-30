package com.xueh.comm_core.weight.compose

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asImageBitmap
import com.blankj.utilcode.util.ImageUtils

/**
 * 创 建 人: xueh
 * 创建日期: 2022/8/26
 * 备注：
 */
@Composable
fun Modifier.setBackground(@DrawableRes backIcon: Int)=drawBehind {
    drawImage(
        ImageUtils
            .getBitmap(backIcon)
            .asImageBitmap(),
    )
}