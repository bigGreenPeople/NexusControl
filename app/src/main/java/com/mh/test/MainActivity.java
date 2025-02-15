package com.mh.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.shark.input.InputManager;
import com.shark.input.model.ControlMessage;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "SharkChilli";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test(View view) throws RemoteException {
//        IBinder example_service = ServiceManager.getService("example_service");
//        Log.i(TAG, "example_service: " + example_service);
//        IBinder requestBinder = requestBinder(this, "testApp");
//
//        if (requestBinder == null){
//            Log.e(TAG, "requestBinder is null " );
//            return;
//        }

//        IExampleService iExampleService = IExampleService.Stub.asInterface(requestBinder);
//        Log.i(TAG, "requestBinder: " + iExampleService.add(2,3));
//        Toast.makeText(this, test1("nihjao"), Toast.LENGTH_SHORT).show();

        new Thread(() -> {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            runOnUiThread(() -> {
                View viewById = findViewById(R.id.et_test);
                viewById.requestFocus();
            });
            InputManager inputManager = InputManager.getInstance();
//            ControlMessage controlMessage = ControlMessage.createInjectText("765456456");
//            inputManager.handleEvent(controlMessage);
            inputManager.inputText("765456456");
        }).start();
    }

    public String test1(String msg) {

        Log.i(TAG, "test1: " + msg);
        return "ok " + msg;
    }


}