package com.yuqiang.aop.asm.execute;

import com.yuqiang.aop.annotations.TryCatch;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * {@link com.yuqiang.aop.annotations.TryCatch}
 * @author yuqiang
 */
public class TryCatchExecuteMethodVisitor extends AbstractExecuteMethodVisitor {

    private boolean isTryCatchBlock;

    public TryCatchExecuteMethodVisitor(MethodVisitor mv, Source source) {
        super(mv, source);
        isTryCatchBlock = targetList != null && targetList.size() == 1;
    }

    @Override
    protected Class getAnnotationClass() {
        return TryCatch.class;
    }

    @Override
    protected String getTag() {
        return "TryCatch";
    }

    private final Label start = new Label();
    private final Label end = new Label();
    private final Label handle = new Label();

    @Override
    public void visitCode() {
        super.visitCode();
        if (!isTryCatchBlock) {
            return;
        }
        visitTryCatchBlock(start, end, handle, "java/lang/Exception");
        visitLabel(start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (!isTryCatchBlock) {
            super.visitMaxs(maxStack, maxLocals);
            return;
        }

        visitLabel(end);

        Label v3 = new Label();
        visitJumpInsn(Opcodes.GOTO, v3);
        visitLabel(handle);

        Object[] objects = new Object[1];
        objects[0] = "java/lang/Exception";
        visitFrame(Opcodes.F_SAME1, 0, null, 1, objects);
        visitVarInsn(Opcodes.ASTORE, 1);

        visitVarInsn(Opcodes.ALOAD, 1);

        innerRunMethodInst(targetList.get(0));

        visitVarInsn(Opcodes.ALOAD, 1);
        visitLabel(v3);
        visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        visitInsn(Opcodes.RETURN);

        super.visitMaxs(maxStack + 2, maxLocals + 2);
    }

    @Override
    protected void innerRunMethodInst(Target target) {
        Type [] targetParamTypes = Type.getArgumentTypes(target.getMethodDesc());
        if (targetParamTypes.length == 1 &&
                targetParamTypes[0].equals(Type.getType(Throwable.class))) {
            super.innerRunMethodInst(target);
        } else {
            throw new RuntimeException("TryCatch func param must be (Throwable) " + target.getClassName() + "." + target.getMethodName() + target.getMethodDesc());
        }
    }
}
