package com.xueh.comm_core.base.compose

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.LogUtils
import com.google.accompanist.insets.statusBarsHeight
import com.xueh.comm_core.R
import com.xueh.comm_core.weight.compose.CommonTitleView
import com.xueh.comm_core.weight.xml.ViewLoading

//ComponentActivity
//AppCompatActivity  可以解决弹窗问题
abstract class BaseComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisposableEffect(Unit) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { false }, //这里的意思是是否需要检测深色主题模式，我们使用自己的背景，所以不需要直接设置为false,下面也是一样的
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { false },
                )
                onDispose {}
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                if (showTitleView()) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .then(if (showStatusBars()) Modifier.statusBarsPadding() else Modifier)
                    ) {
                        CommonTitleView(
                            title = setTitle(),
                            showBackIcon = showBackIcon(),
                            backClick = {
                                onBackPressedDispatcher.onBackPressed()
                            })
                    }

                }
                setComposeContent()
            }
        }
    }

    @Composable
    abstract fun setComposeContent()

    protected open fun showTitleView() = true

    protected open fun setTitle() = ""

    protected open fun showBackIcon() = true


    protected open fun showStatusBars()=true


    override fun getResources(): Resources {
        val resources = super.getResources()
        val configuration = resources.configuration
        if (configuration.fontScale != 1.0f) {
            LogUtils.e(configuration.fontScale)
            configuration.fontScale = 1.0f
            // 但是这个函数被标记为过期了
            //resources.updateConfiguration(configuration, resources.displayMetrics)
            // 那么我们直接这么来
            return createConfigurationContext(configuration).resources
        }
        return resources
    }
}












