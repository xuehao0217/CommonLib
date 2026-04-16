# =============================================================================
# app 模块（com.xueh.commonlib）
# common_core 已通过 consumerProguardFiles 合并：kotlinx.serialization、Retrofit/OkHttp、
# Coil、MMKV 等；此处只保留 app 侧最小补充，避免与清单/依赖自带规则重复。
# =============================================================================

# kotlinx.serialization：$serializer 与 Companion 由 common_core 的 com.xueh.** 规则 + 库自带规则覆盖；
# 若 release 出现反序列化/多态异常，再按需为具体包增加 -keep @kotlinx.serialization.Serializable。
# （此前整包 com.xueh.commonlib.** { *; } 已删除以缩小保留范围。）

# Retrofit 3 自带 consumer 规则，无需再 keep 接口。

# Application 由 AndroidManifest 引用，R8 会保留入口类。

# -----------------------------------------------------------------------------
# 崩溃还原：行号（源文件名可选隐藏）
# -----------------------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-printmapping proguard-mapping.txt

# -----------------------------------------------------------------------------
# WebView（JS 桥与 WebViewClient；AgentWeb / 系统 WebView 运行时）
# -----------------------------------------------------------------------------
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

# JNI（依赖库若含 native 方法）
-keepclasseswithmembernames class * {
    native <methods>;
}
