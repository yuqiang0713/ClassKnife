package com.yuqiang.classknife;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



/**
 * @author yuqiang
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private BottomNavigationView mBottomNavigationView;
    private Fragment[] mFragments;
    private SearchView mSearchView;

    public static int safeCommit(android.app.FragmentTransaction fr) {
        if (fr == null) return -1;
        return fr.commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 使用Toolbar代替actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick");
            }
        });
        setSupportActionBar(toolbar);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("name", MODE_PRIVATE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        findViewById(R.id.fab).setOnClickListener(this);
        funCall();
        testStaticFunAround(this, 2);
        testReplaceForCommit();
        start();
    }


    /**
     * 测试接口Around
     * @param v
     */
    @Override
    public void onClick(View v) {
        View view = v;
        switch (v.getId()) {
            case 1:
                Log.e(TAG, "1");
                return;
            case 2:
                Log.e(TAG, "2");
                return;
            case 3:
                Log.e(TAG, "3");
                return;
        }
        Toast.makeText(MainActivity.this, "Click TAB", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        mFragments[mFragments.length - 1].onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // 测试拦截
        super.onBackPressed();
    }

    /**
     * 测试接口的Around
     */
    public void testInterfaceAround() {
        RecyclerView recyclerView = null;
        if (recyclerView != null) {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.e(TAG, "onScrollStateChanged");
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Log.e(TAG, "onScrolled");
                }
            });
        }
    }

    /**
     * 测试函数调用替换 Replace
     */
    public void testReplaceForCommit() {
        android.support.v4.app.FragmentTransaction fragmentTransaction1 = null;
        if (fragmentTransaction1 != null) {
            fragmentTransaction1.commit();
        }

        android.app.FragmentTransaction fragmentTransaction = null;
        if (fragmentTransaction != null) {
            fragmentTransaction.commit();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "thread Name - >>> " + Thread.currentThread().getName());
            }
        });
        thread.start();

        getSharedPreferences("key", MODE_PRIVATE);


        View view = null;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer temp = 1;
                if (isFinishing()) {
                    temp = 2;
                }
                MainActivity.this.test(1, temp);
            }


        });
    }


    void test(int a, int b) {
    }


        /**
         * 测试方法的Around
         * @param activity
         * @param a
         */
    public static void testStaticFunAround(MainActivity activity, int a) {
        String name = "name";
        if (TextUtils.isEmpty(name)) {
            Log.e(TAG, "with value");
        }
    }

    /*********************函数调用堆栈************/
    private void funCall() {
        firstStack();
        testInterfaceAround();
    }

    private void firstStack(){
        secondStack();
    }

    private void secondStack() {
        thirdStack();
    }

    private void thirdStack() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*******************************************/
}
