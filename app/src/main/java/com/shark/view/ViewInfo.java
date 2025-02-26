package com.shark.view;

import android.app.Activity;
import android.view.View;

import com.google.gson.annotations.Expose;
import com.shark.input.InputManager;

import java.util.ArrayList;
import java.util.List;

/**
 * View视图信息
 */
public class ViewInfo {
    private String className;
    private boolean enabled;
    private boolean shown;
    private int id;
    private String text;
    private String description;

    private String imgData;

    private int x;
    private int y;
    private int width;
    private int height;

    private transient View mView;


    private List<ViewInfo> childList;


    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ViewInfo> getChildList() {
        return childList;
    }

    public void setChildList(List<ViewInfo> childList) {
        this.childList = childList;
    }

    public void click() {
        InputManager.getInstance().click(getView());
    }

    public void click(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        InputManager.getInstance().click(getView());
    }

    public void clickByActivity(String clazzName) {
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
        // 如果 id, text, clazzName 不为 null，则进行匹配判断；如果为 null，则跳过该条件
        boolean idMatches = (id == null || this.id == id);
        boolean textMatches = (text == null || text.equals(this.getText()));
        boolean classMatches = (clazzName == null || clazzName.equals(this.getClassName()));

        // 如果都匹配，返回当前 ViewInfo
        if (idMatches && textMatches && classMatches) {
            return this;
        }

        // 遍历子 ViewInfo 列表
        if (childList != null) {
            for (ViewInfo child : childList) {
                ViewInfo result = child.findView(id, text, clazzName);
                if (result != null) {
                    return result;
                }
            }
        }

        // 如果没有找到，则返回 null
        return null;
    }


    public List<ViewInfo> findViewList(Integer id, String text, String clazzName) {
        List<ViewInfo> matchingViews = new ArrayList<>();

        // 根据条件检查当前 View 是否符合要求
        boolean idMatches = (id == null || this.id == id);
        boolean textMatches = (text == null || text.equals(this.getText()));
        boolean classMatches = (clazzName == null || clazzName.equals(this.getClassName()));

        // 如果当前 View 符合条件，添加到结果列表
        if (idMatches && textMatches && classMatches) {
            matchingViews.add(this);
        }

        // 遍历子 ViewInfo 列表，递归查找符合条件的 View
        if (childList != null) {
            for (ViewInfo child : childList) {
                matchingViews.addAll(child.findViewList(id, text, clazzName));
            }
        }

        return matchingViews;
    }


    @Override
    public String toString() {
        return "ViewInfo{" +
                "className='" + className + '\'' +
                ", enabled=" + enabled +
                ", shown=" + shown +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", mView=" + mView +
                ", childList=" + childList +
                '}';
    }
}