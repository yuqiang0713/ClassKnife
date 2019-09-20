package com.yuqiang.aop.asm.annotation;

import com.yuqiang.aop.annotations.Around;
import com.yuqiang.aop.annotations.Aspect;
import com.yuqiang.aop.annotations.Interceptor;
import com.yuqiang.aop.annotations.Replace;
import com.yuqiang.aop.annotations.Timing;
import com.yuqiang.aop.annotations.TryCatch;
import com.yuqiang.aop.asm.model.Target;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 注解ClassVisitor {@link com.yuqiang.aop.annotations.Aspect}
 * @author yuqiang
 */
public class AspectClassVisitor extends ClassVisitor {

    private boolean isAspectClass = false;
    private String className;

    AspectClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        isAspectClass = Type.getType(Aspect.class).getDescriptor().equals(desc);
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (isAspectClass) {
            Target target = new Target.Builder()
                    .className(className)
                    .methodAccess(access)
                    .methodName(name)
                    .methodDesc(desc)
                    .build();
            boolean isStatic = (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
            return new AspectAnnotationMethodVisitor(target, isStatic);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    class AspectAnnotationMethodVisitor extends MethodVisitor {

        Target target;
        boolean isStatic;

        private AspectAnnotationMethodVisitor(Target target, boolean isStatic) {
            super(Opcodes.ASM5);
            this.target = target;
            this.isStatic = isStatic;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (!canVisit(isStatic, desc)) {
                throw new RuntimeException("Method with classKnife annotation must be static(if in kotlin code should add @JvmStatic), Please check "
                        + target.getClassName() + "." + target.getMethodName() + "." + target.getMethodDesc());
            }
            return new AspectAnnotationVisitor(desc, target);
        }

        private boolean canVisit(boolean isStatic, String desc) {
            if (isStatic) {
                return true;
            }

            return !Type.getType(Around.class).getDescriptor().equals(desc) &&
                    !Type.getType(TryCatch.class).getDescriptor().equals(desc) &&
                    !Type.getType(Interceptor.class).getDescriptor().equals(desc) &&
                    !Type.getType(Replace.class).getDescriptor().equals(desc) &&
                    !Type.getType(Timing.class).getDescriptor().equals(desc);
        }
    }
}
