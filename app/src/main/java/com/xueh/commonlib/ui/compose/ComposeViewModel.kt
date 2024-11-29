package com.xueh.commonlib.ui.compose


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.xueh.comm_core.base.mvvm.BaseComposeViewModel
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
