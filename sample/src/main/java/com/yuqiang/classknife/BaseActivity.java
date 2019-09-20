package com.yuqiang.classknife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;


public class BaseActivity extends AppCompatActivity {

    static {
        int a = 2;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 测试TryCatch
     */
    public void start() {
        android.app.FragmentTransaction fragmentTransaction = null;
        if (fragmentTransaction != null) {
            fragmentTransaction.commit();
        }
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
    }
}
