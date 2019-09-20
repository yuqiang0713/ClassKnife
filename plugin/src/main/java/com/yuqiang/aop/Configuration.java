package com.yuqiang.aop;

import java.util.List;

/**
 * classKnife configuration
 * {@link com.yuqiang.aop.extension.ClassKnifeExtension}
 * @author yuqiang
 */
public class Configuration {

    public boolean enable;
    public List<String> exclude;
    public List<String> include;

    Configuration(boolean enable, List<String> include, List<String> exclude) {
        this.enable = enable;
        this.include = include;
        this.exclude = exclude;
    }

    public static class Builder {
        private boolean enable;
        private List<String> exclude;
        private List<String> include;

        public Builder setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public Builder setExclude(List<String> exclude) {
            this.exclude = exclude;
            return this;
        }

        public Builder setInclude(List<String> include) {
            this.include = include;
            return this;
        }


        public Configuration build() {
            return new Configuration(enable, include, exclude);
        }

    }
}
