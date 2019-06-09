package com.example.gaoxiong.follower;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.List;


public class StartScan {
    private BluetoothAdapter mBluetoothAdapter;
    private StartScan(){}
    public  static  StartScan getInstance(){
        return  SingletonHolder.sInstance;
    }
    private static class SingletonHolder{
       private static final StartScan sInstance = new StartScan();
    }
    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter){
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

   static List<BluetoothDevice> devices = new ArrayList<>();
   static List<String> address = new ArrayList<>();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();

            scanner.stopScan(leCallback);
        }
    };
    private Handler handler = new Handler();


    public void scanDevice(final boolean enable) {
        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {

            handler.postDelayed(runnable, 10000);

            scanner.startScan(leCallback);
        }



    }

    ScanCallback leCallback = new ScanCallback() {
        //leCallback是一个回调函数，通过onScanResult()把每次搜索到的设备添加到本地。
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothDevice device = result.getDevice();
                Logger.e( "添加设备地址:"+device.getName()+device.getAddress());

                if (!devices.contains(device)) {  //判断是否已经添加
                    devices.add(device);
                    address.add(device.getAddress());
                }
            }
        }

        //批量处理搜索到的结果
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);


        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            //扫描失败返回的参数有4个
            //errorCode=1;Fails to start scan as BLE scan with the same settings is already started by the app.
            //errorCode=2;Fails to start scan as app cannot be registered.
            //errorCode=3;Fails to start scan due an internal error
            //errorCode=4;Fails to start power optimized scan as this feature is not supported.
            switch(errorCode){
                case 1:
                    Logger.d("Fails to start scan as BLE scan with the same settings is already started by the app.");
                    break;
                case  2:
                    Logger.d("Fails to start scan as app cannot be registered.");
                    break;
                case 3:
                    Logger.d("Fails to start scan due an internal error");
                    break;
                case 4:
                    Logger.d("Fails to start power optimized scan as this feature is not supported.");
                    break;


            }
        }
    };


}
