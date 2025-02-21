package com.mh.test;

import android.util.Log;

import com.google.gson.Gson;
import com.shark.SuperModule;
import com.shark.input.InputManager;
import com.shark.utils.LogUtils;
import com.shark.view.ViewInfo;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DemoMain extends SuperModule {

    @Override
    protected String getTargetPackageName() {
        return "com.autonavi.minimap";
    }

    @Override
    public void main(ClassLoader classLoader, String processName, String packageName) {
        trackActivityOnResume(classLoader);
        Log.i(TAG, "main: DemoMain");
//        InputManager inputManager = InputManager.getInstance();
//        inputManager.inputText("Hello NexusControl!!");
        InputManager.getInstance().swipe(241, 97, 235, 541);
//        InputManager.getInstance().touchHold(180, 1227);


        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ArrayList<ViewInfo> windowViewInfo = mViewManager.getWindowViewInfo(currentActivity);
            Gson gson = new Gson();

            windowViewInfo.forEach(new Consumer<ViewInfo>() {
                @Override
                public void accept(ViewInfo viewInfo) {
                    ViewInfo viewById = viewInfo.findViewById("我的");
                    Log.i(TAG, "windowViewInfo: " + gson.toJson(viewById));
                    if (viewById != null) {
                        InputManager.getInstance().click(viewById.getView());
                    }
                }
            });
//            Log.i(TAG, "windowViewInfo: " + gson.toJson(windowViewInfo));
//            LogUtils.logLongString(gson.toJson(windowViewInfo));

        }).start();

    }
}
