package com.yuqiang.aop.asm.call;

import com.yuqiang.aop.asm.api.ICall;
import com.yuqiang.aop.asm.model.RuleConfigManager;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;
import com.yuqiang.aop.util.StringUtil;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

/**
 * @author yuqiang
 */
public abstract class AbstractCallMethodVisitor extends MethodVisitor implements Opcodes, ICall {

    protected Source source;
    protected List<Target> targetList;


    public AbstractCallMethodVisitor(MethodVisitor mv, Source source) {
        super(ASM5, mv);
        this.source = source;
    }

    @Override
    public final synchronized List<Target> findTarget(Source source) {
        source.setAnnotationDesc(Type.getType(getAnnotationClass()).getDescriptor());
        return RuleConfigManager.getInstance().findTarget(source);
    }

    /**
     * 当前处理的注解Class
     * @return class
     */
    protected abstract Class getAnnotationClass();

    /**
     * 当前处理的注解tag
     * @return tag
     */
    protected abstract String getTag();

    /**
     * 是否要执行字节码指令
     * @return  true    执行
     *          false   不执行
     */
    protected boolean runCallMethodInst() {
        return true;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        Source source = new Source.Builder()
                .className(StringUtil.replaceSlash2Dot(owner))
                .methodName(name)
                .annotationDesc(Type.getType(getAnnotationClass()).getDescriptor())
                .extend(true)
                .methodDesc(desc)
                .build();
        targetList = findTarget(source);
        if (runCallMethodInst() && targetList != null && targetList.size() == 1) {
            innerCallMethodInst(CallMethodInst.createMethodInst(opcode, owner, name, desc, itf), targetList.get(0));
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    /**
     * {@link org.objectweb.asm.MethodVisitor#visitMethodInsn(int, String, String, String, boolean)}
     * @param callMethodInst    source MethodInst
     * @param target            target MethodInst
     */
    protected abstract void innerCallMethodInst(CallMethodInst callMethodInst, Target target);

    public static class CallMethodInst {
        public int opcode;
        public String owner;
        public String name;
        public String desc;
        public boolean itf;

        @Override
        public String toString() {
            return "CallMethodInst{" +
                    "opcode=" + opcode +
                    ", owner='" + owner + '\'' +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", itf=" + itf +
                    '}';
        }

        private CallMethodInst(int opcode, String owner, String name, String desc, boolean itf) {
            this.opcode = opcode;
            this.owner = owner;
            this.name = name;
            this.desc = desc;
            this.itf = itf;
        }

        private static CallMethodInst createMethodInst(int opcode, String owner, String name, String desc, boolean itf) {
            return new CallMethodInst(opcode, owner, name, desc, itf);
        }
    }
}
