package com.example.gaoxiong.follower;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by gaoxiong on 2018/7/30.
 */

public  class BleUUID {
    BleUUID(){};

    //手机发送数据给手环
    public final static UUID UUID_BLUE_WRITE = UUID
            .fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    //获取蓝牙串口服务UUID
    public final static UUID UUID_BLUE_SERVICE = UUID
            .fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    //用来与手环通信  更新消息的UUid
    public final static UUID UUID_NOTIFY = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(HEART_RATE_MEASUREMENT);
    public static final String CHAIR_ADDRESS= "7C:01:0A:64:77:20";
//    public static final String CHAIR_ADDRESS= "7C:01:0A:64:77:1C";//1C为血糖
    public static final String RADAR_ADDRESS= "3C:2D:B7:85:D9:B9";
    public static final String CUSHION_ADDRESS= "7C:01:0A:64:73:9B";

    public  static  HashMap<String, String> map = new HashMap<String, String>() {
        {
            put(CHAIR_ADDRESS, "智能轮椅");
           put(RADAR_ADDRESS, "激光雷达");
            put(CUSHION_ADDRESS, "智能坐垫");
        }
    };

    public static final String CHAIR_CONTROL = "android.ble.chair.control";
    public static final String RECEIVED = "android.ble.chair.received";
    public static final String CONNECT = "android.ble.chair.connected";
    public static final String BTN_CHANGE = "android.ble.chair.changed";
    public static  String chair_state = "启动";
    public static  String radar_state = "开始跟随";


}
