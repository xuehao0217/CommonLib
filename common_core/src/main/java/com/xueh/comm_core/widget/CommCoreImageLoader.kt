package com.xueh.comm_core.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.svg.SvgDecoder
import java.util.concurrent.atomic.AtomicReference

/**
 * common_core 内共享的 Coil [ImageLoader]（含 [SvgDecoder]），按 Application Context 单例缓存。
 */
object CommCoreImageLoader {
    private val holder = AtomicReference<ImageLoader?>(null)

    fun get(context: Context): ImageLoader {
        val app = context.applicationContext
        holder.get()?.let { return it }
        synchronized(this) {
            holder.get()?.let { return it }
            return ImageLoader.Builder(app)
                .components { add(SvgDecoder.Factory()) }
                .build()
                .also { holder.set(it) }
        }
    }
}

@Composable
fun rememberCommCoreImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember(context.applicationContext) { CommCoreImageLoader.get(context) }
}
