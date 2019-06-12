package com.example.gaoxiong.follower.myFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaoxiong.follower.BleService;
import com.example.gaoxiong.follower.BleUUID;
import com.example.gaoxiong.follower.MainActivity;
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
    Activity activity;
    public static MainActivity mainActivity;
    Context context;
    ReceivedReceiver1 receivedReceiver1;
    FirstReceiver firstReceiver;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.followerlayout,container,false);
        View view1 = view.findViewById(R.id.linear1);
        view1.getBackground().setAlpha(135);


        Logger.e("第一个Fragment");
        button = (Button)view.findViewById(R.id.button);//启动
        button.setText(BleUUID.chair_state);
        button2 = (Button)view.findViewById(R.id.button2);//跟随
        button2.setText(BleUUID.radar_state);
        button3 = (Button)view.findViewById(R.id.button3);//人体状态

        button2.setOnClickListener(clickListener);
        button.setOnClickListener(clickListener);
        button3.setOnClickListener(clickListener);
         mainActivity = (MainActivity)getActivity();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        firstReceiver = new FirstReceiver();
        IntentFilter intent11 = new IntentFilter();
        intent11.addAction(BleUUID.BTN_CHANGE);
        context.getApplicationContext().registerReceiver(firstReceiver,intent11);

        receivedReceiver1 = new ReceivedReceiver1();
        IntentFilter intent1 = new IntentFilter();
        intent1.addAction(BleUUID.RECEIVED);
        context.getApplicationContext().registerReceiver(receivedReceiver1,intent1);
    }

    public class FirstReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String string = intent.getStringExtra("start");
            Intent intent1 = new Intent(BleUUID.RECEIVED);
            if(action.equals(BleUUID.BTN_CHANGE)){
                if(string!=null){
                    if(string.equals(context.getResources().getString(R.string.follower))){
                        button2.setText(context.getResources().getString(R.string.stop_follower));
                        intent1.putExtra("start","开始跟随");
                        BleUUID.radar_state =context.getResources().getString(R.string.stop_follower);
                        Logger.d("开始跟随");
                        context.getApplicationContext().sendBroadcast(intent1);
                    }else {
                        button2.setText(getString(R.string.follower));
                        intent1.putExtra("start","停止跟随");
                        Logger.d("停止跟随");
                        context.getApplicationContext().sendBroadcast(intent1);

                        BleUUID.radar_state = getString(R.string.follower);
                    }

                }

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.getApplicationContext().unregisterReceiver(firstReceiver);
        context.getApplicationContext().unregisterReceiver(receivedReceiver1);

    }
    public class ReceivedReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String ctr2 = intent.getStringExtra("data");
            if(ctr2!=null&&ctr2.length()>=2){
                String s = ctr2.substring(0,2);
                Intent i = new Intent(BleUUID.CHAIR_CONTROL);

                switch (s){
                    case "s0":
                        button3.setText("正坐");
                        button3.setTextColor(context.getResources().getColor(R.color.color_Green));
                        i.putExtra("control","a");
                        context.getApplicationContext().sendBroadcast(i);
                        BleUUID.chair_state = "停止";
                        button.setText("停止");
                        Logger.d("正坐");
                        break;
                    case "s1":
                        button3.setText("前倾");
                        Logger.d("前倾");
                        button3.setTextColor(context.getResources().getColor(R.color.colorRed));
                        i.putExtra("control","c");
                        context.getApplicationContext().sendBroadcast(i);
                        BleUUID.chair_state = "启动";
                        button.setText("启动");
                        break;
                    case "s2":
                        button3.setText("左倾");
                        button3.setTextColor(context.getResources().getColor(R.color.colorRed));
                        i.putExtra("control","c");
                        context.getApplicationContext().sendBroadcast(i);
                        Logger.d("左倾");
                        BleUUID.chair_state = "启动";
                        button.setText("启动");
                        break;
                    case "s3":
                        button3.setText("右倾");
                        button3.setTextColor(context.getResources().getColor(R.color.colorRed));
                        i.putExtra("control","c");
                        context.getApplicationContext().sendBroadcast(i);
                        Logger.d("右倾");
                        BleUUID.chair_state = "启动";
                        button.setText("启动");
                        break;
                    case "s4":
                        button3.setText("无人");
                        Logger.d("无人");
                        BleUUID.chair_state = "启动";
                        button.setText("启动");

                        break;
                    default:break;

                }

            }


        }

    }

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mainActivity. vibrator.vibrate(60);
            Intent intent0 = new Intent(BleUUID.CHAIR_CONTROL);
            switch (v.getId()) {
                case R.id.button3:

                    break;
                case R.id.button:
                    if(BleService.hashMap.get(BleUUID.CHAIR_ADDRESS)!=null){
                        if(BleService.hashMap.get(BleUUID.CHAIR_ADDRESS).getState()==2){
                            if(button.getText().equals(context.getString(R.string.start_up))){
                                Logger.e("a");
                                Logger.e(context.getString(R.string.start_up));

                                intent0.putExtra("control","a");
                                context.getApplicationContext().sendBroadcast(intent0);
                                button.setText(context.getString(R.string.stop));
                                BleUUID.chair_state = "停止";

                            }else{
                                Logger.e("停止：c");
                                intent0.putExtra("control","c");
                                context.getApplicationContext().sendBroadcast(intent0);
                                button.setText(getString(R.string.start_up));

                                Intent intent10 = new Intent(BleUUID.BTN_CHANGE);
                                intent10.putExtra("start",getString(R.string.stop_follower));
                                context.getApplicationContext().sendBroadcast(intent10);
                                BleUUID.chair_state = "启动";

                            }//end  if(button.getText().equals(context.getString(R.string.start_up)))
                        }
                        else {
                            Toast.makeText(context.getApplicationContext(),"请连接智能轮椅",Toast.LENGTH_SHORT).show();

                            changeToAnotherFragment();

                        }

                    }else {
                        Toast.makeText(context.getApplicationContext(),"请连接智能轮椅",Toast.LENGTH_SHORT).show();

                        changeToAnotherFragment();
                    }

                    break;
                case R.id.button2:
                    if(BleService.hashMap.get(BleUUID.RADAR_ADDRESS)!=null&&BleService.hashMap.get(BleUUID.CHAIR_ADDRESS)!=null){
                        if(BleService.hashMap.get(BleUUID.RADAR_ADDRESS).getState()==2){
                            if(BleService.hashMap.get(BleUUID.CHAIR_ADDRESS).getState()==0){
                                Toast.makeText(context.getApplicationContext(),"请连接智能轮椅",Toast.LENGTH_SHORT).show();
                                changeToAnotherFragment();
                                return;
                            }

                            intent0.putExtra("control","a");
                            context.getApplicationContext().sendBroadcast(intent0);

                            String s = (String) button2.getText();
                            Intent intent2 = new Intent(BleUUID.BTN_CHANGE);
                            intent2.putExtra("start",s);
                            context.getApplicationContext().sendBroadcast(intent2);

                            intent0.putExtra("control","a");
                            context.getApplicationContext().sendBroadcast(intent0);
                            button.setText(context.getString(R.string.stop));
                            BleUUID.chair_state = "停止";
                        }else {
                            Toast.makeText(context.getApplicationContext(),"请连接激光雷达",Toast.LENGTH_SHORT).show();
                            changeToAnotherFragment();

                        }

                    }else {
                        Toast.makeText(context.getApplicationContext(),"请连接激光雷达",Toast.LENGTH_SHORT).show();

                        changeToAnotherFragment();
                    }




                    break;
                default:break;
            }
        }


    };
    public void changeToAnotherFragment(){

        mainActivity.getViewPager().setCurrentItem(3);
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
////        FragmentManager fragmentManager = getActivity().getFragmentManager();
//        Fragment fragment = new MyFragment4();
//        fragmentManager.beginTransaction().replace(R.id.fragment1,fragment).commit();
    }

}
