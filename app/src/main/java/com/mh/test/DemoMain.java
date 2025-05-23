package com.mh.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.github.kyuubiran.ezxhelper.HookFactory;
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder;
import com.github.kyuubiran.ezxhelper.finders.MethodFinder;
import com.github.kyuubiran.ezxhelper.interfaces.IMethodHookCallback;
import com.google.gson.Gson;
import com.shark.SuperModule;
import com.shark.ViewModule;
import com.shark.context.ContextUtils;
import com.shark.input.InputManager;
import com.shark.signal.IRecvListener;
import com.shark.socket.JWebSocketClient;
import com.shark.socket.WebSocketMessage;
import com.shark.tools.ScreenShot;
import com.shark.utils.LogUtils;
import com.shark.utils.ThreadUtils;
import com.shark.view.ViewInfo;
import com.shark.view.ViewManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class DemoMain extends ViewModule implements IRecvListener {
    public static List<Object> sessionList = new ArrayList<>();
    public static Object lastSession;

    @Override
    public void main(ClassLoader classLoader, String processName, String packageName) {
//        trackActivityOnResume(classLoader);
//        trackFragment(classLoader);

        // 106.13.210.172:80

        Log.i(TAG, "main: DemoMain");
//        InputManager inputManager = InputManager.getInstance();
//        inputManager.inputText("Hello NexusControl!!");
//        InputManager.getInstance().swipe(241, 97, 235, 541);2263
//        InputManager.getInstance().touchHold(972, 127);

//        if (ThreadUtils.isMainThread()) {
//            // 当前是 UI 线程
//            Log.d(TAG, "Running on the main thread.");
//        } else {
//            // 当前不是 UI 线程
//            Log.d(TAG, "Not running on the main thread.");
//        }
//

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

//            List<Method> methods = MethodFinder.fromClass(findClass("d3r")).filterNonAbstract().toList();
//
//
//            HookFactory.createMethodBeforeHooks(methods, methodHookParam -> {
//                Log.i(TAG, "d3r: " + methodHookParam.thisObject);
//                logBefore(methodHookParam);
//            });

          /*  List<Constructor<?>> d3r = ConstructorFinder.fromClass(findClass("d3r")).toList();

            HookFactory.createConstructorAfterHooks(d3r, new IMethodHookCallback() {
                @Override
                public void onMethodHooked(@NonNull XC_MethodHook.MethodHookParam methodHookParam) {
                    sessionList.add(methodHookParam.thisObject);
                    lastSession = methodHookParam.thisObject;
                }
            });
            hookBeforeClazzsMethod(new IMethodHookCallback() {
                @Override
                public void onMethodHooked(@NonNull XC_MethodHook.MethodHookParam methodHookParam) {
                    if (lastSession != methodHookParam.thisObject) return;
                    Log.i(TAG, "thisObject: " + methodHookParam.thisObject);
                    logBefore(methodHookParam);
                }
            }, "d3r", "yzq", "xzq");*/
            entry();
        }).start();

    }

    private static JWebSocketClient mJWebSocketClient;


    public void entry() {
        Log.i(TAG, "entry 222: ");
        mContextUtils = ContextUtils.getInstance(mClassLoader, currentActivity.getApplication());
        mViewManager = ViewManager.getInstance(this.mClassLoader);

        URI uri = URI.create("ws://192.168.124.16:9873");
        mJWebSocketClient = new JWebSocketClient(uri, this);
        if (mJWebSocketClient != null) {
            try {
                mJWebSocketClient.connectBlocking();
            } catch (Exception e) {
                Log.e(TAG, "onLoad: ", e);
            }
        }

        if (mJWebSocketClient.isOpen()) {
            Log.i(TAG, "连接成功");
        } else
            Log.i(TAG, "连接失败!!!");
    }

    @Override
    public void recvMessage(String message) {
        WebSocketMessage webSocketMessage = new Gson().fromJson(message, WebSocketMessage.class);
        if (WebSocketMessage.Type.GET_LAYOUT_IMG.equals(webSocketMessage.getType())) {
            if (mJWebSocketClient == null) {
                Log.i(TAG, "mJWebSocketClient == null");
                return;
            }

            ArrayList<View> windowsView = mViewManager.getWindowsView(currentActivity);


            windowsView.forEach(view -> {
                Log.i(TAG, "recvMessage view: " + view);
                byte[] activityScreenBytes = ScreenShot.getActivityScreenBytes(currentActivity, view);
//                mJWebSocketClient.send(activityScreenBytes);
            });
            // 发送完毕
            WebSocketMessage textMessage = WebSocketMessage.createMessage(WebSocketMessage.Type.GET_LAYOUT_IMG_END);
            mJWebSocketClient.send(textMessage);
        } else if (WebSocketMessage.Type.GET_LAYOUT.equals(webSocketMessage.getType())) {
            Map<String, ViewInfo> activitysLayout = mViewManager.getActivitysLayout(currentActivity);

            activitysLayout.forEach((key, viewInfo) -> {
                Log.i(TAG, "key: " + key);

                byte[] activityScreenBytes = ScreenShot.getActivityScreenBytes(currentActivity, viewInfo.getView());

                Log.i(TAG, "setImgData: " + viewInfo);
                viewInfo.setImgData(Base64.encodeToString(activityScreenBytes, Base64.NO_WRAP));
            });

            String activitysLayoutInfo = new Gson().toJson(activitysLayout);
            WebSocketMessage textMessage = WebSocketMessage.createLayoutMessage("0", activitysLayoutInfo);
            mJWebSocketClient.send(textMessage);
        }
    }
}
