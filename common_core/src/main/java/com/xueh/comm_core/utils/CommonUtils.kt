package com.xueh.comm_core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputFilter
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import java.util.*
import java.util.regex.Pattern

/**
 * 创建日期: 2017/7/7 16:13
 * 备注：常用的工具方法
 */
object CommonUtils {
    @JvmStatic
    fun getColor(colorId: Int): Int {
        return ContextCompat.getColor(Utils.getApp(), colorId)
    }

    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(Utils.getApp(), id)
    }

    fun getString(strId: Int): String {
        return Utils.getApp().resources.getString(strId)
    }

    fun getArrayString(strId: Int): Array<String> {
        return Utils.getApp().resources.getStringArray(strId)
    }

    fun checkString(str: String?): Boolean {
        return !StringUtils.isEmpty(str) && !StringUtils.equalsIgnoreCase(
            str,
            "null"
        )
    }

    /**
     * 获取设备唯一标识
     */
    fun getUniqueID(context: Context): String {
        val tm = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val tmDevice: String
        val tmSerial: String
        val androidId: String
        tmDevice = "" + tm.deviceId
        tmSerial = "" + tm.simSerialNumber
        androidId = "" + Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val deviceUuid = UUID(
            androidId.hashCode().toLong(),
            tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong()
        )
        return deviceUuid.toString()
    }

    /**
     * 检测是不是全部中文！
     */
    fun containsSpecialChar(source: String?): Boolean {
        val p =
            Pattern.compile("^[A-Za-z\\u4E00-\\u9FA5]+$")
        val m = p.matcher(source)
        return m.matches()
    }

    /**
     * 验证邮箱输入是否合法
     */
    fun isEmail(strEmail: String?): Boolean {
        val strPattern =
            "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"
        val p = Pattern.compile(strPattern)
        val m = p.matcher(strEmail)
        return m.matches()
    }

    /*
     * 将时分秒转为秒数 30：40
     * */
    fun formatTurnSecond(time: String): Long {
        val s = time.split(":").toTypedArray()
        var sum = 0
        if (s.size == 3) {
            val h = s[0].toInt() * 60 * 60
            val m = s[1].toInt() * 60
            val ss = s[2].toInt()
            sum = h + m + ss
        } else if (s.size == 2) {
            val m = s[0].toInt() * 60
            val ss = s[1].toInt()
            sum = m + ss
        }
        return sum.toLong()
    }

    /**
     * 检测是否有emoji表情
     */
    fun containsEmoji(source: String): Boolean {
        val len = source.length
        for (i in 0 until len) {
            val codePoint = source[i]
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true
            }
        }
        return false
    }

    /**
     * 判断是否是Emoji
     */
    private fun isEmojiCharacter(codePoint: Char): Boolean {
        return (codePoint.toInt() == 0x0 || codePoint.toInt() == 0x9 || codePoint.toInt() == 0xA || codePoint.toInt() == 0xD
                || codePoint.toInt() >= 0x20 && codePoint.toInt() <= 0xD7FF || codePoint.toInt() >= 0xE000 && codePoint.toInt() <= 0xFFFD || codePoint.toInt() >= 0x10000 && codePoint.toInt() <= 0x10FFFF)
    }

    /**
     * 禁止EditText输入空格   * @param editText
     */
    fun setEditTextInhibitInputSpace(editText: EditText) {
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            if (source == " ") {
                ""
            } else {
                null
            }
        }
        editText.filters = arrayOf(filter)
    }

    fun getStrFromView(textView: TextView): String {
        return textView.text.toString().trim { it <= ' ' }.replace(" ".toRegex(), "")
    }
}