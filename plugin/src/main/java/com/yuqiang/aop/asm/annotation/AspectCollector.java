package com.yuqiang.aop.asm.annotation;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.yuqiang.aop.util.ClassUtil;
import com.yuqiang.aop.util.FileUtil;
import com.yuqiang.aop.util.Log;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 注解扫描处理器 {@link com.yuqiang.aop.annotations.Aspect}
 * @author yuqiang
 */
public class AspectCollector {
    private static final String TAG = "AspectCollector";

    private final ExecutorService executor;

    public AspectCollector(ExecutorService executor) {
        this.executor = executor;
    }

    public void collect(TransformInvocation transformInvocation) throws ExecutionException, InterruptedException {
        List<Future> futures = new LinkedList<>();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            //dir
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dirInput = directoryInput.getFile();
                if (dirInput.isDirectory()) {
                    for (File classFile : com.android.utils.FileUtils.getAllFiles(dirInput)) {
                        futures.add(executor.submit(new CollectSrcTask(classFile)));
                    }
                } else {
                    futures.add(executor.submit(new CollectSrcTask(dirInput)));
                }
            }
            //jar
            for (JarInput inputJar : input.getJarInputs()) {
                futures.add(executor.submit(new CollectJarTask(inputJar.getFile())));
            }
        }

        for (Future future : futures) {
            future.get();
        }
        futures.clear();
    }


    class CollectSrcTask implements Runnable {

        File classFile;

        CollectSrcTask(File classFile) {
            this.classFile = classFile;
        }

        @Override
        public void run() {
            InputStream is = null;
            try {
                if (ClassUtil.filter(classFile.getAbsolutePath())) {
                    is = new FileInputStream(classFile);
                    ClassReader classReader = new ClassReader(is);
                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    ClassVisitor visitor = new AspectClassVisitor(classWriter);
                    classReader.accept(visitor, 0);
                }
            } catch (Exception e) {
                Log.e(TAG, " Src error : " + e.getMessage());
                e.printStackTrace();
            } finally {
                FileUtil.closeQuietly(is);
            }
        }
    }

    class CollectJarTask implements Runnable {

        File fromJar;

        CollectJarTask(File jarFile) {
            this.fromJar = jarFile;
        }

        @Override
        public void run() {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(fromJar);
                Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                while (enumeration.hasMoreElements()) {
                    ZipEntry zipEntry = enumeration.nextElement();
                    String zipEntryName = zipEntry.getName();
                    if (ClassUtil.filter(zipEntryName)) {
                        InputStream inputStream = zipFile.getInputStream(zipEntry);
                        ClassReader classReader = new ClassReader(inputStream);
                        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                        ClassVisitor visitor = new AspectClassVisitor(classWriter);
                        classReader.accept(visitor, 0);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, " Jar error : " + e.getMessage());
                e.printStackTrace();
            } finally {
                FileUtil.closeQuietly(zipFile);
            }
        }
    }
}
