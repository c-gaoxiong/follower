package com.example.gaoxiong.follower;

import java.util.UUID;

/**
 * Created by gaoxiong on 2018/7/30.
 */

public class BleUUID {

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
    public static final String RADAR_ADDRESS= "7C:01:0A:3A:F5:FA";
    public static final String CUSION_ADDRESS= "D0:39:72:D9:B3:A4";

    public static final String CHAIR_CONTROL = "android.ble.chair.control";
    public static final String RECEIVED = "android.ble.chair.received";

}
