package com.yuqiang.aop.asm.execute;

import com.yuqiang.aop.annotations.Timing;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;
import com.yuqiang.aop.util.Log;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * {@link com.yuqiang.aop.annotations.Timing}
 * @author yuqiang
 */
public class TimingExecuteAdviceAdapter extends AbstractExecuteAdviceAdapter {

    public TimingExecuteAdviceAdapter(MethodVisitor mv, Source source) {
        super(mv, source);
    }

    @Override
    protected Class getAnnotationClass() {
        return Timing.class;
    }

    @Override
    protected String getTag() {
        return "Timing";
    }

    @Override
    protected void innerRunMethodInst(Target target) {
        if (recursiveAdvice(target)) {
            Log.e(getTag(), "in kotlin @Aspect occurs java.lang.ExceptionInInitializerError discard!");
            return;
        }
        Type [] targetParamTypes = Type.getArgumentTypes(target.getMethodDesc());
        if (targetParamTypes.length == 1 &&
                targetParamTypes[0].equals(Type.getType(String.class))) {
            mv.visitLdcInsn(String.format("%s.%s.%s", source.getClassName(), source.getMethodName(), source.getMethodDesc()));
            mv.visitMethodInsn(INVOKESTATIC, target.getClassName(), target.getMethodName(), target.getMethodDesc(), false);
            String key = String.format("%s.%s.%s Timing by %s.%s.%s", source.getClassName(), source.getMethodName(), source.getMethodDesc(),
                    target.getClassName(), target.getMethodName(), target.getMethodDesc());

            Log.i(getTag(), key);
        } else {
            throw new RuntimeException("Timing func param must be (string) " + target.getClassName() + "." + target.getMethodName() + target.getMethodDesc());
        }
    }
}
