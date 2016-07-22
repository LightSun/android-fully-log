package com.heaven7.android.log;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import java.lang.reflect.Method;

/**
 * Created by heaven7 on 2016/7/21.
 */
 class BinderCompatUtil {

    private static final IBinderCompat sBinderCompat;

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            sBinderCompat = new BinderCompat_18();
        } else {
            sBinderCompat = new BinderCompat_Below18();
        }
    }

    public static void putBinder(Bundle b, String key, IBinder binder){
        sBinderCompat.putBinder(b, key, binder);
    }
    public static IBinder getBinder(Bundle b, String key){
        return sBinderCompat.getBinder(b, key);
    }

    interface IBinderCompat{
        void putBinder(Bundle b, String key, IBinder binder);
        IBinder getBinder(Bundle b, String key);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static class BinderCompat_18 implements IBinderCompat{
        @Override
        public void putBinder(Bundle b, String key, IBinder binder) {
            b.putBinder(key, binder);
        }
        @Override
        public IBinder getBinder(Bundle b, String key) {
            return b.getBinder(key);
        }
    }
    private static class BinderCompat_Below18 implements IBinderCompat{
        static Method sMethod_putIBinder;
        static Method sMethod_getIBinder;
        static{
            try {
                sMethod_putIBinder = Bundle.class.getDeclaredMethod("putIBinder",String.class, IBinder.class);
                sMethod_getIBinder = Bundle.class.getDeclaredMethod("getIBinder",String.class);
                sMethod_putIBinder.setAccessible(true);
                sMethod_getIBinder.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void putBinder(Bundle b, String key, IBinder binder) {
            try {
                sMethod_putIBinder.invoke(b, key, binder );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public IBinder getBinder(Bundle b, String key) {
            try {
                return (IBinder) sMethod_getIBinder.invoke(b,key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
