package com.example.gaoxiong.follower;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class BleActivity extends ListActivity implements PermissionInterface {
    public BluetoothAdapter mBluetoothAdapter;
    //    BluetoothLeScanner scanner;
    public BluetoothDevice mBluetoothDevice;
    private Button button;
    UUID Uuid;
//    private BService bleService;
    //设备扫描
    private PermissionHelper mPermissionHelper;
    List<BluetoothDevice> devices = new ArrayList<>();
    /**
     * gatt的集合
     */
    private Map<String, BluetoothGatt> mBluetoothGatts;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();

            scanner.stopScan(leCallback);
        }
    };
    private Handler handler = new Handler();//import android.os.Handler;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
         BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothGatts = new HashMap<>();
        // 确保蓝牙在设备上可以开启(判断手机是否支持蓝牙和是否开启，没有开启则开启)
        if ( !mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.enable();///　/*隐式打开蓝牙*/
            }

        }
//        启动服务
////        初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();


        button = (Button) findViewById(R.id.mButton);
        button.setOnClickListener(new ButtonListener());

//        ArrayList<HashMap<String, String>> list = new ArrayList<>();
//        HashMap<String, String> map1 = new HashMap<>();
//        map1.put("user_name", "蓝牙名");
//        map1.put("user_id", "蓝牙地址");
//        list.add(map1);
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.user,
//                new String[]{"user_name", "user_id"}, new int[]{R.id.user_name, R.id.user_id});
//        setListAdapter(simpleAdapter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Ble Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。

    }

    private void initViews() {
        //已经拥有所需权限，可以放心操作任何东西了
    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction0());
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction0());

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction0() {
        Thing object = new Thing.Builder()
                .setName("Ble Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Logger.e("开始扫描");
            scanDevice(true);
        }
    }

    private void addDeviceToList() {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (Iterator iterator = devices.iterator(); iterator.hasNext(); ) {
            mBluetoothDevice = (BluetoothDevice) iterator.next();
            HashMap<String, String> map1 = new HashMap<String, String>();

            map1.put("user_name", mBluetoothDevice.getName());
            map1.put("user_id", mBluetoothDevice.getAddress());
            list.add(map1);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.user,
                new String[]{"user_name", "user_id"}, new int[]{R.id.user_name, R.id.user_id});
        setListAdapter(simpleAdapter);
    }

    //启动蓝牙服务
//    ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            bleService = ((BService.LocalBinder) iBinder).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            unbindService(serviceConnection);
//        }
//    };

    private  String Mac;

    //对点击的ListView进行监听
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        BluetoothDevice bluetoothDevice = devices.get(position);
        Mac = bluetoothDevice.getAddress();
//        bleService.disConnect();
//        bleService.refreshDeviceCache(mBluetoothGatts.get(position));
//        bleService.initialize();
//        bleService.connect(bluetoothDevice,Mac);
//        bleService.sendOrder("a");

        Intent intent =new Intent("android.ble.chair.control");
        intent.putExtra("address",Mac.toString());
        sendBroadcast(intent);

        Logger.e("点击地址>>>>>>>：" + Mac);

    }



    private void scanDevice(final boolean enable) {
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
//               final  BluetoothDevice device = result.getDevice();
                addDeviceToList();
//                Toast.makeText(getApplicationContext(), device.getAddress(), Toast.LENGTH_SHORT).show();
              Logger.e( "添加设备地址:"+device.getName()+device.getAddress());

                if (!devices.contains(device)) {  //判断是否已经添加
                    devices.add(device);
//                    addDeviceToList(device.getName() + "", device.getAddress());
//                    Logger.e("添加设备---地址:" + device.getAddress());
                }

//
//                deviceAdapter.notifyDataSetChanged();
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
