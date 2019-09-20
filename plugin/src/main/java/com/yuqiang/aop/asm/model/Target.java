package com.yuqiang.aop.asm.model;

import java.util.Objects;

/**
 * 插入的目标字节码描述信息
 * @author yuqiang
 */
public class Target {
    private String className;
    private int methodAccess;
    private String methodName;
    private String methodDesc;
    private boolean methodEnter;
    private boolean returnValue;

    @Override
    public String toString() {
        return "Target{" +
                "className='" + className + '\'' +
                ", methodAccess=" + methodAccess +
                ", methodName='" + methodName + '\'' +
                ", methodDesc='" + methodDesc + '\'' +
                ", methodEnter=" + methodEnter +
                ", returnValue=" + returnValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Target)) {
            return false;
        }
        Target target = (Target) o;
        return methodEnter == target.methodEnter &&
                returnValue == target.returnValue &&
                Objects.equals(className, target.className) &&
                Objects.equals(methodName, target.methodName) &&
                Objects.equals(methodDesc, target.methodDesc);
    }

    @Override
    public int hashCode() {

        return Objects.hash(className, methodName, methodDesc, methodEnter, returnValue);
    }

    private Target(String className, int methodAccess, String methodName, String methodDesc) {
        this.className = className;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    private Target(String className, int methodAccess, String methodName, String methodDesc, boolean methodEnter) {
        this(className, methodAccess, methodName, methodDesc);
        this.methodEnter = methodEnter;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public boolean isMethodEnter() {
        return methodEnter;
    }

    public void setMethodEnter(boolean methodEnter) {
        this.methodEnter = methodEnter;
    }


    public void setReturnValue(boolean returnValue) {
        this.returnValue = returnValue;
    }

    public boolean getReturnValue() {
        return returnValue;
    }

    public static class Builder {
        private String className;
        private int methodAccess;
        private String methodName;
        private String methodDesc;
        private boolean methodEnter;

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

        public Builder methodEnter(boolean methodEnter) {
            this.methodEnter = methodEnter;
            return this;
        }

        public Target build() {
            return new Target(className, methodAccess, methodName, methodDesc, methodEnter);
        }
    }
}
