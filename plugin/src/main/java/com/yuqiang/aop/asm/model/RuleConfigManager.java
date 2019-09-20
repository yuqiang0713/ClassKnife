package com.yuqiang.aop.asm.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yuqiang
 */
public final class RuleConfigManager {

    private Map<Source, List<Target>> mConfig = new ConcurrentHashMap<>();

    private RuleConfigManager() {}

    private static class RuleConfigManagerHolder {
        private static RuleConfigManager INSTANCE = new RuleConfigManager();
    }

    public static RuleConfigManager getInstance() {
        return RuleConfigManagerHolder.INSTANCE;
    }

    public synchronized void add(Source key, Target value) {
        List<Target> set;
        if (mConfig.containsKey(key)) {
            set = mConfig.get(key);
        } else {
            set = new CopyOnWriteArrayList<>();
            mConfig.put(key, set);
        }
        if (!set.contains(value)) {
            set.add(value);
        }
    }

    public List<Target> findTarget(Source key) {
        return mConfig.get(key);
    }

    public Map<Source, List<Target>> getConfig() {
        return mConfig;
    }

    public void clear() {
        mConfig.clear();
    }
}
