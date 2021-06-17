package com.fro.room_sunalarmcase;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fro.room_sunalarmcase.MainActivity;
import com.fro.room_sunalarmcase.R;

public class splash_activity extends Activity {
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what==190)
            {
                Intent intent1=new Intent(splash_activity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
            super.handleMessage(msg);

        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        WaitThread thread1=new WaitThread();
        thread1.start();
    }

    public class WaitThread extends Thread
    {
        @Override
        public void run() {
            try{
                Thread.sleep(3000);
            }catch(InterruptedException exception)
            {
                exception.printStackTrace();
            }
            Message message1=new Message();
            message1.what=190;
            message1.arg1=1;
            handler.sendMessage(message1);


        }
    }
}
