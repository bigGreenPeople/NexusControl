package com.mh.test;

import android.util.Log;

import com.shark.SuperModule;
import com.shark.input.InputManager;

public class DemoMain extends SuperModule {
    @Override
    public void main(ClassLoader classLoader, String processName, String packageName) {
        trackActivityOnResume(classLoader);
        Log.i(TAG, "main: DemoMain");
        InputManager inputManager = InputManager.getInstance();
//        inputManager.inputText("Hello NexusControl!!");
        InputManager.getInstance().swipe(241, 97, 235, 541);

    }
}
