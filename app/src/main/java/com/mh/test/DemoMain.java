package com.mh.test;

import com.shark.SuperModule;
import com.shark.input.InputManager;

public class DemoMain extends SuperModule {
    @Override
    public void main(ClassLoader classLoader, String processName, String packageName) {
        trackActivityOnResume(classLoader);
        InputManager inputManager = InputManager.getInstance();
//        inputManager.inputText("Hello NexusControl!!");
        InputManager.getInstance().swipe(241, 97, 235, 541);

    }
}
