package com.yuqiang.sampling;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


import com.yuqiang.aop.annotations.Ignore;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Date : 2019/8/5
 * Time : 3:07 PM
 *
 * @author : yuqiang
 */
@Ignore
public class Samping extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType( Uri uri) {
        return null;
    }

    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        return null;
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        return 0;
    }

    @Override
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i < args.length; i++) {
            stringBuffer.append(args[i]).append(".");
        }
        String className = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
        SampingUtil.getStack(className, writer);
    }
}