package com.example.gaoxiong.follower.myFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gaoxiong.follower.BleService;
import com.example.gaoxiong.follower.BleUUID;
import com.example.gaoxiong.follower.R;
import com.orhanobut.logger.Logger;

/**
 * Created by Jay on 2015/8/28 0028.
 */

public class MyFragment2 extends Fragment {
TextView textView;
    Context context;
    ReceivedReceiver2 receivedReceiver2;
    public MyFragment2() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_content,container,false);
        textView = (TextView)view.findViewById(R.id.textView2);
        receivedReceiver2 = new ReceivedReceiver2();
        IntentFilter intent1 = new IntentFilter();
        intent1.addAction(BleUUID.RECEIVED);
        context.getApplicationContext().registerReceiver(receivedReceiver2,intent1);
        Logger.e("第二个Fragment");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.getApplicationContext().unregisterReceiver(receivedReceiver2);
    }

    public class ReceivedReceiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String ctr2 = intent.getStringExtra("data");
            if(ctr2!=null&&ctr2.length()>=2){
                String s = ctr2.substring(0,2);
                Intent i = new Intent(BleUUID.CHAIR_CONTROL);

                switch (s){
                    case "s0":
                        textView.setText("正坐");
                        textView.setTextColor(context.getResources().getColor(R.color.color_Green));
                        i.putExtra("control","a");
                        context.getApplicationContext().sendBroadcast(i);
                        BleUUID.chair_state = "停止";

                        break;
                    case "s1":
                        textView.setText("前倾");
                        textView.setTextColor(context.getResources().getColor(R.color.colorRed));
                        i.putExtra("control","c");
                        context.getApplicationContext().sendBroadcast(i);
                        BleUUID.chair_state = "启动";
                        break;
                    case "s2":
                        textView.setText("左倾");
                        textView.setTextColor(context.getResources().getColor(R.color.colorRed));
                        i.putExtra("control","c");
                        context.getApplicationContext().sendBroadcast(i);
                        BleUUID.chair_state = "启动";
                        break;
                    case "s3":
                        textView.setText("右倾");
                        textView.setTextColor(context.getResources().getColor(R.color.colorRed));
                        i.putExtra("control","c");
                        context.getApplicationContext().sendBroadcast(i);
                        BleUUID.chair_state = "启动";
                        break;
                    case "s4":
                        textView.setText("无人");
                        BleUUID.chair_state = "启动";
//                        Intent i1 = new Intent(BleUUID.BTN_CHANGE);
//                        i1.putExtra("start",getString(R.string.follower));
//                        context.getApplicationContext().sendBroadcast(i1);
                        break;
                    default:break;

                }

            }


        }

    }

}
