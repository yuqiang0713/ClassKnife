# ClassKnife
A lightweight Android Studio gradle plugin based on ASM for editing bytecode in Android.

# ClassKnife ![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat) ![Release Version](https://img.shields.io/badge/release-1.1.0-blue.svg)
----------
`ClassKnife`是一个轻量级的Android字节码插桩插件, 基于`ASM`指令对字节码进行操作,通过注解配置要处理的class文件以及要插入的目标代码,以达到对class文件进行动态修改对效果.和其它AOP方案不同,ClassKnife提供了一种更加轻量级,简单易用,无侵入,可配置化的字节码操作方式。使用者不需要了解ASM的相关处理字节码的指令，只需对应的方法配置对应的直接以及匹配规则即可实现类似AOP的功能,只需要引入一个注解库即可。
> **ASM**: (https://asm.ow2.io/asm4-guide.pdf)


## 功能
- **Replace**：在指定位置代码替换为指定代码
- **Interceptor**：在指定的位置添加拦截 如果不满足可不执行相关逻辑
- **Around**：在指定的方法前后插入指定的代码 或者方法前或者方法后 可配置
- **增强**：
	- **TryCatch** 在指定的位置添加try catch代码块
	- **Timing** 在指定的代码添加耗时统计代码

## 特点
* 灵活的配置方式, 一个注解配置就可以处理项目中的相关class文件。
* 丰富的字节码处理功能,根据当前公司的业务场景提供了代码替换，添加try catch, 方法的耗时统计。
* 简单易用，只需要依赖一个插件以及一个注解库，处理过程以及处理后的代码也不需要添加额外的依赖。
* 多线程扫描，处理字节码，只占用较少的编译时间。

## 使用指南

在 root project 的  `build.gradle`  里添加：

```groovy
dependencies {
    classpath "com.yuqiang.aop:class-knife:0.0.1"
}

在使用注解的地方引入注解库
dependencies {
    implementation 'com.yuqiang.aop:aop-annonations:0.0.1'
}

在app项目的build.gradle里应用插件
apply plugin: 'class-knife'
classKnife {
    enable          //插件开关 默认true
    include         //包含包名
    exclude         //排除的包名
}
//如果第三方库混淆了需要exclude出去 否则会导致参数列表出错 因为【为了尽可能节省栈帧空间，局部变量中的 Slot 是可以重用的】
```

## 函数配置规则

| 注解        | 函数签名列表   | Join Point类型 | Target | 
| --------   | -----:  | -----:  | -----:  |
| @Aspect       | - | - | 类 |
| @TryCatch	    |public static void tryCatchBlock(Throwable throwable) |execution |方法 | 
| @Interceptor	|public static boolean interceptor($this, $1,,$2...) | execution |方法 | 
| @Around       |public static void aroundBefore/After($this, $1, $2...) | execution | 方法 | 
| @Timing       |public static void timingBefore/After(String desc) | execution | 方法 | 
| @Replace      |public static void replaceFun($this, $1, $2....) |call |方法 | 

### 注解公共属性介绍
- target   是一个数组用来描述要处理的类的信息 格式 类名.方法名.函数签名 如 :  com.yuqianig.classknife.MainActivity.onBackPressed()V
- entend 是否继承匹配 默认false是类名完全匹配
- enter   默认true 表示环绕方法前相当于Before false相当于After 可单独配置其中一个也可配置俩个
- 其中 @TryCatch @Interceptor @Around @Timing可作用与同一个target
- 目前支持模糊匹配 * 
- 类名模糊匹配规则        com.yuqiang 如 com.yuqiang.*  匹配所有的 *
- 方法名模糊匹配规则    * 匹配所有  或者fun* 匹配fun你开头的所有方法名
- 签名模糊匹配规则       (*)* 所有都匹配 或者 (*)V 匹配返回值为Void类型的函数
- 类名 方法名 签名 结合使用
- 匹配的优先级顺序 模糊匹配 > 继承匹配 > 完全匹配

## 示例
```java
//Java文件配置
@Aspect
public class AopConfig {
     
    // ****************************************************** //
    // Interceptor TryCatch Around 这三个注解可针对同一个函数处理 *// 
    // ****************************************************** //
    private static final String TAG = "AopConfig";
 
    // 耗时统计 模糊匹配 start
    @Timing(target = {"com.yuqiang.*.*(*)*"}, enter = true)
    public static void timingBefore(String desc) {
        String msg = String.format("%s : (pid : %d tid : %s)  %s", "Before", Process.myPid(), Thread.currentThread().getName(), desc);
        Log.i(TAG, msg);
    }
 
    @Timing(target = {"com.yuqiang.*.*(*)*"}, enter = false)
    public static void timingAfter(String desc) {
        String msg = String.format("%s : (pid : %d tid : %s)  %s", "After ", Process.myPid(), Thread.currentThread().getName(), desc);
        Log.i(TAG, msg);
    }
    // 耗时统计 模糊匹配 end
 
    // 静态代码块 start
    @Around(target = {"com.yuqiang.classknife.BaseActivity.<clinit>()V"}, enter = true)
    public static void cconIn() {
        Log.e(TAG, "------BaseActivity.cinitIn-->>>");
    }
 
    @Around(target = {"com.yuqiang.classknife.BaseActivity.<clinit>()V"}, enter = false)
    public static void cconOut() {
        Log.e(TAG, "------BaseActivity.cinitOut-->>>");
    }
    // 静态代码块 end
 
 
    // 构造函数 start
    @Around(target = {"com.yuqiang.classknife.Sub.<init>(Ljava/lang/String;)V"}, enter = true)
    public static void conSubIn() {
        Log.e(TAG, "------Sub.cinitIn-->>>");
    }
 
    @Around(target = {"com.yuqiang.classknife.Sub.<init>(Ljava/lang/String;)V"}, enter = false)
    public static void conSubOut() {
        Log.e(TAG, "------Sub.cinitOut-->>>");
    }
    // 构造函数 end
 
    // click  start 其中extend表示继承匹配 否则完全匹配(字符串完全一样)
    @Around(target = {"android.view.View$OnClickListener.onClick(Landroid/view/View;)V"}, extend = true, enter = true)
    public static void clickBefore(View view) {
        Log.e(TAG, "------AopActivityUtil.clickBefore-->>>" + view);
    }
 
    @Around(target = {"android.view.View$OnClickListener.onClick(Landroid/view/View;)V"}, enter = false, extend = true)
    public static void clickAfter(View view) {
        Log.e(TAG, "------AopActivityUtil.clickAfter-->>>" + view);
    }
    // click  end
 
    // 拦截配置 start 其中returnValue表示该方法返回什么值进行拦截
    @Interceptor(target = {"com.yuqiang.classknife.MainActivity.onBackPressed()V"},
            extend = false, returnValue = true)
    public static boolean interceptorBlock() {
        return true;
    }
    // 拦截配置 end
 
 
    // try catch 配置 start
    @TryCatch(target = {"com.yuqiang.classknife.BaseActivity.start()V",
            "com.yuqiang.classknife.MainApp.onCreate()V"
    })
    public static void tryCatchBlock(Throwable throwable) {
        Log.e(TAG, "tryCatchBlock " + Log.getStackTraceString(throwable));
    }
    // try catch 配置 end
 
    // 替换配置 start
    @Replace(target = {"android.app.FragmentTransaction.commit()I", "android.support.v4.app.FragmentTransaction.commit()I"})
    public static int safeCommit(android.app.FragmentTransaction fr) {
        if (fr == null) return -1;
        return fr.commitAllowingStateLoss();
    }
    // 替换配置 end
}
```


```java
//Kotlin文件配置
@Aspect
class AopKotlinConfig {
 
    companion object {
        private const val TAG : String = "AopKotlinConfig"
 
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
        fun hookThreadStarat(thread: Thread?) {
            thread?.name = "AopConfigUtil.hookThread.start(###?$$$)"
            thread?.start()
        }
    }
}
```

## 编译完查看的目录如下
- build/outputs/classKnife/classKnife.txt（处理的函数信息）
- build/outputs/classKnife/rule.json(配置的规则信息)
- build/intermediates/transforms/classKnife/debug(release)(查看自己的class文件是否被修改)
