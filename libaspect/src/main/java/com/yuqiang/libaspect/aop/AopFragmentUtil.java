package com.yuqiang.libaspect.aop;


import com.yuqiang.aop.annotations.Aspect;
import com.yuqiang.aop.annotations.Replace;

@Aspect
public class AopFragmentUtil {

    @Replace(target = {"android.app.FragmentTransaction.commit()I"})
    public static int safeCommit(android.app.FragmentTransaction fr) {
        if (fr == null) {
            return -1;
        }
        return fr.commitAllowingStateLoss();
    }

    @Replace(target = {"android.support.v4.app.FragmentTransaction.commit()I"})
    public static int safeCommit(android.support.v4.app.FragmentTransaction fr) {
        if (fr == null) {
            return -1;
        }
        return fr.commitAllowingStateLoss();
    }
}
