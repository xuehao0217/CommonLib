package com.xueh.commonlib.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.MVVMComposeActivity
import com.xueh.comm_core.weight.compose.ComposeTitleView
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.HomeViewModel

class ComposeActivity : MVVMComposeActivity<HomeViewModel>() {

    override fun initDataAfterView() {
        viewModel.banner.observe(this) {
            ToastUtils.showShort(it.toString())
        }
    }

    override fun initDataBeforeView() {
        super.initDataBeforeView()
        viewModel.loadDsl()
    }

    override fun getTitleText() = "我是标题"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        contentRoot {
            LazyColumn() {
                items(100) { index ->
                    itemView("HHHHHHHHHHHH") {
                        ToastUtils.showShort("点击了 ${index}")
                    }
                }
            }
        }
    }

    @Composable
    private fun itemView(item: String, clickEvent: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Blue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item,
                modifier = Modifier
                    .clickable(onClick = clickEvent),
                textAlign = TextAlign.Center
            )
        }
    }

}

