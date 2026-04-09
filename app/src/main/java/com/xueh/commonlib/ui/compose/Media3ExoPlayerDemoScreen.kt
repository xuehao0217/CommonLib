package com.xueh.commonlib.ui.compose

// 主导航入口：只拼装 DemoScreenIntro + Media3PlayerScaffold；预设 URL 为演示直连，与库内 Scaffold 解耦。

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xueh.comm_core.widget.media3.Media3PlayerScaffold
import com.xueh.comm_core.widget.media3.Media3StreamPreset

/** 默认打开的海洋短片（HTTP，需 cleartext 或按域名放开）。 */
private const val DefaultDemoMp4 = "http://vjs.zencdn.net/v/oceans.mp4"
private const val SampleMp4Oceans = DefaultDemoMp4
private const val SampleMp4Xgplayer360p =
    "https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4"
private const val SampleMp4W3schoolBbb = "http://www.w3school.com.cn/example/html5/mov_bbb.mp4"
private const val SampleMp4W3schoolsMovie = "https://www.w3schools.com/html/movie.mp4"
private const val SampleMp4SintelTrailer = "https://media.w3.org/2010/05/sintel/trailer.mp4"
private const val SampleMp4NewsIqilu1 =
    "https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209105011F0zPoYzHry.mp4"
private const val SampleMp4NewsIqilu2 =
    "https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4"
private const val SampleM3u8Xgplayer =
    "https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/hls/xgplayer-demo.m3u8"
private const val SampleM3u8MuxBbb = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

/** 与 Media3PlayerScaffold 的芯片列表一一对应，便于改地址只改此处。 */
private val demoPresets = listOf(
    Media3StreamPreset("MP4 · 海洋", SampleMp4Oceans),
    Media3StreamPreset("MP4 · 西瓜 Demo", SampleMp4Xgplayer360p),
    Media3StreamPreset("MP4 · 大兔子", SampleMp4W3schoolBbb),
    Media3StreamPreset("MP4 · 大灰熊", SampleMp4W3schoolsMovie),
    Media3StreamPreset("MP4 · 冰川", SampleMp4SintelTrailer),
    Media3StreamPreset("MP4 · 新闻1", SampleMp4NewsIqilu1),
    Media3StreamPreset("MP4 · 新闻2", SampleMp4NewsIqilu2),
    Media3StreamPreset("M3U8 · 西瓜 Demo", SampleM3u8Xgplayer),
    Media3StreamPreset("M3U8 · 大白兔", SampleM3u8MuxBbb),
)

/**
 * Media3 演示：业务只关心 [Media3StreamPreset] 与 [Media3PlayerScaffold] 参数；播放器 UI 在 common_core。
 */
@Composable
fun Media3ExoPlayerDemoScreen() {
    Column(Modifier.fillMaxSize()) {
        DemoScreenIntro(
            text = "视频区紧贴说明下方并占满大部分屏幕；片源、倍速与地址在下方窄条内滚动。实现见 common_core/widget/media3。",
        )
        Media3PlayerScaffold(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            presets = demoPresets,
            initialUri = DefaultDemoMp4,
            dualPlaylistUris = SampleMp4Oceans to SampleMp4Xgplayer360p,
        )
    }
}
