/**
 * Lottie 动效组件：基于 Airbnb [lottie-compose](https://github.com/airbnb/lottie-android)，
 * 用于在 Compose 中播放 **Bodymovin / Lottie JSON**（`res/raw/` 或 `assets/`）。
 *
 * 设计稿导出见 LottieFiles / After Effects Bodymovin；将 `.json` 放入 `app` 或库的 `raw` / `assets` 后调用 [LottieCompose]。
 */
package com.xueh.comm_core.widget

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * 播放 `res/raw/` 下的 Lottie JSON（文件名不含扩展名即为 `R.raw.xxx`）。
 *
 * @param rawRes `res/raw/anim.json` 对应 `R.raw.anim`
 * @param modifier 布局与点击等修饰
 * @param isPlaying 是否驱动时间轴；`false` 时画面停留在当前进度或由 [restartOnPlay] 控制恢复行为
 * @param restartOnPlay 从暂停恢复为 `true` 时是否从头播放
 * @param iterations 循环次数；无限循环使用 [LottieConstants.IterateForever]
 * @param speed 播放速率，`1f` 为设计默认速度
 * @param reverseOnRepeat 每次重复是否反向播放（乒乓效果）
 * @param contentScale 与 `Image` 类似，默认 [ContentScale.Fit] 在视图框内完整显示
 * @param alignment 在 [modifier] 约束内的对齐方式
 * @param clipToCompositionBounds 是否按合成尺寸裁剪，避免透明区域溢出
 */
@Composable
fun LottieCompose(
    @RawRes rawRes: Int,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = true,
    iterations: Int = LottieConstants.IterateForever,
    speed: Float = 1f,
    reverseOnRepeat: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    clipToCompositionBounds: Boolean = true,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        restartOnPlay = restartOnPlay,
        iterations = iterations,
        speed = speed,
        reverseOnRepeat = reverseOnRepeat,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
        clipToCompositionBounds = clipToCompositionBounds,
    )
}

/**
 * 播放 `assets/` 目录下的 Lottie JSON（相对 `assets` 根路径，例如 `"lottie/splash.json"`）。
 *
 * @param assetName assets 内路径，与 [android.content.res.AssetManager.open] 一致
 * @see LottieCompose 其余形参（播放控制、缩放与对齐）与 [rawRes] 重载一致
 */
@Composable
fun LottieCompose(
    assetName: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = true,
    iterations: Int = LottieConstants.IterateForever,
    speed: Float = 1f,
    reverseOnRepeat: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    clipToCompositionBounds: Boolean = true,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetName))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        restartOnPlay = restartOnPlay,
        iterations = iterations,
        speed = speed,
        reverseOnRepeat = reverseOnRepeat,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
        clipToCompositionBounds = clipToCompositionBounds,
    )
}

/**
 * 在 [rawRes] 版本基础上支持 **裁剪播放区间**（帧或进度），用于只播放 JSON 中的某一段动画。
 *
 * @param clipSpec 使用 [LottieClipSpec.Progress] 或 [LottieClipSpec.Frame] 限定起止；`null` 表示播全长
 */
@Composable
fun LottieCompose(
    @RawRes rawRes: Int,
    clipSpec: LottieClipSpec?,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = true,
    iterations: Int = LottieConstants.IterateForever,
    speed: Float = 1f,
    reverseOnRepeat: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    clipToCompositionBounds: Boolean = true,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        restartOnPlay = restartOnPlay,
        iterations = iterations,
        speed = speed,
        reverseOnRepeat = reverseOnRepeat,
        clipSpec = clipSpec,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = contentScale,
        alignment = alignment,
        clipToCompositionBounds = clipToCompositionBounds,
    )
}
