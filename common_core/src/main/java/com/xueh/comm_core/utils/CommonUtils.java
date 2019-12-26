package com.xueh.comm_core.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.annotations.Nullable;

/**
 * 创建日期: 2017/7/7 16:13
 * 备注：常用的工具方法
 */
public class CommonUtils {
    /**
     * 将dip转换为px
     */
    public static int dip2px(float dpValue) {
        final float scale = Utils.getApp().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px转换为dip
     */
    public static int px2dip(float pxValue) {
        final float scale = Utils.getApp().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(float pxValue) {
        final float fontScale = Utils.getApp().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp转换为px
     */
    public static int sp2px(float spValue) {
        final float fontScale = Utils.getApp().getResources()
                .getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getColor(int colorId) {
        return ContextCompat.getColor(Utils.getApp(), colorId);
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(Utils.getApp(), id);
    }

    public static String getString(int strId) {
        return Utils.getApp().getResources().getString(strId);
    }
    public static String[] getArrayString(int strId) {
        return Utils.getApp().getResources().getStringArray(strId);
    }

    public static boolean checkString(String str) {
        return !StringUtils.isEmpty(str) && !StringUtils.equalsIgnoreCase(str, "null");
    }

    /**
     * 获取设备唯一标识
     */
    public static String getUniqueID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(
                androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        return deviceUuid.toString();
    }

    /**
     * 判断是否具有网络连接
     */
    public static final boolean hasNetWorkConection() {
        //获取连接活动管理器
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) Utils.getApp()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取连接的网络信息
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable());
    }


    /**
     * 检测是不是全部中文！
     */
    public static boolean containsSpecialChar(@Nullable String source) {
        Pattern p = Pattern.compile("^[A-Za-z\\u4E00-\\u9FA5]+$");
        Matcher m = p.matcher(source);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 验证邮箱输入是否合法
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /*
     * 将时分秒转为秒数 30：40
     * */
    public static long formatTurnSecond(String time) {
        String s[] = time.split(":");
        int sum = 0;

        if (s.length == 3) {
            int h = Integer.parseInt(s[0]) * 60 * 60;
            int m = Integer.parseInt(s[1]) * 60;
            int ss = Integer.parseInt(s[2]);
            sum = h + m + ss;

        } else if (s.length == 2) {
            int m = Integer.parseInt(s[0]) * 60;
            int ss = Integer.parseInt(s[1]);
            sum = m + ss;
        }
        return sum;
    }


    /**
     * 检测是否有emoji表情
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (
                codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }


    /**
     * 禁止EditText输入空格   * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(
                    CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static String getStrFromView(TextView textView) {
        return textView.getText().toString().trim().replaceAll(" ", "");
    }


}
