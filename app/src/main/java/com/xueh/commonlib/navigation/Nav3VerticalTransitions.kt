package com.xueh.commonlib.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

private const val Nav3VerticalAnimMs = 320

/**
 * 前进（入栈）：新页面从屏幕下方滑入，当前页面向下滑出。
 */
val Nav3VerticalPushTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    slideInVertically(
        animationSpec = tween(Nav3VerticalAnimMs),
        initialOffsetY = { it },
    ) togetherWith slideOutVertically(
        animationSpec = tween(Nav3VerticalAnimMs),
        targetOffsetY = { it },
    )
}

/**
 * 返回（出栈）：当前页面向下滑出，上一页面从屏幕上方滑入。
 */
val Nav3VerticalPopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    slideInVertically(
        animationSpec = tween(Nav3VerticalAnimMs),
        initialOffsetY = { -it },
    ) togetherWith slideOutVertically(
        animationSpec = tween(Nav3VerticalAnimMs),
        targetOffsetY = { it },
    )
}

/**
 * 预测性返回手势：与 [Nav3VerticalPopTransitionSpec] 一致的纵向语义。
 */
val Nav3VerticalPredictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform =
    {
        slideInVertically(
            animationSpec = tween(Nav3VerticalAnimMs),
            initialOffsetY = { full -> -full },
        ) togetherWith slideOutVertically(
            animationSpec = tween(Nav3VerticalAnimMs),
            targetOffsetY = { full -> full },
        )
    }
