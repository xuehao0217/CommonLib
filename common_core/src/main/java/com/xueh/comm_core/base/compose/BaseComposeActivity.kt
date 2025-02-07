package com.xueh.comm_core.base.compose

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.blankj.utilcode.util.LogUtils
import com.xueh.comm_core.R
import com.xueh.comm_core.base.compose.theme.AppBaseTheme
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.ComposeMaterialTheme
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.weight.compose.ImageCompose
import com.xueh.comm_core.weight.compose.click
import com.xueh.comm_core.weight.xml.ViewLoading

//ComponentActivity
//AppCompatActivity  可以解决弹窗问题
abstract class BaseComposeActivity : ComponentActivity() {
    //statusBar图标颜色模式
    //true是白色  false是黑色
    var isSystemBarLight by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback {
            finish()
        }

        setContent {
            // statusBar图标颜色模式
            val isDark = AppThemeType.isDark(
                themeType = appThemeType
            )
            DisposableEffect(isDark, isSystemBarLight) {
                enableEdgeToEdge(
                    SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) {
                        //false statusBar图标颜色模式黑色
                        //true  statusBar图标颜色模式白色
                        isDark || isSystemBarLight
                    },
                    SystemBarStyle.auto(
                        android.graphics.Color.WHITE,
                        android.graphics.Color.BLACK
                    ) { isDark || isSystemBarLight },
                )
                onDispose { }
            }

            ComposeMaterialTheme {
                GrayAppAdapter(isGray = false) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .navigationBarsPadding()
                            .then(if (showStatusBars()) Modifier.statusBarsPadding() else Modifier)
                    ) {
                        if (showTitleView()) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .then(if (showStatusBars()) Modifier.statusBarsPadding() else Modifier)
                            ) {
                                getTitleView()
                            }
                        }
                        setComposeContent()
                    }
                }
            }
        }
    }

    @Composable
    abstract fun setComposeContent()

    protected open fun showTitleView() = true

    protected open fun setTitle() = ""

    protected open fun showBackIcon() = true


    protected open fun showStatusBars() = true

    @Composable
    protected open fun getTitleView() = CommonTitleView(
        title = setTitle(),
        showBackIcon = showBackIcon(),
        backClick = {
            onBackPressedDispatcher.onBackPressed()
        })

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


@Composable
fun CommonTitleView(
    title: String,
    @DrawableRes backIcon: Int = R.mipmap.bar_icon_back_black,
    modifier: Modifier = Modifier,
    titleBackgroundColor: Color = Color.White,
    showBackIcon: Boolean = true,
    rightContent: (@Composable () -> Unit)? = null,
    backClick: (() -> Unit)? = null,
) {
    val isDark = AppThemeType.isDark(themeType = appThemeType)
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(titleBackgroundColor)
            .height(44.dp)
            .then(modifier)
    ) {
        val (iv_close, row_title, surface_right_view) = createRefs()
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = if (isDark) Color.White else Color.Black,
            modifier = Modifier.constrainAs(row_title) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
        if (showBackIcon) {
            ImageCompose(id = backIcon, modifier = Modifier
                .size(28.dp)
                .constrainAs(iv_close) {
                    top.linkTo(row_title.top)
                    bottom.linkTo(row_title.bottom)
                    start.linkTo(parent.start, 16.dp)
                }
                .click {
                    backClick?.invoke()
                })
        }
        Box(modifier = Modifier.constrainAs(surface_right_view) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, 16.dp)
        }) {
            rightContent?.invoke()
        }
    }
}









