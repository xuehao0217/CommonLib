package com.xueh.commonlib.ui.compose

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** AutoSize / 省略号两段演示共用的收窄比例，便于对比「同一物理宽度」。 */
private const val LoginDemoNarrowWidthFraction = 0.62f

/**
 * Compose 1.8+ 能力演示：Autofill（contentType）、TextAutoSize、TextOverflow、animateBounds、登录表单骨架。
 */
@Composable
fun LoginComposeDemoScreen() {
    val scheme = MaterialTheme.colorScheme
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var toast by rememberSaveable { mutableStateOf<String?>(null) }

    val focusEmail = remember { FocusRequester() }
    val focusPassword = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        DemoScreenIntro(
            text = "含 Autofill；TextAutoSize.StepBased 响应容器；TextOverflow 多种省略；animateBounds 动画。均可切换窄/满宽对比。",
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "登录（演示）",
                style = MaterialTheme.typography.titleLarge,
                color = scheme.onSurface,
                modifier = Modifier.padding(top = 8.dp),
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusEmail)
                    .semantics { contentType = ContentType.EmailAddress },
                label = { Text("邮箱") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusPassword)
                    .semantics { contentType = ContentType.Password },
                label = { Text("密码") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
            )

            Button(
                onClick = {
                    toast = if (email.isBlank() || password.isBlank()) {
                        "请填写邮箱与密码（演示）"
                    } else {
                        "已提交（无网络，纯 UI 演示）"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("登录")
            }

            toast?.let { tip ->
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            LoginTextAutoSizeDemoSection()

            Spacer(modifier = Modifier.height(8.dp))
            LoginTextEllipsisDemoSection()

            Spacer(modifier = Modifier.height(8.dp))
            LoginAnimateBoundsDemoSection()
        }
    }
}

/** TextAutoSize.StepBased：在 min～max 字号间按步进取不溢出约束的最大字号（Material3 Text 的 autoSize）。 */
@Composable
private fun LoginTextAutoSizeDemoSection() {
    val scheme = MaterialTheme.colorScheme
    var narrowContainer by rememberSaveable { mutableStateOf(true) }

    val autoSizeHeadline = remember {
        TextAutoSize.StepBased(
            minFontSize = 11.sp,
            maxFontSize = 34.sp,
            stepSize = 1.sp,
        )
    }
    val autoSizeCompare = remember {
        TextAutoSize.StepBased(
            minFontSize = 10.sp,
            maxFontSize = 22.sp,
            stepSize = 0.5.sp,
        )
    }
    val autoSizeMultiline = remember {
        TextAutoSize.StepBased(
            minFontSize = 11.sp,
            maxFontSize = 18.sp,
            stepSize = 0.5.sp,
        )
    }

    val promoLine =
        "618 大促 · 满减叠券 · 会员专享 · 本行在窄宽下自动缩小字号以尽量完整展示"
    val compareLine =
        "同一字符串：左栏固定 18sp 只能省略；右栏 AutoSize 在高度内尽量放大并保持单行"
    val multilineBlurb =
        "多行场景下在限定高度与 maxLines 内逐级尝试更大字号；" +
            "折叠屏或横竖屏切换时同一组件可少写断点逻辑。继续加长文本以观察行数与字号平衡。"

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = scheme.outlineVariant,
    )
    Text(
        text = "AutoSize 文本（TextAutoSize.StepBased）",
        style = MaterialTheme.typography.titleMedium,
        color = scheme.onSurface,
    )
    Text(
        text = "在约束内选最大可读字号；常与 maxLines、固定高度 Box 联用。按钮可切换容器宽度看缩放。",
        style = MaterialTheme.typography.bodySmall,
        color = scheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
    )
    FilledTonalButton(
        onClick = { narrowContainer = !narrowContainer },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            if (narrowContainer) {
                "当前：约 ${(LoginDemoNarrowWidthFraction * 100).toInt()}% 宽 · 点我拉满"
            } else {
                "当前：满宽 · 点我收窄"
            },
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (narrowContainer) LoginDemoNarrowWidthFraction else 1f)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            EllipsisSampleCard(
                label = "1）单行标题 · 固定高度条内自适应",
                scheme = scheme,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = promoLine,
                        style = MaterialTheme.typography.headlineSmall,
                        color = scheme.onSurface,
                        maxLines = 1,
                        autoSize = autoSizeHeadline,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            EllipsisSampleCard(
                label = "2）对比 · 固定字号 + 省略 vs AutoSize",
                scheme = scheme,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    color = scheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = compareLine,
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                                color = scheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    color = scheme.secondaryContainer,
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = compareLine,
                                style = MaterialTheme.typography.titleSmall,
                                color = scheme.onSecondaryContainer,
                                maxLines = 1,
                                autoSize = autoSizeCompare,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "固定 18sp + 省略",
                            style = MaterialTheme.typography.labelSmall,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "AutoSize 单行",
                            style = MaterialTheme.typography.labelSmall,
                            color = scheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            EllipsisSampleCard(
                label = "3）多行 · 固定高度 + maxLines=4 + AutoSize",
                scheme = scheme,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(108.dp),
                ) {
                    Text(
                        text = multilineBlurb,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurface,
                        maxLines = 4,
                        autoSize = autoSizeMultiline,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

/** 演示 Text 在有限宽度下的多种省略策略（Compose 1.8+ 可组合使用）。 */
@Composable
private fun LoginTextEllipsisDemoSection() {
    val scheme = MaterialTheme.colorScheme
    var narrowContainer by rememberSaveable { mutableStateOf(true) }

    val longTitle =
        "超长标题示例 · 副标题 · 规格说明 · 活动标签 · 店铺名 · 所有信息连在一起时的单行展示效果"
    val longBody =
        "这是一段故意写得很长的正文，用来观察多行 maxLines 限制下末尾如何出现省略号。" +
            "继续补充句子长度，以便在较窄容器中更容易触发第二行、第三行截断。" +
            "仍可继续延伸内容，直到超出设定的行数上限。"
    val longPath =
        "/storage/emulated/0/Android/data/com.xueh.commonlib/files/downloads/report-final-v2-edited-copy.pdf"

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = scheme.outlineVariant,
    )
    Text(
        text = "智能省略号（TextOverflow）",
        style = MaterialTheme.typography.titleMedium,
        color = scheme.onSurface,
    )
    Text(
        text = "尾部 / 开头 / 中部适用于单行；多行一般用末尾 Ellipsis。点按钮切换容器宽度观察差异。",
        style = MaterialTheme.typography.bodySmall,
        color = scheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
    )
    FilledTonalButton(
        onClick = { narrowContainer = !narrowContainer },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            if (narrowContainer) {
                "当前：约 ${(LoginDemoNarrowWidthFraction * 100).toInt()}% 宽 · 点我拉满"
            } else {
                "当前：满宽 · 点我收窄"
            },
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (narrowContainer) LoginDemoNarrowWidthFraction else 1f)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            EllipsisSampleCard(
                label = "1）尾部省略（默认）",
                scheme = scheme,
            ) {
                Text(
                    text = longTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            EllipsisSampleCard(
                label = "2）开头省略 · 适合路径/前缀不重要",
                scheme = scheme,
            ) {
                Text(
                    text = longPath,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            EllipsisSampleCard(
                label = "3）中部省略 · 尽量保留头尾",
                scheme = scheme,
            ) {
                Text(
                    text = longPath,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            EllipsisSampleCard(
                label = "4）多行 · maxLines=3 + 末尾省略",
                scheme = scheme,
            ) {
                Text(
                    text = longBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun EllipsisSampleCard(
    label: String,
    scheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = scheme.onSurfaceVariant,
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            shape = RoundedCornerShape(10.dp),
            color = scheme.surfaceContainerLow,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                content()
            }
        }
    }
}

/**
 * [animateBounds] 需在 [LookaheadScope] 内使用；通过改变对齐、子项 [RowScope.weight]、横向占比触发布局变化并做过渡。
 */
@Composable
private fun LoginAnimateBoundsDemoSection() {
    val scheme = MaterialTheme.colorScheme
    var chipAtEnd by rememberSaveable { mutableStateOf(false) }
    var leftHeavy by rememberSaveable { mutableStateOf(true) }
    var wideBar by rememberSaveable { mutableStateOf(true) }

    val springyBounds = remember {
        BoundsTransform { _: Rect, _: Rect ->
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = scheme.outlineVariant,
    )
    Text(
        text = "animateBounds 演示",
        style = MaterialTheme.typography.titleMedium,
        color = scheme.onSurface,
    )
    Text(
        text = "布局在 lookahead 测量下改变边界时，位置与尺寸会插值动画；可搭配 boundsTransform 自定义曲线。",
        style = MaterialTheme.typography.bodySmall,
        color = scheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
    )

    Text(
        text = "1）对齐切换：子项在同一 Box 内从 Start 滑到 End",
        style = MaterialTheme.typography.labelLarge,
        color = scheme.onSurfaceVariant,
    )
    LookaheadScope {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    color = scheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(if (chipAtEnd) Alignment.CenterEnd else Alignment.CenterStart)
                    .animateBounds(lookaheadScope = this@LookaheadScope)
                    .width(108.dp)
                    .height(44.dp)
                    .background(
                        color = scheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .clickable { chipAtEnd = !chipAtEnd },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (chipAtEnd) "靠右" else "靠左",
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.onPrimaryContainer,
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "2）权重切换：同一 Row 内两块按 2:1 与 1:2 互换",
        style = MaterialTheme.typography.labelLarge,
        color = scheme.onSurfaceVariant,
    )
    LookaheadScope {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(if (leftHeavy) 2f else 1f)
                    .fillMaxHeight()
                    .animateBounds(lookaheadScope = this@LookaheadScope)
                    .background(
                        color = scheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (leftHeavy) "左 2×" else "左 1×",
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.onSecondaryContainer,
                )
            }
            Box(
                modifier = Modifier
                    .weight(if (leftHeavy) 1f else 2f)
                    .fillMaxHeight()
                    .animateBounds(lookaheadScope = this@LookaheadScope)
                    .background(
                        color = scheme.tertiaryContainer,
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (leftHeavy) "右 1×" else "右 2×",
                    style = MaterialTheme.typography.labelLarge,
                    color = scheme.onTertiaryContainer,
                )
            }
        }
    }
    FilledTonalButton(
        onClick = { leftHeavy = !leftHeavy },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        Text("交换左右权重（2:1 ↔ 1:2）")
    }

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "3）宽度条 + 弹性 boundsTransform",
        style = MaterialTheme.typography.labelLarge,
        color = scheme.onSurfaceVariant,
    )
    LookaheadScope {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (wideBar) 1f else 0.68f)
                .animateBounds(
                    lookaheadScope = this@LookaheadScope,
                    boundsTransform = springyBounds,
                )
                .height(52.dp)
                .background(
                    color = scheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable { wideBar = !wideBar }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = if (wideBar) "满宽 · 点此收窄（带弹性）" else "约 68% 宽 · 点此拉满",
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSecondaryContainer,
            )
        }
    }
}
