package com.xueh.comm_core.widget.media3

// 演示向整体壳子：内部组合 Media3PlayerContent + 片源/倍速/地址/底栏传输；正式业务可只用 Media3PlayerContent。

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.material3.buttons.MuteButton
import androidx.media3.ui.compose.material3.buttons.NextButton
import androidx.media3.ui.compose.material3.buttons.PlayPauseButton
import androidx.media3.ui.compose.material3.buttons.PreviousButton
import androidx.media3.ui.compose.material3.buttons.RepeatButton
import androidx.media3.ui.compose.material3.buttons.SeekBackButton
import androidx.media3.ui.compose.material3.buttons.SeekForwardButton

/** 片源芯片展示用：短标题 + 可加载的流媒体地址。 */
data class Media3StreamPreset(
    val label: String,
    val uri: String,
)

private val speedStops = listOf(0.5f, 1f, 1.25f, 1.5f, 2f)

private data class Media3PlaybackLabelState(
    val statusLine: String = "",
    val lastError: String? = null,
    val isPlayingUi: Boolean = false,
)

// 水平边距、视频圆角卡片、底栏仅顶角圆角
private val PanelHorizontalPadding = 20.dp
private val VideoCardShape = RoundedCornerShape(20.dp)
private val ControlDockShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

/** 分区标题行（可选左侧图标）。 */
@Composable
private fun Media3SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        icon?.invoke()
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = scheme.onSurface,
        )
    }
}

/** 整行横向滚动，承载多枚 FilterChip。 */
@Composable
private fun Media3ChipRowScroll(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

/**
 * Media3 演示向整体布局：视频区为 [Media3PlayerContent]；下方为可滚动工具条与贴底传输栏。
 * 内部自建 [ExoPlayer]：`ON_STOP` 暂停，`dispose` 时 [Player.release]。
 *
 * M3U8 等需在模块依赖 `media3-exoplayer-hls` 等渲染模块。
 *
 * @param modifier 根 Column 修饰，一般为 `fillMaxSize()` 或与 `weight` 组合。
 * @param presets 横向片源芯片；空列表则整段不展示。
 * @param initialUri 首次进入时 applied 地址与输入框初始文案。
 * @param dualPlaylistUris 非 null 时展示「两段连播」芯片，并以 Pair 内两 URI 建列表。
 * @param topContent 置于最上方的插槽（如说明条）。
 */
@Composable
fun Media3PlayerScaffold(
    modifier: Modifier = Modifier,
    presets: List<Media3StreamPreset>,
    initialUri: String,
    dualPlaylistUris: Pair<String, String>? = null,
    topContent: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scheme = MaterialTheme.colorScheme

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    // 进程后台时暂停，避免继续解码；页面销毁时释放 native 资源
    DisposableEffect(lifecycleOwner, player) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                player.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    // 地址草稿 / 实际加载 / 是否走两段列表
    var draftUrl by rememberSaveable { mutableStateOf(initialUri) }
    var appliedUrl by rememberSaveable { mutableStateOf(initialUri) }
    var dualPlaylistMode by rememberSaveable { mutableStateOf(false) }

    // 传入 Media3PlayerContent：Surface 类型、Fit/Crop、reset 是否留末帧
    var surfaceType by rememberSaveable { mutableIntStateOf(SURFACE_TYPE_SURFACE_VIEW) }
    var useCropScale by rememberSaveable { mutableStateOf(false) }
    var keepContentOnReset by rememberSaveable { mutableStateOf(false) }

    var selectedSpeed by rememberSaveable { mutableFloatStateOf(1f) }
    var advancedExpanded by rememberSaveable { mutableStateOf(false) }

    // 状态文案、错误信息、圆点是否视为播放中
    var playbackLabels by remember { mutableStateOf(Media3PlaybackLabelState()) }

    // 播放状态、列表索引、错误回调 → 刷新文案与错误条
    DisposableEffect(player) {
        fun syncLabel() {
            playbackLabels = playbackLabels.copy(
                isPlayingUi = player.isPlaying,
                statusLine = buildString {
                    append(
                        when (player.playbackState) {
                            Player.STATE_IDLE -> "空闲"
                            Player.STATE_BUFFERING -> "缓冲中"
                            Player.STATE_READY ->
                                if (player.isPlaying) "播放中" else "已暂停"
                            Player.STATE_ENDED -> "已播完"
                            else -> "未知"
                        },
                    )
                    append(" · ${"%.2f".format(player.playbackParameters.speed)}x")
                    if (player.mediaItemCount > 1) {
                        append(" · 第 ${player.currentMediaItemIndex + 1}/${player.mediaItemCount} 段")
                    }
                },
            )
        }
        syncLabel()
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                syncLabel()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                syncLabel()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                syncLabel()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                syncLabel()
            }

            override fun onPlayerError(error: PlaybackException) {
                playbackLabels = playbackLabels.copy(
                    lastError = error.message ?: error.errorCodeName,
                )
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(selectedSpeed, player) {
        // 倍速变更即时作用到同一 Player
        player.playbackParameters = PlaybackParameters(selectedSpeed)
    }

    // 单地址或两段列表切换时重建 MediaItem 并重新 prepare
    LaunchedEffect(appliedUrl, dualPlaylistMode, dualPlaylistUris, player) {
        playbackLabels = playbackLabels.copy(lastError = null)
        player.stop()
        val dual = dualPlaylistUris
        if (dualPlaylistMode && dual != null) {
            player.setMediaItems(
                listOf(MediaItem.fromUri(dual.first), MediaItem.fromUri(dual.second)),
            )
        } else {
            player.setMediaItem(MediaItem.fromUri(appliedUrl))
        }
        player.prepare()
        player.playWhenReady = true
    }

    val contentScale = if (useCropScale) ContentScale.Crop else ContentScale.Fit

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.surface),
    ) {
        topContent?.invoke()

        // 主画面：weight(1f) 吃满除工具条与底栏外的竖直空间
        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = PanelHorizontalPadding, vertical = 6.dp),
            shape = VideoCardShape,
            colors = CardDefaults.outlinedCardColors(containerColor = scheme.surface),
        ) {
            Media3PlayerContent(
                player = player,
                modifier = Modifier.fillMaxSize(),
                surfaceType = surfaceType,
                contentScale = contentScale,
                keepContentOnReset = keepContentOnReset,
            )
        }

        // 片源、倍速、高级渲染选项、状态、地址（限高可纵向滚动）
        val toolsScroll = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 280.dp)
                .verticalScroll(toolsScroll),
        ) {
            if (presets.isNotEmpty()) {
                Media3SectionTitle(
                    text = "片源",
                    modifier = Modifier.padding(
                        start = PanelHorizontalPadding,
                        end = PanelHorizontalPadding,
                        top = 4.dp,
                        bottom = 4.dp,
                    ),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = scheme.primary,
                        )
                    },
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = PanelHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(presets, key = { it.label }) { preset ->
                        val selected = !dualPlaylistMode && appliedUrl == preset.uri
                        FilterChip(
                            selected = selected,
                            onClick = {
                                dualPlaylistMode = false
                                draftUrl = preset.uri
                                appliedUrl = preset.uri
                            },
                            label = { Text(preset.label, style = MaterialTheme.typography.labelLarge) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = scheme.primaryContainer,
                                selectedLabelColor = scheme.onPrimaryContainer,
                                containerColor = scheme.surfaceContainerHighest,
                                labelColor = scheme.onSurface,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = scheme.outlineVariant,
                            ),
                        )
                    }
                    if (dualPlaylistUris != null) {
                        item(key = "dual") {
                            FilterChip(
                                selected = dualPlaylistMode,
                                onClick = { dualPlaylistMode = true },
                                label = { Text("两段连播", style = MaterialTheme.typography.labelLarge) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = scheme.secondaryContainer,
                                    selectedLabelColor = scheme.onSecondaryContainer,
                                    containerColor = scheme.surfaceContainerHighest,
                                    labelColor = scheme.onSurface,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = dualPlaylistMode,
                                    borderColor = scheme.outlineVariant,
                                ),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = PanelHorizontalPadding,
                        end = PanelHorizontalPadding,
                        top = 8.dp,
                        bottom = 4.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = scheme.primary,
                )
                Text(
                    text = "倍速",
                    style = MaterialTheme.typography.titleSmall,
                    color = scheme.onSurface,
                )
            }
            Media3ChipRowScroll {
                Spacer(Modifier.width(PanelHorizontalPadding))
                speedStops.forEach { sp ->
                    val on = selectedSpeed == sp
                    FilterChip(
                        selected = on,
                        onClick = { selectedSpeed = sp },
                        label = { Text("${sp}x", style = MaterialTheme.typography.labelLarge) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = scheme.primary,
                            selectedLabelColor = scheme.onPrimary,
                            containerColor = scheme.surfaceContainerHigh,
                            labelColor = scheme.onSurfaceVariant,
                        ),
                    )
                }
                Spacer(Modifier.width(PanelHorizontalPadding))
            }

            TextButton(
                onClick = { advancedExpanded = !advancedExpanded },
                modifier = Modifier.padding(start = 8.dp, end = PanelHorizontalPadding),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(if (advancedExpanded) "收起画面与渲染选项" else "画面与渲染（Surface / 缩放）")
            }
            if (advancedExpanded) {
                Media3ChipRowScroll {
                    Spacer(Modifier.width(PanelHorizontalPadding))
                    FilterChip(
                        selected = surfaceType == SURFACE_TYPE_SURFACE_VIEW,
                        onClick = { surfaceType = SURFACE_TYPE_SURFACE_VIEW },
                        label = { Text("SurfaceView") },
                    )
                    FilterChip(
                        selected = surfaceType == SURFACE_TYPE_TEXTURE_VIEW,
                        onClick = { surfaceType = SURFACE_TYPE_TEXTURE_VIEW },
                        label = { Text("TextureView") },
                    )
                    FilterChip(
                        selected = !useCropScale,
                        onClick = { useCropScale = false },
                        label = { Text("Fit 留黑边") },
                    )
                    FilterChip(
                        selected = useCropScale,
                        onClick = { useCropScale = true },
                        label = { Text("Crop 裁切铺满") },
                    )
                    FilterChip(
                        selected = keepContentOnReset,
                        onClick = { keepContentOnReset = !keepContentOnReset },
                        label = { Text("Reset 保留末帧") },
                    )
                    Spacer(Modifier.width(PanelHorizontalPadding))
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = PanelHorizontalPadding),
                color = scheme.outlineVariant,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PanelHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                playbackLabels.lastError != null -> scheme.error
                                playbackLabels.isPlayingUi -> scheme.primary
                                else -> scheme.outline
                            },
                        ),
                )
                Text(
                    text = playbackLabels.statusLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
            }
            playbackLabels.lastError?.let { err ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PanelHorizontalPadding, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = scheme.errorContainer,
                ) {
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            OutlinedTextField(
                value = draftUrl,
                onValueChange = { draftUrl = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PanelHorizontalPadding, vertical = 8.dp),
                label = { Text("媒体地址") },
                leadingIcon = {
                    Icon(Icons.Outlined.Link, contentDescription = null)
                },
                trailingIcon = {
                    val canEdit = !dualPlaylistMode || dualPlaylistUris == null
                    FilledTonalButton(
                        onClick = {
                            dualPlaylistMode = false
                            appliedUrl = draftUrl.trim()
                        },
                        enabled = canEdit,
                        modifier = Modifier.padding(end = 4.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text("加载", style = MaterialTheme.typography.labelLarge)
                    }
                },
                enabled = !dualPlaylistMode || dualPlaylistUris == null,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = scheme.primary,
                    unfocusedBorderColor = scheme.outlineVariant,
                ),
            )
            Spacer(Modifier.height(6.dp))
        }

        // Material3 媒体按钮：上/下一首、快退快进、播放暂停、循环、静音
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ControlDockShape,
            color = scheme.surfaceContainerLow,
            tonalElevation = 2.dp,
            shadowElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PanelHorizontalPadding, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Media3SectionTitle(
                    text = "播放控制",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = scheme.surfaceContainerHigh,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PreviousButton(player = player)
                            SeekBackButton(player = player)
                            PlayPauseButton(player = player)
                            SeekForwardButton(player = player)
                            NextButton(player = player)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RepeatButton(player = player)
                            Spacer(Modifier.width(24.dp))
                            MuteButton(player = player)
                        }
                    }
                }
            }
        }
    }
}
