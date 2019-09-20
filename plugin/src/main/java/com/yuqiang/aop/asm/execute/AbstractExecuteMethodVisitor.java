package com.yuqiang.aop.asm.execute;

import com.yuqiang.aop.util.Log;
import com.yuqiang.aop.asm.api.IExecute;
import com.yuqiang.aop.asm.model.RuleConfigManager;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

/**
 * @author yuqiang
 */
public abstract class AbstractExecuteMethodVisitor extends MethodVisitor implements Opcodes, IExecute {

    protected Source source;
    protected List<Target> targetList;


    AbstractExecuteMethodVisitor(MethodVisitor mv, Source source) {
        super(ASM5, mv);
        this.source = source;
        targetList = findTarget(source);
    }

    @Override
    public final synchronized List<Target> findTarget(Source source) {
        source.setAnnotationDesc(Type.getType(getAnnotationClass()).getDescriptor());
        return RuleConfigManager.getInstance().findTarget(source);
    }

    /**
     * 当前处理的注解Class
     * @return  Class
     */
    protected abstract Class getAnnotationClass();

    /**
     * 当前处理的注解tag
     * @return  tag
     */
    protected abstract String getTag();


    protected void innerRunMethodInst(Target target) {
        String key = String.format("%s.%s.%s advice by %s.%s.%s", source.getClassName(), source.getMethodName(), source.getMethodDesc(),
                target.getClassName(), target.getMethodName(), target.getMethodDesc());

        Log.i(getTag(), key);
        mv.visitMethodInsn(INVOKESTATIC, target.getClassName(), target.getMethodName(), target.getMethodDesc(), false);
    }
}
