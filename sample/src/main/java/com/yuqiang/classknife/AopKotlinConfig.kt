package com.yuqiang.classknife

import android.util.Log
import com.yuqiang.aop.annotations.Around
import com.yuqiang.aop.annotations.Aspect
import com.yuqiang.aop.annotations.Replace
import com.yuqiang.aop.annotations.TryCatch

@Aspect
class AopKotlinConfig {

    companion object {
        private const val TAG: String = "AopKotlinConfig"

        @TryCatch(target = ["com.yuqiang.classknife.BaseActivity.<clinit>()V"])
        @JvmStatic
        fun kotlinTryCatchBlock(throwable: Throwable?) {
            Log.e(TAG, "....." + Log.getStackTraceString(throwable))
        }

        @Around(target = ["com.yuqiang.classknife.BaseActivity.<clinit>()V"], enter = true)
        @JvmStatic
        fun cconIn() {
            Log.e(TAG, "------BaseActivity.cinitIn-->>>")
        }

        @Around(target = ["com.yuqiang.classknife.BaseActivity.<clinit>()V"], enter = false)
        @JvmStatic
        fun cconOut() {
            Log.e(TAG, "------BaseActivity.cinitOut-->>>")
        }
        //静态代码块 end

        @Replace(target = ["java.lang.Thread.start()V"])
        @JvmStatic
        fun hookThreadStart(thread: Thread?) {
            thread?.name = "AopConfigUtil.hookThread.start(###?$$$)"
            thread?.start()
        }

        @JvmStatic
        @TryCatch(target = arrayOf("com.yuqiang.classknife.MainActivity.onCreate(Landroid/os/Bundle;)V"))
        fun mainTryCatch(throwable: Throwable) {
            Log.e(TAG, "------AopConfigUtil.mainTryCatch-->>>" + throwable.message)
        }
    }
}