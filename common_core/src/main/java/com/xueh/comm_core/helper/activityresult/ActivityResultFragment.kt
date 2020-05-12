package com.xueh.comm_core.helper.activityresult


import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * activity 结果代理fragment
 */
class ActivityResultFragment : Fragment() {
    private var activityResultListener: ActivityResultListener? = null
    fun setActivityResultListener(activityResultListener: ActivityResultListener?) {
        this.activityResultListener = activityResultListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (activityResultListener != null) activityResultListener!!.onReceiveResult(
            resultCode,
            data
        )
    }
}