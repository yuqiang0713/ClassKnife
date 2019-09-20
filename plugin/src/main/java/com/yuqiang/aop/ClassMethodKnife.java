package com.yuqiang.aop;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.yuqiang.aop.annotations.Aspect;
import com.yuqiang.aop.annotations.Ignore;
import com.yuqiang.aop.asm.model.MethodVisitorChainManager;
import com.yuqiang.aop.asm.model.Source;
import com.yuqiang.aop.util.ClassUtil;
import com.yuqiang.aop.util.FileUtil;
import com.yuqiang.aop.util.Log;
import com.yuqiang.aop.util.StringUtil;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;



/**
 * @author yuqiang
 */
public class ClassMethodKnife {
    private static final String TAG = "ClassMethodKnife";
    private final ExecutorService executor;
    private final Configuration configuration;

    public ClassMethodKnife(ExecutorService executor, Configuration configuration) {
        this.executor = executor;
        this.configuration = configuration;
    }

    public void knife(TransformInvocation transformInvocation) throws ExecutionException, InterruptedException, IOException {
        List<Future> futures = Collections.synchronizedList(new LinkedList<Future>());
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            //dir
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                final File dirInput = directoryInput.getFile();
                final File dest = transformInvocation.getOutputProvider().getContentLocation(
                        directoryInput.getFile().getAbsolutePath(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                FileUtils.forceMkdir(dest);
                futures.add(executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        innerKnifeMethodFromSrc(dirInput, dest);
                    }
                }));
            }
            //jar
            for (final JarInput inputJar : input.getJarInputs()) {
                final File dest = transformInvocation.getOutputProvider().getContentLocation(inputJar.getFile().getAbsolutePath(),
                        inputJar.getContentTypes(),
                        inputJar.getScopes(),
                        Format.JAR);
                futures.add(executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        innerKnifeMethodFromJar(inputJar.getFile(), dest);
                    }
                }));
            }
        }

        for (Future future : futures) {
            future.get();
        }
        futures.clear();
    }

    private void innerKnifeMethodFromSrc(File input, File output) {
        ArrayList<File> classFileList = new ArrayList<>();
        if (input.isDirectory()) {
            listClassFiles(classFileList, input);
        } else {
            classFileList.add(input);
        }

        for (File classFile : classFileList) {
            InputStream is = null;
            FileOutputStream os = null;
            try {
                final String changedFileInputFullPath = classFile.getAbsolutePath();
                final File changedFileOutput = new File(changedFileInputFullPath.replace(input.getAbsolutePath(), output.getAbsolutePath()));
                if (!changedFileOutput.exists()) {
                    changedFileOutput.getParentFile().mkdirs();
                }
                changedFileOutput.createNewFile();
                if (ClassUtil.filter(classFile.getAbsolutePath(), configuration)) {
                    is = new FileInputStream(classFile);
                    ClassReader classReader = new ClassReader(is);
                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    ClassVisitor classVisitor = new KnifeClassAdapter(Opcodes.ASM5, classWriter);
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
                    is.close();

                    if (output.isDirectory()) {
                        os = new FileOutputStream(changedFileOutput);
                    } else {
                        os = new FileOutputStream(output);
                    }
                    os.write(classWriter.toByteArray());
                    os.close();
                } else {
                    FileUtil.copyFileUsingStream(classFile, changedFileOutput);
                }
            } catch (Exception e) {
                Log.e(TAG, "[innerKnifeMethodFromSrc] input:%s e:%s", input.getName(), e);
                try {
                    Files.copy(input.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } finally {
                FileUtil.closeQuietly(is);
                FileUtil.closeQuietly(os);
            }
        }
    }

    private void innerKnifeMethodFromJar(File input, File output) {
        ZipOutputStream zipOutputStream = null;
        ZipFile zipFile = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(output));
            zipFile = new ZipFile(input);
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = enumeration.nextElement();
                String zipEntryName = zipEntry.getName();
                if (ClassUtil.filter(zipEntryName, configuration)) {
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    ClassReader classReader = new ClassReader(inputStream);
                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    ClassVisitor classVisitor = new KnifeClassAdapter(Opcodes.ASM5, classWriter);
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
                    byte[] data = classWriter.toByteArray();
                    InputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    ZipEntry newZipEntry = new ZipEntry(zipEntryName);
                    FileUtil.addZipEntry(zipOutputStream, newZipEntry, byteArrayInputStream);
                } else {
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    ZipEntry newZipEntry = new ZipEntry(zipEntryName);
                    FileUtil.addZipEntry(zipOutputStream, newZipEntry, inputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "[innerKnifeMethodFromJar] input:%s output:%s e:%s", input.getName(), output, e);
            try {
                Files.copy(input.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.finish();
                    zipOutputStream.flush();
                    zipOutputStream.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "close stream err!");
            }
        }
    }

    private void listClassFiles(ArrayList<File> classFiles, File folder) {
        File[] files = folder.listFiles();
        if (null == files) {
            Log.e(TAG, "[listClassFiles] files is null! %s", folder.getAbsolutePath());
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (file.isDirectory()) {
                listClassFiles(classFiles, file);
            } else {
                if (file.isFile()) {
                    classFiles.add(file);
                }
            }
        }
    }

    private class KnifeClassAdapter extends ClassVisitor {

        private String className;
        private boolean isAspectClass;

        KnifeClassAdapter(int i, ClassVisitor classVisitor) {
            super(i, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.className = name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            isAspectClass = Type.getType(Aspect.class).getDescriptor().equals(desc) || Type.getType(Ignore.class).getDescriptor().equals(desc);
            return super.visitAnnotation(desc, visible);
        }


        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc,
                                         String signature, String[] exceptions) {
            if (isAspectClass) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            Source source = new Source.Builder()
                    .className(StringUtil.replaceSlash2Dot(className))
                    .methodAccess(access)
                    .methodName(name)
                    .methodDesc(desc)
                    .extend(true)
                    .build();
            return MethodVisitorChainManager.createChain(methodVisitor, source);

        }
    }
}
