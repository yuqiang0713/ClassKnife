package com.yuqiang.aop.asm.call;

import com.yuqiang.aop.annotations.Replace;
import com.yuqiang.aop.ClassMethodKnife;
import com.yuqiang.aop.util.ClassUtil;
import com.yuqiang.aop.util.Log;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;
import com.yuqiang.aop.util.StringUtil;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Objects;

/**
 * {@link com.yuqiang.aop.annotations.Replace}
 * @author yuqiang
 */
public class ReplaceCallMethodVisitor extends AbstractCallMethodVisitor {

    public ReplaceCallMethodVisitor(MethodVisitor mv, Source source) {
        super(mv, source);
    }

    @Override
    protected void innerCallMethodInst(CallMethodInst callMethodInst, Target target) {
        if (recursiveReplace(target)) {
            mv.visitMethodInsn(callMethodInst.opcode, callMethodInst.owner, callMethodInst.name, callMethodInst.desc, callMethodInst.itf);
            Log.e(getTag(), "recursive call occurs stackOverflowException discard!");
            return;
        }
        int sourceParamCount = 0;
        int targetParamCount = 0;
        Type[] sourceParamTypes = Type.getArgumentTypes(callMethodInst.desc);
        if (sourceParamTypes != null) {
            sourceParamCount = sourceParamTypes.length;
        }
        Type[] targetParamTypes = Type.getArgumentTypes(target.getMethodDesc());
        if (targetParamTypes != null) {
            targetParamCount = targetParamTypes.length;
        }

        //虚方法
        if (callMethodInst.opcode == INVOKEVIRTUAL && (sourceParamCount + 1 != targetParamCount)) {
            throw new RuntimeException("replace method exception : The replacement function must has one more parameter than the original function");
        }
        //虚方法类型匹配
        if (targetParamCount > 0 && callMethodInst.opcode == INVOKEVIRTUAL) {
            String ownerClassName = callMethodInst.owner;
            Type type = targetParamTypes[0];
            String firstParamClassName = type.getClassName();
            String msg = firstParamClassName + " is a subclass of " + ownerClassName + " or itself! ";
            if (!ClassUtil.isSuper(ownerClassName, firstParamClassName)) {
                Log.e(getTag(), msg);
                throw new RuntimeException(msg);
            }
        }

        if (callMethodInst.opcode == INVOKESTATIC && (sourceParamCount != targetParamCount)) {
            throw new RuntimeException("replace method exception : The replacement function must has the same list of parameters as the original function.");
        }
        Log.i(getTag(), source.getClassName() + "." + source.getMethodName() + "." + source.getMethodDesc() + String.format("%s.%s.%s replace by %s.%s.%s",
                callMethodInst.owner, callMethodInst.name, callMethodInst.desc, target.getClassName(), target.getMethodName(), target.getMethodDesc()));
        mv.visitMethodInsn(INVOKESTATIC, target.getClassName(), target.getMethodName(), target.getMethodDesc(), false);
    }

    /**
     * {@link ClassMethodKnife.KnifeClassAdapter#visitAnnotation(String, boolean)}
     * 扫描类掉时候已经排除掉
     * 消除递归替换导致的 StackOverflowException
     * Kotlin注解需要调用该方法
     *          Source.className = *.$Companion
     *          Target.className = *
     *       so source.className.equals(target.className + "$Companion")
     * @param target 目标指令信息
     * @return       true   discard
     *               false
     */
    private boolean recursiveReplace(Target target) {
        return this.source != null && target != null &&
                StringUtil.replaceSlash2Dot(this.source.getClassName())
                        .equals(StringUtil.replaceSlash2Dot(target.getClassName()) + "$Companion") &&
                Objects.equals(this.source.getMethodName(), target.getMethodName()) &&
                Objects.equals(this.source.getMethodDesc(), target.getMethodDesc());
    }

    @Override
    protected Class getAnnotationClass() {
        return Replace.class;
    }

    @Override
    protected String getTag() {
        return "Replace";
    }
}
