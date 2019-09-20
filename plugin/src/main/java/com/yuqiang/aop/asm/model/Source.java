package com.yuqiang.aop.asm.model;

import com.yuqiang.aop.util.ClassUtil;
import com.yuqiang.aop.util.RegexUtil;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author yuqiang
 */
public class Source {
    private String className;
    private int methodAccess;
    private String methodName;
    private String methodDesc;
    private boolean extend;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Source)) {
            return false;
        }
        Source source = (Source) o;
        boolean isMatch = true;
        if (source.className.contains(RegexUtil.REG_STAR) || className.contains(RegexUtil.REG_STAR)) {
            isMatch &= Pattern.matches(source.className, className);
        } else {
            isMatch &= Objects.equals(source.className, className);
        }
        if (source.methodName.contains(RegexUtil.REG_STAR) || methodName.contains(RegexUtil.REG_STAR)) {
            isMatch &= Pattern.matches(source.methodName, methodName);
        } else {
            isMatch &= Objects.equals(source.methodName, methodName);
        }
        if (source.methodDesc.contains(RegexUtil.REG_STAR) || methodDesc.contains(RegexUtil.REG_STAR)) {
            isMatch &= Pattern.matches(source.methodDesc, methodDesc);
        } else {
            isMatch &= Objects.equals(source.methodDesc, methodDesc);
        }
        isMatch = isMatch & Objects.equals(annotationDesc, source.annotationDesc);
        if (isMatch) {
            return true;
        }
        if (extend && source.extend) {
            return Objects.equals(methodName, source.methodName) &&
                    Objects.equals(methodDesc, source.methodDesc) &&
                    Objects.equals(annotationDesc, source.annotationDesc) &&
                    ClassUtil.isSuper(className.replace("/", "."),
                            source.className.replace("/", "."));
        }
        return
                Objects.equals(className.replace("/", "."),
                        source.className.replace("/", ".")) &&
                        Objects.equals(methodName, source.methodName) &&
                        Objects.equals(methodDesc, source.methodDesc) &&
                        Objects.equals(annotationDesc, source.annotationDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotationDesc);
    }

    @Override
    public String toString() {
        return "Source{" +
                "className='" + className + '\'' +
                ", methodAccess=" + methodAccess +
                ", methodName='" + methodName + '\'' +
                ", methodDesc='" + methodDesc + '\'' +
                ", extend=" + extend +
                ", annotationDesc='" + annotationDesc + '\'' +
                '}';
    }

    private String annotationDesc;

    private Source(String className, int methodAccess, String methodName, String methodDesc, boolean extend, String annotationDesc) {
        this.className = className;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.extend = extend;
        this.annotationDesc = annotationDesc;
    }

    public String getClassName() {
        return className;
    }

    public int getMethodAccess() {
        return methodAccess;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setExtend(boolean extend) {
        this.extend = extend;
    }

    public void setAnnotationDesc(String annotationDesc) {
        this.annotationDesc = annotationDesc;
    }

    public static class Builder {
        private String className;
        private int methodAccess;
        private String methodName;
        private String methodDesc;
        private boolean extend;
        private String annotationDesc;

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder methodAccess(int methodAccess) {
            this.methodAccess = methodAccess;
            return this;
        }

        public Builder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder methodDesc(String methodDesc) {
            this.methodDesc = methodDesc;
            return this;
        }

        public Builder extend(boolean extend) {
            this.extend = extend;
            return this;
        }

        public Builder annotationDesc(String annotationDesc) {
            this.annotationDesc = annotationDesc;
            return this;
        }

        public Source build() {
            return new Source(className, methodAccess, methodName, methodDesc, extend, annotationDesc);
        }
    }
}
