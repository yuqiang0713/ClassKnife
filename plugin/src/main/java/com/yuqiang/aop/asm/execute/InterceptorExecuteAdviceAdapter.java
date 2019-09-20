package com.yuqiang.aop.asm.execute;

import com.yuqiang.aop.annotations.Interceptor;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * {@link com.yuqiang.aop.annotations.Interceptor}
 * @author yuqiang
 */
public class InterceptorExecuteAdviceAdapter extends AbstractExecuteAdviceAdapter {

    public InterceptorExecuteAdviceAdapter(MethodVisitor mv, Source source) {
        super(mv, source);
    }

    @Override
    protected Class getAnnotationClass() {
        return Interceptor.class;
    }

    @Override
    protected String getTag() {
        return "Interceptor";
    }

    @Override
    protected void innerRunMethodInst(Target target) {
        if (!Type.getReturnType(target.getMethodDesc()).equals(Type.getType(boolean.class))) {
            throw new RuntimeException("Interceptor func returnType Must Boolean");
        }
        if (!Type.getReturnType(source.getMethodDesc()).equals(Type.getType(void.class))) {
            throw new RuntimeException("Source func returnType Must Void");
        }
        super.innerRunMethodInst(target);
        Label l1 = new Label();
        if (target.getReturnValue()) {
            mv.visitJumpInsn(IFEQ, l1);
        } else {
            mv.visitJumpInsn(IFNE, l1);
        }
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitInsn(RETURN);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }
}
