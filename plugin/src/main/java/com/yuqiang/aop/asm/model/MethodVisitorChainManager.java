package com.yuqiang.aop.asm.model;

import com.yuqiang.aop.asm.call.ReplaceCallMethodVisitor;
import com.yuqiang.aop.asm.execute.AroundExecuteAdviceAdapter;
import com.yuqiang.aop.asm.execute.InterceptorExecuteAdviceAdapter;
import com.yuqiang.aop.asm.execute.TimingExecuteAdviceAdapter;
import com.yuqiang.aop.asm.execute.TryCatchExecuteMethodVisitor;

import org.objectweb.asm.MethodVisitor;


/**
 * @author yuqiang
 */
public class MethodVisitorChainManager {

    public static MethodVisitor createChain(MethodVisitor mv, Source source) {
        ReplaceCallMethodVisitor replace = new ReplaceCallMethodVisitor(mv, source);
        TryCatchExecuteMethodVisitor tryCatch = new TryCatchExecuteMethodVisitor(replace, source);
        InterceptorExecuteAdviceAdapter interceptor = new InterceptorExecuteAdviceAdapter(tryCatch, source);
        TimingExecuteAdviceAdapter timing = new TimingExecuteAdviceAdapter(interceptor, source);
        return new AroundExecuteAdviceAdapter(timing, source);
    }
}
