package com.homeout.Lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//화면이 꺼지는 것을 감지하는 receiver
public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(intent.ACTION_SCREEN_OFF)){
            Intent i = new Intent(context, LockActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
