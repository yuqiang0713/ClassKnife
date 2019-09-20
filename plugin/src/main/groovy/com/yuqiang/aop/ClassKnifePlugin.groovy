package com.yuqiang.aop

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.yuqiang.aop.extension.ClassKnifeExtension
import com.yuqiang.aop.transform.ClassKnifeTransform
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin entrance
 * @author yuqiang
 */
class ClassKnifePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("classKnife", ClassKnifeExtension)
        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException('ClassKnifePlugin Plugin, Android Application plugin required')
        }
        if (project.plugins.hasPlugin(AppPlugin.class)) {
            AppExtension extension = project.extensions.getByType(AppExtension)
            extension.registerTransform(new ClassKnifeTransform(project))
        }
    }
}
