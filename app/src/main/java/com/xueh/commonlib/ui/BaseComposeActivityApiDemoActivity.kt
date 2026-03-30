package com.xueh.commonlib.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.compose.BaseComposeActivity
import com.xueh.comm_core.base.compose.LocalBaseComposeActivity

/**
 * 独立 Activity：用按钮切换 [BaseComposeActivity] 文档中的各项 API，验证边到边、Insets、标题栏与键盘等行为。
 *
 * 从首页「全部示例」→「BaseComposeActivity API 实验室」进入。
 */
class BaseComposeActivityApiDemoActivity : BaseComposeActivity() {

    private var demoImmersiveStatus by mutableStateOf(false)
    private var demoMergeIme by mutableStateOf(true)
    private var demoGray by mutableStateOf(false)
    private var demoDesignWidth by mutableFloatStateOf(402f)
    private var demoShowTitle by mutableStateOf(true)
    private var demoShowBack by mutableStateOf(true)
    private var demoTitle by mutableStateOf("BaseCompose API")
    private var securePending by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            restoreDemoState(savedInstanceState)
        } else {
            securePending = intent.getBooleanExtra(EXTRA_SECURE, false)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveDemoState(outState)
    }

    private fun restoreDemoState(b: Bundle) {
        demoImmersiveStatus = b.getBoolean(KEY_IMM_STATUS, false)
        demoMergeIme = b.getBoolean(KEY_MERGE_IME, true)
        demoGray = b.getBoolean(KEY_GRAY, false)
        demoDesignWidth = b.getFloat(KEY_DESIGN_W, 402f)
        demoShowTitle = b.getBoolean(KEY_SHOW_TITLE, true)
        demoShowBack = b.getBoolean(KEY_SHOW_BACK, true)
        demoTitle = b.getString(KEY_TITLE_STR, demoTitle) ?: demoTitle
        securePending = b.getBoolean(KEY_SECURE, false)
        isSystemBarLight = b.getBoolean(KEY_SYS_BAR_LIGHT, false)
    }

    private fun saveDemoState(outState: Bundle) {
        outState.putBoolean(KEY_IMM_STATUS, demoImmersiveStatus)
        outState.putBoolean(KEY_MERGE_IME, demoMergeIme)
        outState.putBoolean(KEY_GRAY, demoGray)
        outState.putFloat(KEY_DESIGN_W, demoDesignWidth)
        outState.putBoolean(KEY_SHOW_TITLE, demoShowTitle)
        outState.putBoolean(KEY_SHOW_BACK, demoShowBack)
        outState.putString(KEY_TITLE_STR, demoTitle)
        outState.putBoolean(KEY_SECURE, securePending)
        outState.putBoolean(KEY_SYS_BAR_LIGHT, isSystemBarLight)
    }

    override fun immersiveStatusBar(): Boolean = demoImmersiveStatus

    override fun mergeImeIntoContentWindowInsets(): Boolean = demoMergeIme

    override fun isAppGrayMode(): Boolean = demoGray

    override fun designWidthDp(): Float = demoDesignWidth

    override fun isSecureWindow(): Boolean = securePending

    override fun showTitleView(): Boolean = demoShowTitle

    override fun showBackIcon(): Boolean = demoShowBack

    override fun setTitle(): String = demoTitle

    @Composable
    override fun setComposeContent() {
        val activity = this
        val fromLocal = LocalBaseComposeActivity.current

        BackHandler {
            activity.finish()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (demoImmersiveStatus) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0x33E53935)),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .then(
                        if (demoImmersiveStatus) {
                            Modifier.statusBarsPadding()
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "BaseComposeActivity API 实验室",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "以下按钮修改基类 open 方法对应行为；防截屏等需 recreate 的项会保留其它开关（已写入 savedInstanceState）。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(Modifier.padding(vertical = 4.dp))

            StatusBlock(activity, fromLocal)

            HorizontalDivider(Modifier.padding(vertical = 4.dp))

            SectionLabel("沉浸式 / Insets")
            ToggleRow("immersiveStatusBar（内容可画进状态栏，底部始终与导航栏隔离）", demoImmersiveStatus) {
                demoImmersiveStatus = it
            }
            ToggleRow("mergeImeIntoContentWindowInsets（根布局并入键盘）", demoMergeIme) {
                demoMergeIme = it
            }

            SectionLabel("标题栏 API")
            ToggleRow("showTitleView", demoShowTitle) { demoShowTitle = it }
            ToggleRow("showBackIcon", demoShowBack) { demoShowBack = it }

            OutlinedButton(
                onClick = {
                    demoTitle = if (demoTitle == "BaseCompose API") "长标题测试 · 截断一行" else "BaseCompose API"
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("切换 setTitle() 文案")
            }

            SectionLabel("主题 / 灰度 / 设计稿宽度")
            ToggleRow("isAppGrayMode（GrayAppAdapter）", demoGray) { demoGray = it }
            ToggleRow("isSystemBarLight（浅色栏图标）", isSystemBarLight) {
                isSystemBarLight = it
            }

            Text(
                text = "designWidthDp = ${demoDesignWidth.toInt()}（影响全屏 dp 缩放）",
                style = MaterialTheme.typography.bodyMedium,
            )
            RowOfButtons(
                labels = listOf("360", "402", "480"),
                onSelected = { w ->
                    demoDesignWidth = w.toFloat()
                },
            )

            SectionLabel("屏幕方向（Activity.requestedOrientation）")
            Text(
                text = "与 requestedScreenOrientation() 初值独立，此处直接改窗口方向便于目测。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            RowOfButtons(
                labels = listOf("竖屏", "横屏", "跟随传感器"),
                onSelected = { label ->
                    activity.requestedOrientation = when (label) {
                        "横屏" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        "跟随传感器" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
                        else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                },
            )

            SectionLabel("防截屏（isSecureWindow，触发 recreate）")
            Text(
                text = "当前 secure = $securePending。切换后将 recreate。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = {
                    securePending = !securePending
                    activity.recreate()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (securePending) "关闭 FLAG_SECURE（recreate）" else "开启 FLAG_SECURE（recreate）")
            }

            SectionLabel("键盘 / IME（配合 mergeIme 开关）")
            var imeText by rememberSaveable { mutableStateOf("") }
            OutlinedTextField(
                value = imeText,
                onValueChange = { imeText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("点击输入以弹出键盘") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            )

            SectionLabel("视觉提示")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFE53935)),
            )
            Text(
                text = "顶沉浸式开时：浅红层铺满含状态栏（证明基类未顶开）；本列表示例仍加 statusBarsPadding 便于阅读。关顶沉浸式则整页在状态栏下起算。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))
            }
        }
    }

    @Composable
    private fun StatusBlock(activity: BaseComposeActivity, fromLocal: BaseComposeActivity?) {
        val density = LocalDensity.current
        Text("LocalBaseComposeActivity", style = MaterialTheme.typography.titleSmall)
        Text(
            "current = ${fromLocal?.javaClass?.simpleName}（与 this 同一实例：${fromLocal === activity}）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "density.density = ${"%.3f".format(density.density)}（设计稿基准 ${demoDesignWidth.toInt()}dp）",
            style = MaterialTheme.typography.bodySmall,
        )
    }

    @Composable
    private fun SectionLabel(text: String) {
        Spacer(Modifier.height(8.dp))
        Text(text, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
    }

    @Composable
    private fun ToggleRow(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
        RowOfButtons(
            labels = listOf("开", "关"),
            onSelected = { onChecked(it == "开") },
            highlight = if (checked) "开" else "关",
            prefix = label,
        )
    }

    @Composable
    private fun RowOfButtons(
        labels: List<String>,
        onSelected: (String) -> Unit,
        highlight: String? = null,
        prefix: String? = null,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (prefix != null) {
                Text(prefix, style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                labels.forEach { label ->
                    val isOn = highlight == label
                    OutlinedButton(
                        onClick = { onSelected(label) },
                        modifier = Modifier.weight(1f),
                        border = if (isOn) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            ButtonDefaults.outlinedButtonBorder(enabled = true)
                        },
                    ) {
                        Text(label)
                    }
                }
            }
        }
    }

    companion object {
        private const val EXTRA_SECURE = "demo_secure"

        private const val KEY_IMM_STATUS = "demo_imm_status"
        private const val KEY_MERGE_IME = "demo_merge_ime"
        private const val KEY_GRAY = "demo_gray"
        private const val KEY_DESIGN_W = "demo_design_w"
        private const val KEY_SHOW_TITLE = "demo_show_title"
        private const val KEY_SHOW_BACK = "demo_show_back"
        private const val KEY_TITLE_STR = "demo_title_str"
        private const val KEY_SECURE = "demo_secure"
        private const val KEY_SYS_BAR_LIGHT = "demo_sys_bar_light"

        fun start(context: Context) {
            context.startActivity(Intent(context, BaseComposeActivityApiDemoActivity::class.java))
        }

        fun startSecureSample(context: Context) {
            context.startActivity(
                Intent(context, BaseComposeActivityApiDemoActivity::class.java).apply {
                    putExtra(EXTRA_SECURE, true)
                },
            )
        }
    }
}

@Composable
fun BaseComposeActivityApiDemoRoute() {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "在独立 Activity 中测试边到边、沉浸式、IME、标题栏、灰度、设计稿宽度、方向与 FLAG_SECURE 等。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val ctx = LocalContext.current
        Button(
            onClick = { BaseComposeActivityApiDemoActivity.start(ctx) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("打开 API 实验室")
        }
        OutlinedButton(
            onClick = { BaseComposeActivityApiDemoActivity.startSecureSample(ctx) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("打开并默认开启防截屏（EXTRA）")
        }
    }
}
