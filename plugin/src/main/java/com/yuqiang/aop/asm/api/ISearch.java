package com.yuqiang.aop.asm.api;

import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;

import java.util.List;

/**
 * @author yuqiang
 */
public interface ISearch {
    /**
     * 通过Source查找Target集
     * @param source    当前扫描的方法信息
     * @return          要织入的目标代码信息
     */
    List<Target> findTarget(Source source);
}
