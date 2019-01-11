package com.chuchujie.apt_library;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

public class BindViewTools {

    public static void bind(Activity activity) {

        Class mClass = activity.getClass();

        try {
            Class bindViewClass = Class.forName(mClass.getName() + "_ViewBinding");
            Method method = bindViewClass.getMethod("bind", activity.getClass());
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
