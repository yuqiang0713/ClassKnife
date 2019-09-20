package com.yuqiang.aop.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.gson.Gson;
import com.yuqiang.aop.ClassMethodKnife;
import com.yuqiang.aop.Configuration;
import com.yuqiang.aop.asm.annotation.AspectCollector;
import com.yuqiang.aop.asm.model.RuleConfigManager;
import com.yuqiang.aop.extension.ClassKnifeExtension;
import com.yuqiang.aop.util.ClassLoaderHelper;
import com.yuqiang.aop.util.ClassUtil;
import com.yuqiang.aop.util.FileUtil;
import com.yuqiang.aop.util.Log;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassKnife Transform
 * @author yuqiang
 */
public class ClassKnifeTransform extends Transform {

    private static final String TAG = "classKnife";
    private Configuration config;

    private ExecutorService executor = Executors.newFixedThreadPool(16);
    private Project project;

    public ClassKnifeTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws InterruptedException, IOException {
        ClassKnifeExtension extension = project.getExtensions().getByType(ClassKnifeExtension.class);
        // If classKnife is disable, just copy the input folder to the output folder
        if (!extension.isEnable()) {
            copyFile(transformInvocation);
            return;
        }
        config = new Configuration.Builder()
                .setEnable(extension.isEnable())
                .setExclude(extension.getExcludes())
                .setInclude(extension.getIncludes())
                .build();
        //init classLoader
        ClassUtil.initClassLoader(ClassLoaderHelper.getClassLoader(transformInvocation.getInputs(),
                transformInvocation.getReferencedInputs(),
                project));
        long start = System.currentTimeMillis();
        try {
            doTransform(transformInvocation);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        long cost = System.currentTimeMillis() - start;
        Log.i(getName(), "[transform] ClassKnifeTransform:%sms", cost);
    }

    private void doTransform(final TransformInvocation transformInvocation) throws ExecutionException, InterruptedException, IOException {
        //Delete output folder and reprocess files, whether or not incremental compilation
        transformInvocation.getOutputProvider().deleteAll();
        Log.setLogFile(project.file(project.getBuildDir() + "/outputs/classKnife/classKnife.txt"));

        long start = System.currentTimeMillis();
        RuleConfigManager.getInstance().clear();
        // Step 1
        AspectCollector aspectCollector = new AspectCollector(executor);
        aspectCollector.collect(transformInvocation);
        //print rule
        File file = project.file(project.getBuildDir() + "/outputs/classKnife/rule.json");
        String json = new Gson().toJson(RuleConfigManager.getInstance().getConfig());
        FileUtil.writeMsgToFile(file, json);
        Log.i(TAG, "[doTransform] AspectCollector cost:%sms", System.currentTimeMillis() - start);
        //Step 2
        start = System.currentTimeMillis();
        ClassMethodKnife methodTracer = new ClassMethodKnife(executor, config);
        methodTracer.knife(transformInvocation);
        Log.i(TAG, "[doTransform] ClassKnife cost:%sms", System.currentTimeMillis() - start);
    }

    private void copyFile(TransformInvocation transformInvocation) throws IOException {
        transformInvocation.getOutputProvider().deleteAll();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            //dir
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File src = directoryInput.getFile();
                File dest = transformInvocation.getOutputProvider().getContentLocation(
                        directoryInput.getFile().getAbsolutePath(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                FileUtils.forceMkdir(dest);
                FileUtil.copyFileOrDir(src, dest);
            }
            //jar
            for (JarInput inputJar : input.getJarInputs()) {
                File src = inputJar.getFile();
                File dest = transformInvocation.getOutputProvider().getContentLocation(inputJar.getFile().getAbsolutePath(),
                        inputJar.getContentTypes(),
                        inputJar.getScopes(),
                        Format.JAR);
                FileUtil.copyFileOrDir(src, dest);
            }
        }
    }
}
