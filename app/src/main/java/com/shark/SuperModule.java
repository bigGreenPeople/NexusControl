package com.shark;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.kyuubiran.ezxhelper.HookFactory;
import com.github.kyuubiran.ezxhelper.finders.MethodFinder;
import com.github.kyuubiran.ezxhelper.interfaces.IMethodHookCallback;
import com.mh.test.BuildConfig;
import com.shark.context.ContextUtils;
import com.shark.view.ViewManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class SuperModule implements IXposedHookLoadPackage {
    public static final String TAG = "SharkMod";
    public Activity currentActivity;
    public ClassLoader mClassLoader;
    public ViewManager mViewManager;
    public ContextUtils mContextUtils;


    protected String getTargetPackageName() {
        return BuildConfig.TARGET_PACKAGE;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(getTargetPackageName())) return;
        if (!lpparam.processName.equals(getTargetPackageName())) return;

        Log.i(TAG, "packageName:" + lpparam.packageName + " processName: " + lpparam.processName);
        mClassLoader = lpparam.classLoader;

        mViewManager = ViewManager.getInstance(lpparam.classLoader);
        mContextUtils = ContextUtils.getInstance(lpparam.classLoader, null);

        main(lpparam.classLoader, lpparam.processName, lpparam.packageName);
        trackActivityOnResume(lpparam.classLoader);
    }

    public void hookApplication() {

    }

    public Class findClass(String className) {
        try {
            Class<?> aClass = mClassLoader.loadClass(className);
            return aClass;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "findClass: ", e);
        }
        return null;
    }

    abstract public void main(ClassLoader classLoader, String processName, String packageName);


    /**
     * 格式化参数，防止 null 值
     */
    private static String formatArgs(Object[] args) {
        if (args == null) return "无参数";
        return Arrays.toString(args);
    }

    /**
     * 打印方法调用的参数（Before Hook）
     */
    public static void logBefore(XC_MethodHook.MethodHookParam param) {
        String log = "\n" +
                "====================================\n" +
                "[Before Hook] 方法调用\n" +
                "------------------------------------\n" +
                "📌 类名    ：" + param.method.getDeclaringClass().getName() + "\n" +
                "🔹 方法名  ：" + getMethodSignature(param) + "\n" +
                "🔸 参数    ：" + formatArgs(param.args) + "\n" +
                "====================================";
        Log.i(TAG, log);
    }

    /**
     * 打印方法返回值（After Hook）
     */
    public static void logAfter(XC_MethodHook.MethodHookParam param) {
        String log = "\n" +
                "====================================\n" +
                "[After Hook] 方法返回\n" +
                "------------------------------------\n" +
                "📌 类名    ：" + param.method.getDeclaringClass().getName() + "\n" +
                "🔹 方法名  ：" + getMethodSignature(param) + "\n" +
                "🔸 返回值  ：" + (param.getResult() != null ? param.getResult().toString() : "null") + "\n" +
                "====================================";
        Log.i(TAG, log);
    }


    public void hookBeforeClazzsMethod(IMethodHookCallback iMethodHookCallback, String... classList) {

        List<Method> allMethods = new ArrayList<>();

        for (String clazz : classList) {
            List<Method> methods = MethodFinder.fromClass(findClass(clazz)).filterNonAbstract().toList();
            allMethods.addAll(methods);
        }

        HookFactory.createMethodBeforeHooks(allMethods, iMethodHookCallback);
    }

    /**
     * 使用抛出异常的方式获取调用栈
     */
    public static String logStackTraceByException() {
        StringBuilder stackTrace = new StringBuilder();
        try {
            throw new Exception("Hook 调用栈");
        } catch (Exception e) {
            StackTraceElement[] stackElements = e.getStackTrace();
            for (int i = 1; i < stackElements.length; i++) { // 跳过自身方法，最多显示 10 层
                stackTrace.append("  ↳ ").append(stackElements[i].toString()).append("\n");
            }
        }
        Log.e(TAG, "getStackTraceByException: " + stackTrace.toString());
        return stackTrace.toString();
    }

    /**
     * 获取方法签名（带参数类型）
     * 例如：myMethod(String, int)
     */
    private static String getMethodSignature(XC_MethodHook.MethodHookParam param) {
        StringBuilder signature = new StringBuilder(param.method.getName());
        signature.append("(");

        Class<?>[] paramTypes;
        if (param.method instanceof Method) {
            paramTypes = ((Method) param.method).getParameterTypes();
        } else if (param.method instanceof Constructor) {
            paramTypes = ((Constructor<?>) param.method).getParameterTypes();
        } else {
            paramTypes = new Class<?>[0];
        }

        for (int i = 0; i < paramTypes.length; i++) {
            signature.append(paramTypes[i].getSimpleName());
            if (i < paramTypes.length - 1) {
                signature.append(", ");
            }
        }
        signature.append(")");
        return signature.toString();
    }

    public void runUi(Runnable action) {
        if (currentActivity == null) {
            Log.e(TAG, "runUi: currentActivity is null");
            return;
        }

        currentActivity.runOnUiThread(action);
    }

    public void trackFragment(ClassLoader classLoader) {
        trackFragmentCreation(classLoader, "android.app.Fragment"); // 旧版 Fragment
        trackFragmentCreation(classLoader, "androidx.fragment.app.Fragment"); // AndroidX Fragment

    }

    public void trackExecStartActivity(String activityName) {
        XposedHelpers.findAndHookMethod(
                Instrumentation.class,
                "execStartActivity",
                Context.class, IBinder.class, IBinder.class, Activity.class,
                Intent.class, int.class, Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        Intent intent = (Intent) param.args[4]; // 获取 Intent
                        if (!intent.getComponent().toString().contains(activityName)) return;
                        Log.i(TAG, "📌 Activity 启动: " + intent.getComponent());
                        Log.i(TAG, "🔹 Intent Extras: " + intent.getExtras());

                        // 打印调用堆栈
                        Log.i(TAG, "🔸 调用栈:\n" + Log.getStackTraceString(new Throwable()));
                    }
                }
        );
    }

    public void trackActivityOnResume(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Activity activity = (Activity) param.thisObject;
                currentActivity = activity;
                String activityName = activity.getClass().getName();

                String log = "\n" +
                        "====================================\n" +
                        "[Activity 切换到前台] \n" +
                        "------------------------------------\n" +
                        "📌 当前显示 Activity ：" + activityName + "\n" +
                        "------------------------------------\n" +
                        "====================================";
                Log.i(TAG, log);
            }
        });
    }

    /**
     * Hook Fragment 构造方法和 onCreate()
     */
    public void trackFragmentCreation(ClassLoader classLoader, String fragmentClass) {
        try {
            // Hook Fragment 构造方法
            XposedHelpers.findAndHookConstructor(fragmentClass, classLoader, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String className = param.thisObject.getClass().getName();
                    Log.i(TAG, "\n" +
                            "====================================\n" +
                            "[Fragment Created] \n" +
                            "📌 类名    ：" + className + "\n" +
                            "====================================");
                }
            });

            // Hook Fragment onCreate() 确保拦截所有 Fragment
            XposedHelpers.findAndHookMethod(fragmentClass, classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String className = param.thisObject.getClass().getName();
                    Log.i(TAG, "\n" +
                            "====================================\n" +
                            "[Fragment onCreate] \n" +
                            "📌 类名    ：" + className + "\n" +
                            "====================================");
                }
            });

        } catch (Exception e) {
            Log.i(TAG, "[ERROR] Hook Fragment 失败：" + e.getMessage());
        }
    }

}
