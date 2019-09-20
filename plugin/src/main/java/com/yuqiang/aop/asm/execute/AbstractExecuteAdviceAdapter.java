package com.yuqiang.aop.asm.execute;

import com.yuqiang.aop.util.Log;
import com.yuqiang.aop.asm.api.IExecute;
import com.yuqiang.aop.asm.model.RuleConfigManager;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;
import com.yuqiang.aop.util.StringUtil;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.List;

/**
 * @author yuqiang
 */
public abstract class AbstractExecuteAdviceAdapter extends AdviceAdapter implements Opcodes, IExecute {

    protected Source source;
    protected List<Target> targetList;

    private AbstractExecuteAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc);
    }

    /**
     * 注解处理器
     *
     * @param mv     MethodVisitor(next handler)
     * @param source 当前扫描到的类的方法的描述信息
     */
    AbstractExecuteAdviceAdapter(MethodVisitor mv, Source source) {
        this(ASM5, mv, source.getMethodAccess(), source.getMethodName(), source.getMethodDesc());
        this.source = source;
        targetList = findTarget(source);
    }

    @Override
    public synchronized List<Target> findTarget(Source source) {
        source.setAnnotationDesc(Type.getType(getAnnotationClass()).getDescriptor());
        return RuleConfigManager.getInstance().findTarget(source);
    }

    /**
     * 当前处理的注解Class
     *
     * @return Class
     */
    protected abstract Class getAnnotationClass();

    /**
     * 当前处理注解的tag
     *
     * @return 当前处理的类型 example Around,TryCatch.
     */
    protected abstract String getTag();

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        runMethodInst(true);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        runMethodInst(false);
    }

    private Target findTargetByType(boolean methodEnter) {
        if (targetList == null) {
            return null;
        }
        for (Target target : targetList) {
            if (target.isMethodEnter() == methodEnter) {
                return target;
            }
        }
        return null;
    }

    private void runMethodInst(boolean methodEnter) {
        Target target = findTargetByType(methodEnter);
        if (target != null) {
            innerRunMethodInst(target);
        }
    }

    protected void innerRunMethodInst(Target target) {
        if (recursiveAdvice(target)) {
            Log.e(getTag(), "in kotlin @Aspect occurs java.lang.ExceptionInInitializerError discard!");
            return;
        }
        boolean isStatic = (source.getMethodAccess() & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
        Type[] sourceParamTypes = Type.getArgumentTypes(source.getMethodDesc());
        Type[] targetParamTypes = Type.getArgumentTypes(target.getMethodDesc());
        int targetLen = targetParamTypes.length;
        int srcLen = sourceParamTypes.length;
        // 静态方法 target参数列表必须小于src的参数列表
        if (isStatic && targetLen > srcLen) {
            String msg = source.toString() + " : " + target.toString();
            throw new RuntimeException("Static func must have the same order param list " + msg);
        }
        //
        if (!isStatic && targetLen > srcLen + 1) {
            String msg = source.toString() + " : " + target.toString();
            throw new RuntimeException("Please check param list " + msg);
        }
        boolean match = true;
        if (targetLen > srcLen) {
            match = false;
        } else {
            int len = targetParamTypes.length;
            for (int i = 0; i < len; i++) {
                if (!sourceParamTypes[i].equals(targetParamTypes[i])) {
                    match = false;
                    break;
                }
            }
        }
        int targetIndex = 0;
        if (!isStatic) {
            if (match) {
                targetIndex = 1;
            } else {
                Type sourceThisType = Type.getObjectType(source.getClassName().replace(".", "/"));
                if (!sourceThisType.equals(targetParamTypes[targetIndex]) &&
                        !Type.getType(Object.class).equals(targetParamTypes[targetIndex])) {
                    String msg = source.toString() + " : " + target.toString();
                    throw new RuntimeException("Please check this param " + msg);
                }
            }
        }

        int targetParamLen = targetParamTypes.length;
        for (int i = 0; i < targetParamLen; i++) {
            Type currentType = targetParamTypes[i];
            int varIndex = targetIndex;
            if (currentType.equals(Type.getType(int.class))) {
                mv.visitVarInsn(ILOAD, varIndex);
            } else if (currentType.equals(Type.getType(boolean.class))) {
                mv.visitVarInsn(ILOAD, varIndex);
            } else if (currentType.equals(Type.getType(byte.class))) {
                mv.visitVarInsn(ILOAD, varIndex);
            } else if (currentType.equals(Type.getType(char.class))) {
                mv.visitVarInsn(ILOAD, varIndex);
            } else if (currentType.equals(Type.getType(int.class))) {
                mv.visitVarInsn(ILOAD, varIndex);
            } else if (currentType.equals(Type.getType(float.class))) {
                mv.visitVarInsn(FLOAD, varIndex);
            } else if (currentType.equals(Type.getType(long.class))) {
                mv.visitVarInsn(FLOAD, varIndex);
            } else if (currentType.equals(Type.getType(double.class))) {
                mv.visitVarInsn(DLOAD, varIndex);
            } else if (currentType.equals(Type.getType(Object.class))) {
                mv.visitVarInsn(ALOAD, varIndex);

                if (source.getClassName().contains("com.mintegral.msdk.video.module.MintegralVideoView") && source.getMethodName().equals("onClick")) {
                    Log.e("ErrorLog", "mv.visitVarInsn(ALOAD/Object, varIndex) >>> " + varIndex);

                }
            } else {
                mv.visitVarInsn(ALOAD, varIndex);
                if (source.getClassName().contains("com.mintegral.msdk.video.module.MintegralVideoView") && source.getMethodName().equals("onClick")) {
                    Log.e("ErrorLog", "mv.visitVarInsn(ALOAD/View, varIndex) >>> " + varIndex);
                }
            }
            targetIndex++;
        }

        String key = String.format("%s.%s.%s advice by %s.%s.%s", source.getClassName(), source.getMethodName(), source.getMethodDesc(),
                target.getClassName(), target.getMethodName(), target.getMethodDesc());

        Log.i(getTag(), key);
        mv.visitMethodInsn(INVOKESTATIC, target.getClassName(), target.getMethodName(), target.getMethodDesc(), false);
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super.visitFrame(type, nLocal, local, nStack, stack);
        if (source.getClassName().contains("com.mintegral.msdk.video.module.MintegralVideoView") && source.getMethodName().equals("onClick")) {
            StringBuilder sb = new StringBuilder();
            sb.append("nLocal " + nLocal);
            if (local != null) {
                for (Object object : local) {
                    sb.append(String.valueOf(object)).append(",");
                }
            }

            sb.append("nStack " + nStack);
            if (stack != null) {
                for (Object object : stack) {
                    sb.append(String.valueOf(object));
                }
            }

            Log.e("ErrorLog", "mv.visitVarInsn(ALOAD/Object, varIndex) >>> " + sb.toString());

        }
    }

    protected boolean recursiveAdvice(Target target) {
        return this.source != null && target != null &&
                StringUtil.replaceSlash2Dot(this.source.getClassName())
                        .equals(StringUtil.replaceSlash2Dot(target.getClassName()) + "$Companion");
    }
}
