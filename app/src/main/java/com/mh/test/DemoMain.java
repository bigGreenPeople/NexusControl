package com.mh.test;

import android.util.Log;

import com.google.gson.Gson;
import com.shark.SuperModule;
import com.shark.input.InputManager;
import com.shark.view.ViewInfo;

import java.util.ArrayList;

public class DemoMain extends SuperModule {
    @Override
    public void main(ClassLoader classLoader, String processName, String packageName) {
        trackActivityOnResume(classLoader);
        Log.i(TAG, "main: DemoMain");
//        InputManager inputManager = InputManager.getInstance();
//        inputManager.inputText("Hello NexusControl!!");
//        InputManager.getInstance().swipe(241, 97, 235, 541);

        ArrayList<ViewInfo> windowViewInfo = mViewManager.getWindowViewInfo(mContextUtils.getTopActivity());
        Gson gson = new Gson();

        Log.i(TAG, "windowViewInfo: " + gson.toJson(windowViewInfo));
    }
}
