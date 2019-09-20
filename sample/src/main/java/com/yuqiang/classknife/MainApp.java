package com.yuqiang.classknife;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author yuqiang
 */
public class MainApp extends Application {

    @Override
    public void onCreate() {
        String str = "abc" + "ef" + "ddd";
        super.onCreate();
        List<Activity> list = new ArrayList<>();
        list.add(new MainActivity());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        List<? super Fruit> list = new ArrayList<>();
        list.add(new Fruit());
        list.add(new Apple());
        list.add(new Banana());


        P<Apple> p = new P<>();

        // 频繁往外读取的 使用extends
        // 频繁往里插入的 使用super
        BlockingDeque blockingDeque = new LinkedBlockingDeque();
    }

    class Food {
    }

    class Fruit extends Food {
    }

    class Meat extends Food {
    }

    class Apple extends Fruit {
    }

    class Banana extends Fruit {
    }

    class Pork extends Meat {
    }

    class Beef extends Meat {
    }

    class P<T extends Fruit> {
    }
}
