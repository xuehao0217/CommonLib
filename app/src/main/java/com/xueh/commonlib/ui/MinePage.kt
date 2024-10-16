package com.xueh.commonlib.ui

import android.view.MenuItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.ActivityUtils
import com.lt.compose_views.compose_pager.ComposePagerScope
import com.lt.compose_views.nav.NavContent
import com.xueh.commonlib.ui.compose.RouteConfig
import com.xueh.commonlib.ui.compose.itemView
import com.xueh.commonlib.ui.xml.MainActivity

class MinePage : NavContent {
    override val route: String="MinePage"
    @Composable
    override fun Content(scope: ComposePagerScope) {
        Column(Modifier.statusBarsPadding()){
            itemView("XML", false){
                ActivityUtils.startActivity(MainActivity::class.java)
            }
        }

    }
}