package com.example.gaoxiong.follower;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BleService extends Service {
    MyReceiver myReceiver;
    ReceivedReceiver receivedReceiver;
    boolean start=false;
    public BleService() {
    }
    StartScan  startScan= StartScan.getInstance();

//    List<BleGattClass> bleGattClassList = new ArrayList<>();

    BleGattClass bleGattClass ;

   public  static HashMap<String,BleGattClass> hashMap = new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Logger.d("进入服务>>>>>>>>onCreate");


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleUUID.CHAIR_CONTROL);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver,intentFilter);

        receivedReceiver = new ReceivedReceiver();
        IntentFilter intent1 = new IntentFilter();
        intent1.addAction(BleUUID.RECEIVED);
        registerReceiver(receivedReceiver,intent1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startScan.scanDevice(true);
        Logger.d("后台服务扫描>>>>>");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        unregisterReceiver(receivedReceiver);
    }

    Handler handler=new Handler(Looper.getMainLooper());
    void MyToast(final String string){
        handler.post(new Runnable(){
            public void run(){
                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
            }
        });
    }



    public class ReceivedReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            String ctr = intent.getStringExtra("data");
            String start_up= intent.getStringExtra("start");
            Logger.d("BleService--Received>>>>>>>>"+ctr);
            Logger.d("BleService--Received>>>>>>>>"+start_up);
            if(start_up!=null) {
                Logger.d(start_up);
                if (start_up.equals("开始跟随")) {
                    start = true;
                } else if (start_up.equals("停止跟随")) {
                    start = false;
                }
            }
            if(start) {
                if (ctr != null&&ctr.length()>=7) {
                    String ctr1 = ctr.substring(0, 7);
                    Logger.d(ctr);

                    if (ctr1.length() == 7 && ctr1.startsWith("#")) {
                        Logger.d(ctr1);
                        Intent i = new Intent(BleUUID.CHAIR_CONTROL);
                        i.putExtra("control", ctr1);
                        sendBroadcast(i);
                    }
                }
            }

        }
    }
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
             String control = intent.getStringExtra("control");
             String address = intent.getStringExtra("address");
            String disconnect = intent.getStringExtra("disconnect");
            if (action.equalsIgnoreCase(BleUUID.CHAIR_CONTROL)) {


                if (control!=null) {
                    Logger.d(control);
                    if(hashMap.get(BleUUID.CHAIR_ADDRESS)!=null){
                        if(control.length()<=7){
                            hashMap.get(BleUUID.CHAIR_ADDRESS).sendOrder(control);
                        }

                    }else {
                        new BleGattClass(getApplicationContext(),BleUUID.CHAIR_ADDRESS).connectBluetooth();
//                        MyToast("未连接设备");
                    }


                }

                if (address != null) {
                    Logger.d(address);
                    if(hashMap.get(address)==null){
                        if(startScan.address.contains(address)){
                            bleGattClass = new BleGattClass(getApplicationContext(),address);
                            bleGattClass.connectBluetooth();
                            hashMap.put(address,bleGattClass);
                        }else {
                            MyToast("未扫描到设备");
                        }

                    }else {
                        switch (hashMap.get(address).getState()){
                            case 0:
//                                MyToast("未连接");
//                                Logger.d("未连接");
                                hashMap.get(address).connectBluetooth();
                                break;
                            case 1:
//                                MyToast("正在连接");
                                break;
                            case 2 :
                                Logger.d("已连接");
//                                MyToast("已连接");
                                break;
                            case 3:
//                                MyToast("正在断开");
                                break;
                            case 4:
                                MyToast("未搜索到该设备蓝牙");
                            default:break;

                        }
                    }
                }
                if(disconnect!=null){
                    if(address!=null){
                        if((disconnect.equals("disconnect"))&&hashMap.get(address)!=null){
                            hashMap.get(address).disConnect();
                            MyToast("断开连接："+address);
                        }else {
//                            MyToast("未连接");
                            Logger.d("未连接");
                        }

                    }

                }
            }


        }
    }
}
