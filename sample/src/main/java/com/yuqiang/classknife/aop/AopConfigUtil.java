package com.yuqiang.classknife.aop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yuqiang.aop.annotations.Around;
import com.yuqiang.aop.annotations.Aspect;
import com.yuqiang.aop.annotations.Interceptor;
import com.yuqiang.aop.annotations.Replace;
import com.yuqiang.aop.annotations.TryCatch;
import com.yuqiang.classknife.BaseActivity;
import com.yuqiang.classknife.MainActivity;

@Aspect
public class AopConfigUtil {

    private static final String TAG = "AopConfigUtil";

    //模糊匹配 start
    @Around(target = {"com.yuqiang.classknife.Sub.<init>(*)*"}, enter = true)
    public static void conSubIn() {
        Log.e(TAG, "------Sub.cinitIn-->>>");
    }

    @Around(target = {"com.yuqiang.classknife.Sub.<init>(*)*"}, enter = false)
    public static void conSubOut() {
        Log.e(TAG, "------Sub.cinitOut-->>>");
    }
    //模糊匹配 end

    //构造函数 start
    @Around(target = {"com.yuqiang.classknife.BaseActivity.<init>()V"}, enter = true)
    public static void conIn() {
        Log.e(TAG, "------BaseActivity.conIn-->>>");
    }

    @Around(target = {"com.yuqiang.classknife.BaseActivity.<init>()V"}, enter = false)
    public static void conOut() {
        Log.e(TAG, "------BaseActivity.conOut-->>>");
    }
    //构造函数 end

    @Around(target = {"com.yuqiang.classknife.BaseActivity.onCreate(Landroid/os/Bundle;)V"}, enter = true)
    public static void i(BaseActivity activity, Bundle bundle) {
        Log.e(TAG, activity + "------BaseActivity.onCreatei-->>>" + bundle);
    }

    @Around(target = {"com.yuqiang.classknife.BaseActivity.onCreate(Landroid/os/Bundle;)V"}, enter = false)
    public static void o(BaseActivity activity, Bundle bundle) {
        Log.e(TAG, activity + "------BaseActivity.onCreateo-->>>" + bundle);
    }

    @Around(target = {"com.yuqiang.classknife.MainActivity.onCreate(Landroid/os/Bundle;)V"}, enter = true)
    public static void i(MainActivity activity, Bundle bundle) {
        Log.e(TAG, activity + "------AopConfigUtil.MainActivityi-->>>" + bundle);
    }

    @Around(target = {"com.yuqiang.classknife.MainActivity.onCreate(Landroid/os/Bundle;)V"}, enter = false)
    public static void o(MainActivity activity, Bundle bundle) {
        Log.e(TAG, activity + "------AopConfigUtil.MainActivityo-->>>" + bundle);
    }

    // Around----after
    @Around(target = {"com.yuqiang.classknife.MainActivity.onStop()V"}, enter = false)
    public static void afterStop(Object activity) {
        Log.e(TAG, activity + "------AopConfigUtil.afterStop-->>>");
    }

    // Around---before
    @Around(target = {"com.yuqiang.classknife.MainActivity.onPause()V"}, enter = true)
    public static void beforePause(MainActivity activity) {
        Log.e(TAG, activity + "------AopConfigUtil.beforePause-->>>");
    }

    // TryCatch Block
    @TryCatch(target = {"com.yuqiang.classknife.BaseActivity.onCreate(Landroid/os/Bundle;)V"})
    public static void onCreateTryCatchBlock(Throwable throwable) {
        Log.e(TAG, "tryCatchBlock " + Log.getStackTraceString(throwable));
    }

    // Replace Pattern
    @Replace(target = {"android.content.Context.getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;"}, extend = true)
    public static SharedPreferences getSp(Context context, String name, int mode) {
        return null;
    }

    /****************************Around start****************************************************************************************************/
    @Around(target = {"android.support.v7.widget.RecyclerView$OnScrollListener.onScrollStateChanged(Landroid/support/v7/widget/RecyclerView;I)V",
            "android.support.v7.widget.RecyclerView$OnScrollListener.onScrolled(Landroid/support/v7/widget/RecyclerView;II)V"},
            extend = true, enter = true)
    public static void scrollEnter(RecyclerView recyclerView, int val) {
        Log.e(TAG, recyclerView + "------AopConfigUtil.i-->>>" + val);
    }

    @Around(target = {"android.support.v7.widget.RecyclerView$OnScrollListener.onScrollStateChanged(Landroid/support/v7/widget/RecyclerView;I)V",
            "android.support.v7.widget.RecyclerView$OnScrollListener.onScrolled(Landroid/support/v7/widget/RecyclerView;II)V"},
            enter = false, extend = true)
    public static void scrollExit(RecyclerView recyclerView, int val) {
        Log.e(TAG, recyclerView + "------AopConfigUtil.o-->>>" + val);
    }
    /*************************************************************************************/

    /****************************Interceptor start*****************************************/
    @Interceptor(target = {"com.yuqiang.classknife.MainActivity.onBackPressed()V"},
            extend = false, returnValue = true)
    public static boolean interceptorBlock() {
        return true;
    }
    /*************************************************************************************/

    /****************************静态方法Around start*****************************************/
    @Around(target = {"com.yuqiang.*.testStaticFun*(*)*"}, enter = true)
    public static void staticIn(MainActivity activity) {
        Log.e(TAG, activity + "------AopConfigUtil.staticIn-->>>");
    }

    @Around(target = {"com.yuqiang.*.testStaticFun*(*)*"}, enter = false)
    public static void staticOut(MainActivity activity, int bundle) {
        Log.e(TAG, activity + "------AopConfigUtil.staticOut-->>>" + bundle);
    }
    /***************************************************************************************************************************/

    /******************************Interceptor TryCatch Around三个注解作用同一个函数 start*****************************************/
    @Interceptor(target = {"android.view.View$OnClickListener.onClick(Landroid/view/View;)V"}, extend = true, returnValue = true)
    public static boolean clickInterceptor(View view) {
        return view.getContext() != null;
    }

    @TryCatch(target = {"android.view.View$OnClickListener.onClick(Landroid/view/View;)V"}, extend = true)
    public static void clickTryCatchBlock(Throwable throwable) {
        Log.e(TAG, "------AopConfigUtil.clickTryCatchBlock-->>>" + Log.getStackTraceString(throwable));
    }

    @Around(target = {"android.view.View$OnClickListener.onClick(Landroid/view/View;)V"}, extend = true, enter = true)
    public static void clickBefore(View view) {
        Log.e(TAG, "------AopConfigUtil.clickBefore-->>>" + view);
    }

    @Around(target = {"android.view.View$OnClickListener.onClick(Landroid/view/View;)V"}, enter = false, extend = true)
    public static void clickAfter(View view) {
        Log.e(TAG, "------AopConfigUtil.clickAfter-->>>" + view);
    }
    /***************************************************************************************************************************/
}
