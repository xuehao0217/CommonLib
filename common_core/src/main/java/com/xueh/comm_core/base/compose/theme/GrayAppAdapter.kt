package com.xueh.comm_core.base.compose.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import com.xueh.comm_core.utils.Lunar
import java.util.*

/**
 * 是否需要黑白化应用
 * 如果是南京大屠杀死难者国家公祭日、清明节或中元节的话返回true，反之返回false
 *
 * @return 是否需要黑白化应用
 */
fun isNeedGray(): Boolean {
    val calendar = Calendar.getInstance()
    val month: Int = calendar.get(Calendar.MONTH) + 1
    val day: Int = calendar.get(Calendar.DATE)
    return if (month == 12 && day == 13) {
        true
    } else {
        month == 4 && day == 4 || month == 4 && day == 5 || month == 4 && day == 6
                || Lunar(calendar).isGhostFestival()
    }
}

/**
 * 在内容之上按需叠加 **饱和度为 0** 的全屏层（[BlendMode.Saturation]），实现整页灰阶。
 *
 * **流程**：[Surface] 铺底 → 绘制子 [content] → 若 [isGray] 为 true 则 [Canvas] 盖一层白字混合，全局去色。
 *
 * @param isGray 是否置灰；默认 [isNeedGray]（公祭日/清明/中元等）。
 */
@Composable
fun GrayAppAdapter(isGray: Boolean = isNeedGray(), content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        content()
        if (isGray) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    color = Color.White,
                    blendMode = BlendMode.Saturation
                )
            }
        }
    }
}