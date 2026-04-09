package com.xueh.comm_core.widget.media3

// 仅含「画面 + 进度/剩余时间」的轻量 UI；业务层自管 ExoPlayer 生命周期与 MediaItem。

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.ui.compose.ContentFrame
import androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW
import androidx.media3.ui.compose.material3.indicator.PositionAndDurationText
import androidx.media3.ui.compose.material3.indicator.ProgressSlider
import androidx.media3.ui.compose.material3.indicator.RemainingDurationText

// 底部时间条与渐变边缘留白
private val TimeBarGradientBottomPadding = 12.dp
private val TimeBarGradientTopPadding = 28.dp
private val TimeBarHorizontalPadding = 14.dp

/**
 * 纯播放画面区：[ContentFrame] 铺满 [modifier]；底部叠渐变条，内含进度条与当前/总时长、剩余时长。
 * 不含播放、暂停等控制按钮；[Player] 须由外部 `remember` + `release`。
 *
 * @param player 已 prepare 的 [Player]（一般为 [androidx.media3.exoplayer.ExoPlayer]）。
 * @param modifier 外层尺寸约束，常用 `fillMaxSize()` 或与 `weight(1f)` 配合占满父级。
 * @param surfaceType [androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW] 或 [androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW]。
 * @param contentScale Fit 留黑边 / Crop 裁切铺满等。
 * @param keepContentOnReset 为 true 时 player reset 后仍保留上一帧。
 */
@Composable
fun Media3PlayerContent(
    player: Player,
    modifier: Modifier = Modifier,
    surfaceType: Int = SURFACE_TYPE_SURFACE_VIEW,
    contentScale: ContentScale = ContentScale.Fit,
    keepContentOnReset: Boolean = false,
) {
    val scheme = MaterialTheme.colorScheme

    Box(modifier = modifier) {
        // 底层底色：letterbox / 快门未起时与画面周边的填充
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scheme.surfaceContainerHighest),
        ) {
            // 切换 Surface 类型或缩放策略时强制重组，避免悬挂旧 Surface
            key(surfaceType, contentScale, keepContentOnReset) {
                ContentFrame(
                    player = player,
                    modifier = Modifier.fillMaxSize(),
                    surfaceType = surfaceType,
                    contentScale = contentScale,
                    keepContentOnReset = keepContentOnReset,
                )
            }
        }
        // 自下而上渐隐，保证 Slider / 文字在浅色画面上可读
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.62f),
                        ),
                    ),
                )
                .padding(
                    start = TimeBarHorizontalPadding,
                    end = TimeBarHorizontalPadding,
                    top = TimeBarGradientTopPadding,
                    bottom = TimeBarGradientBottomPadding,
                ),
        ) {
            ProgressSlider(
                player = player,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PositionAndDurationText(
                    player = player,
                    modifier = Modifier.weight(1f),
                )
                RemainingDurationText(player = player)
            }
        }
    }
}
