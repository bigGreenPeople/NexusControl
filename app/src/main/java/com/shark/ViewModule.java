package com.shark;

import android.view.View;

import com.shark.input.InputManager;
import com.shark.view.ViewInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewModule extends SuperModule {

    public final static int WAIT_TIME = 1000;

    public void clickById(Integer targetId) {
        click(targetId, null, null);
    }

    public void clickByText(String text) {
        click(null, text, null);
    }

    public void clickByClass(String clazzName) {
        click(null, null, clazzName);
    }

    /**
     * 如果没有合适的View将会等待带View出现 这可能会锁死
     *
     * @param id
     * @param text
     * @param clazzName
     */
    public void click(Integer id, String text, String clazzName) {
        ViewInfo view = findView(id, text, clazzName);
        while (view == null) {

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            view = findView(id, text, clazzName);
        }

        click(view);
    }

    public void click(ViewInfo view) {
        if (view == null) return;
        InputManager.getInstance().click(view.getView());
    }


    public ViewInfo findViewById(Integer targetId) {
        return findView(targetId, null, null);
    }

    public ViewInfo findViewByText(String text) {
        return findView(null, text, null);
    }

    public ViewInfo findViewByClass(String clazzName) {
        return findView(null, null, clazzName);
    }

    public ViewInfo findView(Integer id, String text, String clazzName) {
        ArrayList<ViewInfo> windowViewInfo = mViewManager.getWindowViewInfo(currentActivity);
        for (ViewInfo viewInfo :
                windowViewInfo) {
            ViewInfo viewById = viewInfo.findView(id, text, clazzName);
            return viewById;
        }
        return null;
    }

    public List<ViewInfo> findViewList(Integer id, String text, String clazzName) {
        ArrayList<ViewInfo> result = new ArrayList<>();
        ArrayList<ViewInfo> windowViewInfo = mViewManager.getWindowViewInfo(currentActivity);
        for (ViewInfo viewInfo :
                windowViewInfo) {
            List<ViewInfo> viewById = viewInfo.findViewList(id, text, clazzName);
            result.addAll(viewById);
        }
        return result;
    }
}
