package com.yuqiang.aop.extension;

import com.yuqiang.aop.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link Configuration}
 * @author yuqiang
 */
public class ClassKnifeExtension {
    private boolean enable;
    private List<String> includes = new ArrayList<>();
    private List<String> excludes = new ArrayList<>();

    public ClassKnifeExtension() {
        enable = true;
    }

    public ClassKnifeExtension exclude(String...filters) {
        if (filters != null) {
            this.excludes.addAll(Arrays.asList(filters));
        }
        return this;
    }

    public ClassKnifeExtension include(String...filters) {
        if (filters != null) {
            this.includes.addAll(Arrays.asList(filters));
        }
        return this;
    }

    public List<String> getExcludes() {
        return this.excludes;
    }

    public List<String> getIncludes() {
        return this.includes;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}