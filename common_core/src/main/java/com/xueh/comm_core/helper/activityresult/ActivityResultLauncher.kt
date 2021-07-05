package com.xueh.comm_core.helper.activityresult

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

/**
 * 创 建 人: xueh
 * 创建日期: 2021/7/5 12:17
 * 备注：
 */
/*

launcher.lunch<SecondActivity>(
setIntent = {
    //配置请求 intent
    it.putExtra("Configs.LOCATION_RESULT", "value form Main")
},
onSuccess = {
    val location = it?.getStringExtra("Configs.LOCATION_RESULT")
    Log.i(TAG, "startActivityForResultClick: $location")
}
)

setResult(IntentBuilder.builder().params("key1","key1").params("key2","key3"))
*/


class ActivityResultLauncher :
    BaseLauncher<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) {
    var onError: (resultCode: Int) -> Unit = {}
    var onSuccess: (intent: Intent?) -> Unit = {}

    inline fun <reified T : Activity> lunch(
        crossinline setIntent: (intent: Intent) -> Unit = {},
        noinline onError: (resultCode: Int) -> Unit = {},
        noinline onSuccess: (intent: Intent?) -> Unit = {}
    ) {
        this.onError = onError
        this.onSuccess = onSuccess
        val intent = Intent(activity, T::class.java)
        //根据配置设置 intent
        setIntent.invoke(intent)
        launcher.launch(intent)
    }

    override fun onActivityResult(result: ActivityResult?) {
        if (Activity.RESULT_OK == result?.resultCode) onSuccess.invoke(result.data)
        else onError.invoke(result?.resultCode ?: Activity.RESULT_CANCELED)
    }
}