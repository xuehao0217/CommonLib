package com.xueh.comm_core.helper.xml

import android.os.Bundle
import android.view.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView


abstract class BaseComposeDialogHelper : BaseDialogHelper() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= ComposeView(requireContext()).apply {
        setContent {
            setComposeContent()
        }
    }

    @Composable
    abstract fun setComposeContent()

}