package com.xueh.comm_core.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SpacerW(dp: Int) = Spacer(Modifier.width(dp.dp))

@Composable
fun SpacerH(dp: Int) = Spacer(Modifier.height(dp.dp))

@Composable
fun SpacerWidth(width: Dp) = Spacer(Modifier.width(width))

@Composable
fun SpacerHeight(height: Dp) = Spacer(Modifier.height(height))
