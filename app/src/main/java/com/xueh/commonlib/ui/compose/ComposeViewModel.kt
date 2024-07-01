package com.xueh.commonlib.ui.compose


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
import com.xueh.comm_core.helper.compose.rememberMutableStateOf
import com.xueh.comm_core.weight.compose.BoxText
import com.xueh.comm_core.weight.compose.CommonPagingPage
import com.xueh.comm_core.weight.compose.click
import com.xueh.commonlib.ui.viewmodel.ComposeViewModel

@Preview
@Composable
fun ComoposeViewModelPage() {
    BaseComposeViewModel<ComposeViewModel> { viewModel ->
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .height(50.dp)
            .click {
                viewModel.loadDsl()
            })

    }
}
