package com.shark.view;

import android.view.View;

import com.google.gson.annotations.Expose;

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

    private int x;
    private int y;
    private int width;
    private int height;

    private transient View mView;


    private List<ViewInfo> childList;

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

    public ViewInfo findViewById(int targetId) {
        // 如果当前 ViewInfo 的 ID 符合条件，直接返回
        if (this.id == targetId) {
            return this;
        }

        // 遍历子 ViewInfo 列表
        if (childList != null) {
            for (ViewInfo child : childList) {
                ViewInfo result = child.findViewById(targetId);
                if (result != null) {
                    return result;
                }
            }
        }

        // 如果没有找到，则返回 null
        return null;
    }
    public ViewInfo findViewById(String text) {
        // 如果当前 ViewInfo 的 ID 符合条件，直接返回
        if (this.getText()!=null && this.getText().equals(text)) {
            return this;
        }

        // 遍历子 ViewInfo 列表
        if (childList != null) {
            for (ViewInfo child : childList) {
                ViewInfo result = child.findViewById(text);
                if (result != null) {
                    return result;
                }
            }
        }

        // 如果没有找到，则返回 null
        return null;
    }
}