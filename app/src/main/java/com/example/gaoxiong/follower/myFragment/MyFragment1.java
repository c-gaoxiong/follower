package com.example.gaoxiong.follower.myFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gaoxiong.follower.BleUUID;
import com.example.gaoxiong.follower.R;
import com.orhanobut.logger.Logger;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment1 extends Fragment {

    public MyFragment1() {
    }
    View view;
    Button button;
    Button button2;
    Button button3;
Context context;
    FirstReceiver firstReceiver;
    boolean b=false;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.followerlayout,container,false);
        Logger.e("第一个Fragment");
        button = (Button)view.findViewById(R.id.button);//启动
        button2 = (Button)view.findViewById(R.id.button2);//跟随
        button3 = (Button)view.findViewById(R.id.button3);//停止
        button2.setOnClickListener(clickListener);
        button.setOnClickListener(clickListener);
        button3.setOnClickListener(clickListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        firstReceiver = new FirstReceiver();
        IntentFilter intent1 = new IntentFilter();
        intent1.addAction(BleUUID.RECEIVED);
        context.getApplicationContext().registerReceiver(firstReceiver,intent1);
    }
    public class FirstReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String string = intent.getStringExtra("start");
            if(action.equals(BleUUID.RECEIVED)){
                if(string!=null){
                    if(string.equals(R.string.follower)){
                        button2.setText(R.string.stop_follower);

                    }
                    if(string.equals(R.string.stop_follower)){
                        button2.setText(R.string.follower);
                    }

                }

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.getApplicationContext().unregisterReceiver(firstReceiver);
    }

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button3:
                    String v1 = "c";
                    Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
                    Logger.e("停止：c");
                    intent0.putExtra("control", v1);
                    context.getApplicationContext().sendBroadcast(intent0);

                    Intent intent10 = new Intent(BleUUID.RECEIVED);
                    intent10.putExtra("start", button3.getText().toString());
                    context.getApplicationContext().sendBroadcast(intent10);
                    break;
                case R.id.button:
                    String a = "a";
                    Logger.e("a");
                    Intent intent1 = new Intent(BleUUID.CHAIR_CONTROL);
                    intent1.putExtra("control", a);
                    context.getApplicationContext().sendBroadcast(intent1);
                    break;
                case R.id.button2:
                    String s = (String) button2.getText();
                    Intent intent2 = new Intent(BleUUID.RECEIVED);
                    intent2.putExtra("start",s);
                    context.getApplicationContext().sendBroadcast(intent2);


                    break;
                default:break;
            }
        }


    };

}
