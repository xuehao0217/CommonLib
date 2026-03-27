# =============================================================================
# app 模块（com.xueh.commonlib）
# common_core 已通过 consumerProguardFiles 提供：协程、kotlinx.serialization、Retrofit/OkHttp、
# Coil、MMKV、AgentWeb、Utilcodex、ProgressManager、XXPermissions、Chucker 等规则，此处勿重复堆砌。
# =============================================================================

# -----------------------------------------------------------------------------
# 本模块：序列化模型、Retrofit 接口、Application（启动与初始化）
# -----------------------------------------------------------------------------
-keep @kotlinx.serialization.Serializable class com.xueh.commonlib.** { *; }

# Retrofit 通过反射解析接口方法签名；保留接口声明（实现由 Retrofit 生成）
-keep interface com.xueh.commonlib.api.** { *; }

-keep class com.xueh.commonlib.MyApplication { *; }

# -----------------------------------------------------------------------------
# Lifecycle / ViewModel（Compose + ViewModel 工厂）
# -----------------------------------------------------------------------------
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# -----------------------------------------------------------------------------
# 崩溃还原：行号（源文件名可选隐藏）
# -----------------------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 发布 mapping（路径相对 app 模块；CI 可归档该文件）
-printmapping proguard-mapping.txt

# -----------------------------------------------------------------------------
# WebView（AgentWeb / 系统 WebViewClient）
# -----------------------------------------------------------------------------
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}

# -----------------------------------------------------------------------------
# Android 组件与常见模板（与 proguard-android-optimize 互补）
# -----------------------------------------------------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
