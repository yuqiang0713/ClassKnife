package com.yuqiang.aop.asm.annotation;

import com.yuqiang.aop.asm.model.RuleConfigManager;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.asm.model.Target;
import com.yuqiang.aop.util.ClassUtil;
import com.yuqiang.aop.util.RegexUtil;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yuqiang
 */
public class AspectAnnotationVisitor extends AnnotationVisitor implements Opcodes {
    /**
     * {@link com.yuqiang.aop.annotations.Around#enter()}
     * {@link com.yuqiang.aop.annotations.Timing#enter()}
     */
    private static final String ANNOTATION_ENTER = "enter";

    /**
     * {@link com.yuqiang.aop.annotations.TryCatch#extend()}
     * {@link com.yuqiang.aop.annotations.Interceptor#extend()}
     * {@link com.yuqiang.aop.annotations.Around#extend()}
     * {@link com.yuqiang.aop.annotations.Timing#extend()}
     * {@link com.yuqiang.aop.annotations.Replace#extend()}
     */
    private static final String ANNOTATION_EXTEND = "extend";

    /**
     * {@link com.yuqiang.aop.annotations.Interceptor#returnValue()}
     */
    private static final String ANNOTATION_RETURN_VALUE = "returnValue";

    final private List<Source> sourceArray;
    final private String annotationDesc;
    final private Target target;

    private boolean methodEnter = true;
    private boolean extend = false;
    private boolean returnValue = false;

    AspectAnnotationVisitor(String annotationDesc,  Target target) {
        super(ASM5);
        this.annotationDesc = annotationDesc;
        this.target = target;
        sourceArray = new CopyOnWriteArrayList<>();
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor av = super.visitArray(name);
        return new ArrayAnnotationVisitor(av, name);
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
        if (ANNOTATION_ENTER.equals(name)) {
            methodEnter = (boolean) value;
        }
        if (ANNOTATION_EXTEND.equals(name)) {
            extend = (boolean) value;
        }
        if (ANNOTATION_RETURN_VALUE.equals(name)) {
            returnValue = (boolean) value;
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        for (Source source : sourceArray) {
            //for source
            source.setExtend(extend);
            source.setAnnotationDesc(annotationDesc);
            //for target
            target.setMethodEnter(methodEnter);
            target.setReturnValue(returnValue);
            RuleConfigManager.getInstance().add(source, target);
        }
    }

    class ArrayAnnotationVisitor extends AnnotationVisitor {

        final String name;
        private ArrayAnnotationVisitor(AnnotationVisitor av, String name) {
            super(Opcodes.ASM5, av);
            this.name = name;
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            String propValue = String.valueOf(value);
            String className = propValue.substring(0, propValue.lastIndexOf("."));
            String methodName = propValue.substring(propValue.lastIndexOf(".") + 1, propValue.indexOf("("));
            String methodDesc = propValue.substring(propValue.indexOf("("), propValue.length());

            if (!ClassUtil.isMethodExist(propValue, className, methodName, methodDesc)) {
                throw new RuntimeException(propValue + " is not exists !");
            }
            Source source = new Source.Builder()
                    .className(RegexUtil.replace(className))
                    .methodName(RegexUtil.replace(methodName))
                    .methodDesc(RegexUtil.replace(methodDesc))
                    .build();
            if (!sourceArray.contains(source)) {
                sourceArray.add(source);
            }
        }
    }
}
