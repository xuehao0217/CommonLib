package com.xueh.comm_core.net

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * 创 建 人: xueh
 * 创建日期: 2019/4/18 17:20
 * 备注：
 */
class XTrustManager :X509TrustManager {

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }
}