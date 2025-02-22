package com.shark.input;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.shark.aidl.IInputService;
import com.shark.input.model.ControlMessage;
import com.shark.input.model.Input;
import com.shark.input.model.Point;
import com.shark.input.model.Position;
import com.shark.input.model.Size;
import com.shark.utils.SleepUtil;

import java.util.Random;

public class InputManager {
    public static final String TAG = "SharkChilli";
    private IInputService iInputService;
    public Gson mGson = new Gson();

    // 静态私有实例，确保单例
    private static InputManager instance;

    // 私有构造函数，防止外部实例化
    private InputManager() {
        // 初始化操作
        IBinder requestBinder = requestBinder(null, "noName");
        if (requestBinder == null) {
            Log.e(TAG, "requestBinder is null ");
            return;
        }

        iInputService = IInputService.Stub.asInterface(requestBinder);
        Log.i(TAG, "InputManager iInputService: " + iInputService);
    }

    private void handleEvent(ControlMessage controlMessage) {
        String msg = mGson.toJson(controlMessage);
        try {
            iInputService.handleEvent(msg);
        } catch (Exception e) {
            Log.e(TAG, "handleEvent: ", e);
        }
    }

    public void injectKeycode(int action, Integer keycode) {
        ControlMessage controlMessage = ControlMessage.createInjectKeycode(action, keycode, 0, 0);
        handleEvent(controlMessage);
    }

    public void downKeycode(Integer keycode) {
        injectKeycode(Input.AKEY_EVENT_ACTION_DOWN, keycode);
    }

    public void upKeycode(Integer keycode) {
        injectKeycode(Input.AKEY_EVENT_ACTION_UP, keycode);
    }

    public void downUpKeycode(Integer keycode) {
        downKeycode(keycode);
        SleepUtil.randomSleep(50, 150);
        upKeycode(keycode);
    }

    public void inputText(String text) {
        ControlMessage controlMessage = ControlMessage.createInjectText(text);
        handleEvent(controlMessage);
    }

    //{"action":0,"actionButton":1,"buttons":1,"copyKey":0,"hScroll":0.0,"keycode":0,"metaState":0,"paste":false,"pointerId":-1,"position":{"point":{"x":334,"y":271},"screenSize":{"height":816,"width":376}},"pressure":1.5258789E-5,"repeat":0,"sequence":0,"type":2,"vScroll":0.0}
    //{type=2, text='null', metaState=0, action=0, keycode=0, actionButton=1, buttons=1, pointerId=-1, pressure=1.5258789E-5, position=Position{point=Point{x=280, y=163}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}
    //{type=2, text='null', metaState=0, action=0, keycode=0, actionButton=1, buttons=1, pointerId=-1, pressure=1.5258789E-5, position=Position{point=Point{x=280, y=163}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}
    public void touch(int x, int y) {
        Point point = new Point(x, y);
        Size size = new Size(376, 816);
        Position position = new Position(point, size);
        ControlMessage controlMessage = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_DOWN, -1, position, (float) 1.5258789E-5, 1, 1);
        handleEvent(controlMessage);
        SleepUtil.randomSleep(50, 150);
        ControlMessage controlMessage2 = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_UP, -1, position, (float) 0.0f, 1, 0);
        handleEvent(controlMessage2);
    }

    /**
     * 计算 View 的中心点，并模拟触摸点击
     *
     * @param view 需要点击的 View
     */
    public void click(View view) {
        if (view == null) {
            return;
        }

        // 计算 View 中心点坐标
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int centerX = location[0] + view.getWidth() / 2;
        int centerY = location[1] + view.getHeight() / 2;

        Log.i(TAG, "centerX: " + centerX);
        Log.i(TAG, "centerY: " + centerY);

        // 触发点击事件
        touch(centerX, centerY);
    }

    public void swipe(int startX, int startY, int endX, int endY) {
        swipe(startX, startY, endX, endY, 100);
    }


    public void swipe(int startX, int startY, int endX, int endY, int time) {
        Point point = new Point(startX, startY);
        Size size = new Size(376, 816);
        Position position = new Position(point, size);
        ControlMessage controlMessage = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_DOWN, -1, position, (float) 1.5258789E-5, 1, 1);
        handleEvent(controlMessage);


        Random random = new Random();
        int totalDistanceX = endX - startX; // 总的X轴位移
        int totalDistanceY = endY - startY; // 总的Y轴位移

        // 用于记录当前坐标
        int currentX = startX;
        int currentY = startY;

        // 记录已经耗费的时间
        int elapsedTime = 0;

        // 开始滑动
        while (elapsedTime < time) {
            // 生成随机步长 (5 到 20)
            int step = random.nextInt(16) + 5;

            // 计算当前时间比例
            float progress = Math.min((float) elapsedTime / time, 1);

            // 根据比例计算理论上的目标位置
            int targetX = startX + Math.round(totalDistanceX * progress);
            int targetY = startY + Math.round(totalDistanceY * progress);

            // 计算每步要移动的方向和大小
            int deltaX = Math.min(Math.abs(targetX - currentX), step) * Integer.signum(totalDistanceX);
            int deltaY = Math.min(Math.abs(targetY - currentY), step) * Integer.signum(totalDistanceY);

            // 更新当前坐标
            currentX += deltaX;
            currentY += deltaY;

            // 打印每次移动后的坐标
//            Log.i(TAG, "Current Position: (" + currentX + ", " + currentY + ")");
            press(currentX, currentY);

            // 模拟延迟
            int delay = Math.min(step * time / Math.abs(totalDistanceX + totalDistanceY), time - elapsedTime);
            elapsedTime += delay;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 确保最终位置是 (endX, endY)
//        Log.i(TAG, "Final Position: (" + endX + ", " + endY + ")");

        Point point2 = new Point(endX, endY);
        Size size2 = new Size(376, 816);
        Position position2 = new Position(point2, size2);

        ControlMessage controlMessage2 = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_UP, -1, position2, (float) 0.0f, 1, 0);

        handleEvent(controlMessage2);
    }

    public void press(int x, int y) {
        Point point2 = new Point(x, y);
        Size size2 = new Size(376, 816);
        Position position2 = new Position(point2, size2);
        ControlMessage controlMessage2 = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_MULTIPLE, -1, position2, (float) 1.5258789E-5, 1, 1);
        handleEvent(controlMessage2);
    }

    public void touchHold(int x, int y) {
        this.touchHold(x, y, 3000);
    }

    public void touchHold(int x, int y, int sleepTime) {
        Point point = new Point(x, y);
        Size size = new Size(376, 816);
        Position position = new Position(point, size);
        ControlMessage controlMessage = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_DOWN, -1, position, (float) 1.5258789E-5, 1, 1);
        handleEvent(controlMessage);
        try {
            // 休眠指定的时间
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // 捕获中断异常并恢复中断状态
            Thread.currentThread().interrupt();
        }
        ControlMessage controlMessage2 = ControlMessage.createInjectTouchEvent(Input.AKEY_EVENT_ACTION_UP, -1, position, (float) 0.0f, 1, 0);
        handleEvent(controlMessage2);
    }

//{type=2, text='null', metaState=0, action=0, keycode=0, actionButton=1, buttons=1, pointerId=-1, pressure=1.5258789E-5, position=Position{point=Point{x=230, y=451}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}
//{type=2, text='null', metaState=0, action=1, keycode=0, actionButton=1, buttons=0, pointerId=-1, pressure=0.0,          position=Position{point=Point{x=230, y=451}, screenSize=Size{width=376, height=816}}, hScroll=0.0, vScroll=0.0, copyKey=0, paste=false, repeat=0, sequence=0}

    private static IBinder requestBinder(Context context, String niceName) {
        int BRIDGE_ACTION_GET_BINDER = 3;
        String BRIDGE_SERVICE_NAME = "activity";
        int BRIDGE_TRANSACTION_CODE = 1598837584;
        try {
            // 获取 AMS
            String bridgeServiceName = BRIDGE_SERVICE_NAME;
            IBinder bridgeService = ServiceManager.getService(bridgeServiceName);

            if (bridgeService == null) {
                Log.d(TAG, "Can't get " + BRIDGE_SERVICE_NAME);
                return null;
            }

            // Heartbeat Binder 实例
            Binder heartBeatBinder = new Binder();

            // Parcel 初始化
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();

            try {
                // 写入 descriptor
                data.writeInterfaceToken("LSPosed");
                // 写入 action code
                data.writeInt(BRIDGE_ACTION_GET_BINDER);
                // 写入 niceName
                data.writeString(niceName);
                // 写入 Heartbeat Binder
                data.writeStrongBinder(heartBeatBinder);

                // 调用 transact
                boolean result = bridgeService.transact(
                        BRIDGE_TRANSACTION_CODE,
                        data,
                        reply,
                        0
                );

                if (result) {
                    // 检查异常
                    reply.readException();
                    // 获取返回的 Binder
                    IBinder readStrongBinder = reply.readStrongBinder();
                    Log.e(TAG, "get requestBinder result: " + readStrongBinder);
                    return readStrongBinder;
                }
            } finally {
                // 回收 Parcel
                data.recycle();
                reply.recycle();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to request binder", e);
        }
        return null;
    }

    // 提供全局访问点，使用双重检查锁实现线程安全的单例
    public static InputManager getInstance() {
        if (instance == null) {
            synchronized (InputManager.class) {
                if (instance == null) {
                    instance = new InputManager();
                }
            }
        }
        return instance;
    }

}
