package com.ezhuang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ezhuang.MainActivity;
import com.ezhuang.MyApp;
import com.ezhuang.common.Global;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

public class JpushReceiver extends BroadcastReceiver {
    public JpushReceiver() {
    }

    String TAG = this.getClass().getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

            JPushLocalNotification ln = new JPushLocalNotification();
            ln.setBuilderId(0);
            ln.setTitle(message);
            ln.setContent(title);
            ln.setNotificationId(11111111) ;
            ln.setBroadcastTime(System.currentTimeMillis() + 1000 * 60 * 10);

            JPushInterface.addLocalNotification(context.getApplicationContext(), ln);

            Intent msgIntent = new Intent(Global.PUSH_BROADCAST);
            context.sendBroadcast(msgIntent);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG,"收到了通知");
            Log.d("title",bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            Log.d("message",bundle.getString(JPushInterface.EXTRA_ALERT));
//            Log.d("extra",bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG,"用户点击打开了通知");

            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("from_notify",true);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }
}
