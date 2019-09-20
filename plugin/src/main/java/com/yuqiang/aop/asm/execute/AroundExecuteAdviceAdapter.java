package com.yuqiang.aop.asm.execute;

import com.yuqiang.aop.annotations.Around;
import com.yuqiang.aop.asm.model.Source;

import org.objectweb.asm.MethodVisitor;

/**
 * {@link com.yuqiang.aop.annotations.Around}
 * @author yuqiang
 */
public class AroundExecuteAdviceAdapter extends AbstractExecuteAdviceAdapter {

    public AroundExecuteAdviceAdapter(MethodVisitor mv, Source source) {
        super(mv, source);
    }

    @Override
    protected Class getAnnotationClass() {
        return Around.class;
    }

    @Override
    protected String getTag() {
        return "Around";
    }
}
