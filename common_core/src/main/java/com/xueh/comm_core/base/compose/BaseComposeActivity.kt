package com.xueh.comm_core.base.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.IBaseLogic
import com.xueh.comm_core.base.compose.theme.BaseComposeView
import com.xueh.comm_core.base.compose.theme.GrayAppAdapter
import com.xueh.comm_core.base.compose.theme.appThemeState
import com.xueh.comm_core.utils.compose.*
import com.xueh.comm_core.weight.compose.CommonTitleView

//ComponentActivity
//AppCompatActivity  可以解决弹窗问题
abstract class BaseComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
    }

    abstract fun initView(savedInstanceState: Bundle?)

    protected open fun getTitleText() = ""

    @Composable
    protected open fun baseContentRoot(
        titleRightView: (@Composable () -> Unit)? = null,
        content: @Composable () -> Unit,
    ) {
        BaseComposeView {
            transparentStatusBar()
            setSystemBarsColor(color = MaterialTheme.colorScheme.onPrimaryContainer,
                darkIcons = appThemeState.value.darkTheme)
            GrayAppAdapter {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (!getTitleText().isEmpty()) {
                        CommonTitleView(getTitleText(), titleRightView = titleRightView) {
                            this@BaseComposeActivity.finish()
                        }
                        Divider(color = Color.Gray, thickness = 0.5.dp)
                    }
                    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier
                        .fillMaxSize()) {
                        content.invoke()
                    }
                }
            }

        }
    }


}





