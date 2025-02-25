package com.mh.test;

import android.util.Log;

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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DemoMain extends ViewModule implements IRecvListener {

    @Override
    public void main(ClassLoader classLoader, String processName, String packageName) {
        trackActivityOnResume(classLoader);
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
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Log.i(TAG, "main: *************");
            List<ViewInfo> viewList = findViewList(null, null, "com.taobao.search.musie.MuiseContainer");
            Log.i(TAG, "viewList size: " + viewList);
//            if (ThreadUtils.isMainThread()) {
//                // 当前是 UI 线程
//                Log.d(TAG, "Running on the main thread.");
//            } else {
//                // 当前不是 UI 线程
//                Log.d(TAG, "Not running on the main thread.");
//            }
//
//            runUi(new Runnable() {
//                @Override
//                public void run() {
//                    if (ThreadUtils.isMainThread()) {
//                        // 当前是 UI 线程
//                        Log.d(TAG, "Running on the main thread.");
//                    } else {
//                        // 当前不是 UI 线程
//                        Log.d(TAG, "Not running on the main thread.");
//                    }
//                }
//            });
//            clickByText("我的");
            entry();
        }).start();

    }

    private static JWebSocketClient mJWebSocketClient;


    public void entry() {
        Log.i(TAG, "entry: ");
        mContextUtils = ContextUtils.getInstance(mClassLoader, currentActivity.getApplication());
        mViewManager = ViewManager.getInstance(this.mClassLoader);

        URI uri = URI.create("ws://192.168.31.52:9873");
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

            mContextUtils.getRunningActivitys().forEach(activity -> {
                Log.i(TAG, "recvMessage activity: " + activity);
                byte[] activityScreenBytes = ScreenShot.getActivityScreenBytes(activity);
                mJWebSocketClient.send(activityScreenBytes);
            });
            // 发送完毕
            WebSocketMessage textMessage = WebSocketMessage.createMessage(WebSocketMessage.Type.GET_LAYOUT_IMG_END);
            mJWebSocketClient.send(textMessage);
        } else if (WebSocketMessage.Type.GET_LAYOUT.equals(webSocketMessage.getType())) {
            Map<String, ViewInfo> activitysLayout = mViewManager.getActivitysLayout(mContextUtils.getRunningActivitys());
            String activitysLayoutInfo = new Gson().toJson(activitysLayout);
            WebSocketMessage textMessage = WebSocketMessage.createLayoutMessage("0", activitysLayoutInfo);
            mJWebSocketClient.send(textMessage);
        }
    }
}
