package com.xueh.comm_core.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageScope
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder


/**
 * 基于 Coil AsyncImage 的网络图片加载组件，支持占位图、错误图及加载回调
 * @param url 图片地址
 * @param placeholder 加载中占位图
 * @param error 加载失败时显示的图片
 * @param onSuccess 加载成功回调
 * @param onError 加载失败回调
 */
@Composable
fun ImageLoadAsyncImage(
    url: String, modifier: Modifier = Modifier, placeholder: Painter? = null,
    error: Painter? = null, onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) = AsyncImage(
    model = ImageRequest.Builder(LocalContext.current).data(url.trim()).crossfade(true).build(),
    modifier = modifier,
    contentScale = ContentScale.Crop,
    contentDescription = null,
    placeholder = placeholder, //加载中展位图
    error = error,
    onError = onError,
    onSuccess = onSuccess //加载成功
)

/**
 * 基于 Coil SubcomposeAsyncImage 的网络图片组件，支持按加载/成功/失败状态自定义子内容
 * @param url 图片地址
 * @param loading 加载中时显示的可组合内容
 * @param success 加载成功时显示的可组合内容
 * @param error 加载失败时显示的可组合内容
 * @param onLoading 加载中回调
 * @param onSuccess 加载成功回调
 * @param onError 加载失败回调
 */
@Composable
fun ImageLoadCompose(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    loading: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(url.trim()).crossfade(true).build(),
        modifier = modifier,
        contentScale = contentScale,
        contentDescription = null,
        loading = loading,
        success = success,
        error = error,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
    )
}


//@Composable
//fun ImageCompose(
//    @DrawableRes id: Int,
//    modifier: Modifier = Modifier,
//    colorFilter: ColorFilter? = null,
//    alignment: Alignment = Alignment.Center,
//    contentScale: ContentScale = ContentScale.Crop,
//) {
//    val imgLoader = ImageLoader.Builder(Utils.getApp())
//        .components {
//            if (Build.VERSION.SDK_INT >= 28) {
//                add(ImageDecoderDecoder.Factory())
//            } else {
//                add(GifDecoder.Factory())
//            }
//        }
//        .build()
//
//    Image(
//        painter = rememberAsyncImagePainter(id, imgLoader),
//        contentDescription = "ImageComposeContentDescription",
//        modifier = modifier,
//        contentScale = contentScale,
//        colorFilter = colorFilter,
//        alignment = alignment
//    )
//}

/**
 * 本地 drawable 资源图片组件，使用 painterResource 加载
 * @param id drawable 资源 ID
 * @param colorFilter 颜色滤镜，可用于着色
 * @param alignment 对齐方式
 * @param contentScale 缩放模式
 */
@Composable
fun ImageCompose(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
) = Image(
    painter = painterResource(id = id),
    contentDescription = "ImageComposeContentDescription",
    modifier = modifier,
    contentScale = contentScale,
    colorFilter = colorFilter,
    alignment = alignment
)

/**
 * 创建支持 SVG 的 AsyncImagePainter，用于加载 SVG 等矢量图
 * @param model 图片模型（URL、Uri 等）
 * @param imageLoader 带 SvgDecoder 的 ImageLoader
 * @param transform 状态转换
 * @param onState 状态回调
 * @param contentScale 缩放模式
 * @param filterQuality 过滤质量
 */
@Composable
fun rememberSvgPainter(
    model: Any?,
    imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

/**
 * 从 assets 目录加载图片的 Painter，支持 SVG
 * @param path assets 下的相对路径（如 "drawable/xxx.svg"）
 * @param imageLoader 带 SvgDecoder 的 ImageLoader
 * @param transform 状态转换
 * @param onState 状态回调
 * @param contentScale 缩放模式
 * @param filterQuality 过滤质量
 */
@Composable
fun rememberAssetsPainter(
    path: String,
    imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = "file:///android_asset/${path}",
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality,
    )
}


/**
 * 从 assets/drawable 加载 SVG 图片的可组合组件
 * @param assetsName 资源名（不含扩展名），实际路径为 drawable/${assetsName}.svg
 * @param colorFilter 颜色滤镜
 * @param alignment 对齐方式
 * @param contentScale 缩放模式
 */
@Composable
fun ImageSVGCompose(
    assetsName: String,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
) = Image(
    painter = rememberAssetsPainter("drawable/${assetsName}.svg"),
    contentDescription = "ImageComposeContentDescription",
    modifier = modifier,
    contentScale = contentScale,
    colorFilter = colorFilter,
    alignment = alignment
)





