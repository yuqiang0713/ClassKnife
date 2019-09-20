package com.yuqiang.classknife;

public class Sub {

    public Sub() {}

    public Sub(String name) {
        name = name.replace("/", ".");
    }

    public Sub(int val) {
        val += 100;
    }
}
