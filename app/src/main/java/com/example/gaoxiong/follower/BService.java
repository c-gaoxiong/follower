package com.example.gaoxiong.follower;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import com.orhanobut.logger.Logger;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
@SuppressLint("NewApi")
public class BService extends Service {
    public final static String ACTION_GATT_CONNECTED =
            "com.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.bluetooth.le.EXTRA_DATA";
    private static final String STATE_DISCONNECTED = 0 + "";
    private static final String STATE_CONNECTING = 1 + "";
    private static final String STATE_CONNECTED = 2 + "";
    private String mConnectionState = STATE_DISCONNECTED;
    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private static final String TAG = "BLEService";
    protected static final String ACTION_RECEIVED = "com.bluetooth.le.ACTION_RECEIVED";
    public String Mac;
    public BluetoothGatt bluetoothGatt;
    public BluetoothGattService mBluetoothGattService;
    public BluetoothGattCharacteristic characteristicWrite;
    BluetoothDevice mBluetoothDevice;


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BService getService() {
            return BService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean initialize() {

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e("BleSevice", "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("initialize", "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }



    private ChairControlReceiver mReceiver;

    public static IntentFilter getAction() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.ble.chair.control");
        return intentFilter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new ChairControlReceiver();
        registerReceiver(mReceiver, getAction());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        initialize();
        Logger.d("开启服务");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("BleSevice__onDestroy", "执行了onDestroy()方法");
        super.onDestroy();

    }

    @SuppressLint("NewApi")
    public class ChairControlReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String control = intent.getStringExtra("control");
            String address = intent.getStringExtra("address");


            if (action.equalsIgnoreCase(BleUUID.CHAIR_CONTROL)) {

                if (control != null) {
                    Logger.d(control);
                    sendOrder(control);
                }
                if (address != null) {
                    Logger.d(address);
                    connect(address);
                }
            }
        }
    }

    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String address = gatt.getDevice().getAddress();
            Intent i = new Intent(ACTION_GATT_CONNECTED);
            i.putExtra("address", address);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Logger.e("连接成功");
                // Toast.makeText(getApplicationContext(),"蓝牙连接成功",Toast.LENGTH_SHORT).show();
                // reConnect = false;
                mConnectionState = STATE_CONNECTED;

                i.putExtra("data", mConnectionState);
                sendBroadcast(i);
//	                mConnectionState = STATE_CONNECTED;
//	                broadcastUpdate(intentAction);
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.e("断开连接的状态");
                mConnectionState = STATE_DISCONNECTED;
                i.putExtra("data", mConnectionState);
                sendBroadcast(i);
                // Toast.makeText(getApplicationContext(),"蓝牙连接失败",Toast.LENGTH_SHORT).show();
                // 自动断开后进行重连 每次连接之前都关闭对应的通道！
                // disConnect();
                // connect(mBluetoothDevice,Mac);
                // Logger.e("Mac：" + Mac);
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.e("status" + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.d("onServicesDiscovered--" + "发现服务");
//                获取全部服务， gatt.getServices();
                List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
                Logger.d(bluetoothGattServices.size());
                Logger.d(gatt.getServices().get(0));
                Logger.d(gatt.getServices().get(1));
                Logger.d(gatt.getServices().get(2));
//获取对应的BleUUID.UUID_BLUE_SERVICE服务
                mBluetoothGattService = gatt
                        .getService(BleUUID.UUID_BLUE_SERVICE);
                characteristicWrite = mBluetoothGattService.getCharacteristic(BleUUID.UUID_BLUE_WRITE);
                setCharacteristicNotification(gatt.getServices().get(2)
                        .getCharacteristic(BleUUID.UUID_BLUE_WRITE), true);
//
                gatt.getService(UUID.randomUUID());
                for (BluetoothGattService bluetoothGattService :
                        bluetoothGattServices) {
                    // Logger.d("进入for 循环");

                    List<BluetoothGattCharacteristic>
                            bluetoothGattCharacteristics =
                            bluetoothGattService.getCharacteristics();
                    Logger.d("onServicesDiscovered--遍历特征值=" + bluetoothGattCharacteristics);
                    UUID uuid = bluetoothGattService.getUuid();
                    Logger.d("onServicesDiscovered--服务的uuid=" + uuid);//服务

                 /*获取指定服务uuid的特征值*/

//
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic
                            : bluetoothGattCharacteristics) {
                        setCharacteristicNotification(bluetoothGattCharacteristic, true);
                        Logger.d("onServicesDiscovered--服务的特征值的uuid=" +
                                bluetoothGattCharacteristic.getUuid());

                        // Logger.d("onServicesDiscovered--指定服务uuid的特征值不为空=--");
                        final int charaProp =
                                bluetoothGattCharacteristic.getProperties();

                        //
//                 bluetoothGattCharacteristic.getWriteType()==BluetoothGattCharacteristic.PROPERTY_READ;
                 /*如果该字符串可读*/
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) >
                                0) {
                            Logger.d("onServicesDiscovered--字符串可读--");
                            byte[] value = new byte[20];
                            bluetoothGattCharacteristic.setValue(value[0],
                                    BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                            String writeBytes = "a";
                            bluetoothGattCharacteristic.setValue(writeBytes.getBytes());
                        }
                        if
                                (gatt.setCharacteristicNotification(bluetoothGattCharacteristic,
                                true)) {
                            Logger.d("onServicesDiscovered--设置通知成功=--" + uuid);
                        }
                 /*3.再从指定的Characteristic中，我们可以通过getDescriptor()方法来获取该特征所包含的descriptor
                 以上的BluetoothGattService、BluetoothGattCharacteristic、BluetoothGattDescriptor。
                 我们都可以通过其getUuid()方法，来获取其对应的Uuid，从而判断是否是自己需要的。*/
                        List<BluetoothGattDescriptor> bluetoothGattDescriptors =
                                bluetoothGattCharacteristic.getDescriptors();
                        Logger.d("onServicesDiscovered--遍历Descriptor=");
                        for (BluetoothGattDescriptor bluetoothGattDescriptor :
                                bluetoothGattDescriptors) {
                            Logger.d("onServicesDiscovered--Descriptor的uuid=" +
                                    bluetoothGattDescriptor.getUuid());
                            // bluetoothGattDescriptor.getValue();

                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.d("onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_RECEIVED, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            broadcastUpdate(ACTION_RECEIVED, characteristic);
            Logger.d("onCharacteristicChanged" + new String(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Logger.d("onCharacteristicWrite" + characteristic.getValue()
                    .toString());

        }

    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {

        final Intent intent = new Intent(action);
//	        intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS,characteristic.get );
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (BleUUID.UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, format + "");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, format + "");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(byteChar);
//		            Log.d(TAG, stringBuilder.toString());
                intent.putExtra(EXTRA_DATA, new String(data));
            }
        }
        sendBroadcast(intent);
    }

    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        Logger.d("setCharacteristicNotification", "setCharacteristicNotification");
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic
                .getDescriptor(BleUUID.UUID_NOTIFY);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * 清理本地的BluetoothGatt 的缓存，以保证在蓝牙连接设备的时候，设备的服务、特征是最新的
     *
     * @param gatt
     * @return
     */
    public boolean refreshDeviceCache(BluetoothGatt gatt) {
        if (gatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = gatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod(
                        "refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = (Boolean) localMethod
                            .invoke(localBluetoothGatt);
                    return bool;
                }
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }

        return false;
    }

    public boolean disConnect() {
        if (bluetoothGatt != null) {
            refreshDeviceCache(bluetoothGatt);
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            Log.e("disConnect", "执行了断开连接");
            bluetoothGatt = null;
        }

        return true;
    }

    private void shutdownService(boolean disablePlugins) {
        stopSelf();
    }

    public void connect(final String Mac) {

        this.Mac = Mac;
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(Mac);
        //保证每次连接都要搜索设备
//        mBluetoothDevice = null;
        Logger.e("执行了连接的操作");
        if (!Mac.equals("")) {
//        说明已经绑定过设备  搜索是在子线程中执行的，当他搜索到指定的设备了，我们才对他进去连接！
            if (bluetoothGatt != null) {
                if (!Objects.equals(bluetoothGatt.getDevice().getAddress(), Mac)) {
                    bluetoothGatt = mBluetoothDevice.connectGatt(getApplicationContext(), true, callback);
                }
            }else  {
                bluetoothGatt = mBluetoothDevice.connectGatt(getApplicationContext(), true, callback);
            }
        }
    }

    //向蓝牙设备发送指令，疑问：向那个蓝牙设备发送的？
    public void sendOrder(final String strValue) {
        Logger.d("进入sendOrder");
        if (bluetoothGatt != null && characteristicWrite != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    characteristicWrite.setValue(strValue.getBytes());
                    bluetoothGatt.writeCharacteristic(characteristicWrite);
                }
            }).start();
        } else {
            Logger.d("找不到对应的通道，找不到对应写入的特征");
        }
    }
}
