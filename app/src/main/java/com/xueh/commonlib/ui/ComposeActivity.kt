package com.xueh.commonlib.ui

//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ToastUtils
import com.xueh.comm_core.base.compose.MVVMComposeActivity
import com.xueh.comm_core.base.mvvm.BaseViewModel
import com.xueh.comm_core.weight.compose.ComposeTitleView
import com.xueh.commonlib.R
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel
import com.xueh.commonlib.ui.viewmodel.HomeViewModel

class ComposeActivity : MVVMComposeActivity<ComposeViewModel>() {

    override fun getTitleText() = "我是标题"


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }

    override fun initData() {
        viewModel.loadDsl()
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
//        val bannerDatas by viewModel.bannerLiveData.observeAsState()

        contentRoot {
            LazyColumn() {
                items(viewModel.bannerMutableState.size) { index ->
                    itemView(viewModel.bannerMutableState[index].title) {
                        ToastUtils.showShort("点击了 ${index}")
                    }
                }
            }
        }
    }

    @Composable
    private fun itemView(item: String, clickEvent: () -> Unit) {
        Surface(
//            shape = RoundedCornerShape(10.dp),
//            shadowElevation = 5.dp,
            modifier = Modifier.padding(all = 8.dp)
        ){
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Blue)
                    .clickable(onClick = clickEvent)
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.5.dp, MaterialTheme.colors.secondary, shape = CircleShape),

                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color=Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }

    }

}

