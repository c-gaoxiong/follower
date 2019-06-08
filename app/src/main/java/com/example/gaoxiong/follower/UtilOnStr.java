package com.example.gaoxiong.follower;

import android.bluetooth.BluetoothGatt;

import com.orhanobut.logger.Logger;

import java.util.Locale;

/**
 * Created by gaoxiong on 2018/7/23.
 */
public class UtilOnStr {


    public static String parseBytesToHexString(byte[] bytes) {

        String ret = "";
        for (byte item : bytes) {
            String hex = Integer.toHexString(item & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase(Locale.CHINA);
        }
        return ret;
    }

    // 将16进制的字符串转换为字节数组
    public static byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }
    /**
     *写数据
     * @param address：目标设备的address
     * @param data:要发送的内容
     */


}
