package com.xueh.commonlib.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xueh.comm_core.base.compose.theme.AppThemeColorType
import com.xueh.comm_core.base.compose.theme.AppThemeType
import com.xueh.comm_core.base.compose.theme.appThemeColorType
import com.xueh.comm_core.base.compose.theme.appThemeType
import com.xueh.comm_core.base.compose.theme.blue500
import com.xueh.comm_core.base.compose.theme.green500
import com.xueh.comm_core.base.compose.theme.orange500
import com.xueh.comm_core.base.compose.theme.purple
import androidx.compose.material3.dynamicLightColorScheme
import com.xueh.commonlib.navigation.DemoNavDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage() {
    var paletteMenuExpanded by remember { mutableStateOf(false) }
    val isDarkTheme = AppThemeType.isDark(themeType = appThemeType)
    val scheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = scheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Compose 演示",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "Navigation 3 · CommonLib",
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            appThemeType =
                                if (isDarkTheme) AppThemeType.Light else AppThemeType.Dark
                        },
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) {
                                Icons.Outlined.LightMode
                            } else {
                                Icons.Outlined.DarkMode
                            },
                            contentDescription = "切换浅色/深色",
                            tint = scheme.onSurface,
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { paletteMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = "主题色板",
                                tint = scheme.onSurface,
                            )
                        }
                        DropdownMenu(
                            expanded = paletteMenuExpanded,
                            onDismissRequest = { paletteMenuExpanded = false },
                        ) {
                            PaletteMenuItems(
                                onPick = {
                                    appThemeColorType = it
                                    paletteMenuExpanded = false
                                },
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = scheme.surface,
                    titleContentColor = scheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(scheme.surface),
        ) {
            DemoNavDisplay(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun PaletteMenuItems(onPick: (AppThemeColorType) -> Unit) {
    DropdownMenuItem(
        text = { Text("绿色") },
        onClick = { onPick(AppThemeColorType.GREEN) },
        leadingIcon = {
            PaletteDot(green500)
        },
    )
    DropdownMenuItem(
        text = { Text("紫色") },
        onClick = { onPick(AppThemeColorType.PURPLE) },
        leadingIcon = { PaletteDot(purple) },
    )
    DropdownMenuItem(
        text = { Text("橙色") },
        onClick = { onPick(AppThemeColorType.ORANGE) },
        leadingIcon = { PaletteDot(orange500) },
    )
    DropdownMenuItem(
        text = { Text("蓝色") },
        onClick = { onPick(AppThemeColorType.BLUE) },
        leadingIcon = { PaletteDot(blue500) },
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        DropdownMenuItem(
            text = { Text("动态取色") },
            onClick = { onPick(AppThemeColorType.WALLPAPER) },
            leadingIcon = {
                PaletteDot(dynamicLightColorScheme(LocalContext.current).primary)
            },
        )
    }
}

@Composable
private fun PaletteDot(color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color),
    )
}
