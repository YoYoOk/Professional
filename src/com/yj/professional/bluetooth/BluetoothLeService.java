package com.yj.professional.bluetooth;

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
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.yj.professional.utils.ConvertUtils;


/**
 * @author liaoyao
 * ͨ��BLE  API�������BLE�豸����
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    
    public static List<Byte> tempList = new ArrayList<Byte>();
    
    private BluetoothManager mBluetoothManager;//����������
    private BluetoothAdapter mBluetoothAdapter;//����������
    private String mBluetoothDeviceAddress;//�����豸��ַ
    private BluetoothGatt mBluetoothGatt;//���ô�ʵ����ɶ�GATT�ͻ��˵Ĳ���
    private int mConnectionState = STATE_DISCONNECTED;//��������״̬  ���ã�

    private static final int STATE_DISCONNECTED = 0;//��������״̬  δ����  0
    private static final int STATE_CONNECTING = 1;//��������״̬  ��������   1
    private static final int STATE_CONNECTED = 2;//��������״̬  ������  2

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";//gatt ����
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";//gatt  δ����
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
    		"com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";//gatt  ���� δ����
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";//���ݿ���
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";//extra  data

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.�����к����Ļص�����
    //ͨ��BLE  API�Ĳ�ͬ���͵Ļص����� -������״̬�ı䡢  ���߷��ַ���
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override//���������豸������ȥ����ʱ��ص��ú���
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {//������״̬�����ı�
            String intentAction;
          //�յ��豸notifyֵ ���豸�ϱ�ֵ��
            if (newState == BluetoothProfile.STATE_CONNECTED) {//�������豸�Ѿ�����
                intentAction = ACTION_GATT_CONNECTED;//com.example.bluetooth.le.ACTION_GATT_CONNECTED
                mConnectionState = STATE_CONNECTED;//  =2
                broadcastUpdate(intentAction);//���¹㲥  ��ʾ��ǰ�豸��������  ���͹㲥 ���߹㲥������
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//���豸�޷�����
                intentAction = ACTION_GATT_DISCONNECTED;//com.example.bluetooth.le.ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED;// = 0
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }
        //�����·����  //���豸�Ƿ��ҵ�����ʱ������ص��� ����
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {//0
            	//�ҵ�������    ��������ԶԷ�����н�����Ѱ�ҵ�����Ҫ�ķ���
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);//com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
                System.out.println("onServicesDiscovered received: " + status);
            }
        }
        //��д����  ����ȡ�豸��ʱ�� ��ص��ú���
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	//��ȡ��ֵ�������������
            if (status == BluetoothGatt.GATT_SUCCESS) {//0
            	broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                //��ȡ�������ݴ���characteristic���У�����ͨ��characteristic.getValue();����ȡ����Ȼ���ٽ��н���������  
                //int charaProp = characteristic.getProperties();
                //if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)��ʾ�ɷ���֪ͨ��  �жϸ�Characteristic���� 
            }
        }
        //�豸����֪ͨ��ʱ�� ����øýӿ� �Ĵ˷���
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };
    
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.e(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.�������е��ļ���д��ʮ�����Ƹ�ʽ���ļ�
        	//�����ȡ������
            final byte[] data = characteristic.getValue();
            
            if (data != null && data.length > 0) {
            	            	
            	intent.putExtra(EXTRA_DATA, data);
            	
              /*  //���������ж��Ƿ���2 ���ֽڻ���4���ֽ�  Ȼ������4���ֽ�  ��2���ֽ��ֽڵķ��͹㲥
                if(data.length == 4){
                	intent.putExtra(EXTRA_DATA, new byte[]{data[0],data[1]});
                	sendBroadcast(intent);//�ȷ���  Ȼ���ٸ�ֵ��һ�ε�
                	intent.putExtra(EXTRA_DATA, new byte[]{data[2],data[3]});
                }else{
                	intent.putExtra(EXTRA_DATA, data);
                }*/
            }
        }
        //���͹㲥���㲥���������ģ���
        sendBroadcast(intent);
    }
    
    //�ж��Ƿ��ǽ�����
    public boolean isEnd(byte[] data){
    	return ConvertUtils.bytesToHexString(data).equals("ff01");
    }
    
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     * ��ʼ����ʱ���ж��Ƿ�֧������   ���������������
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;//��֧��4.0������
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;//�����豸��֧������
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.  ��ǰ���ӵ��豸������������
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
        //˵���ǵ�һ������  �豸
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");//�豸û���ҵ�  �޷�����
            return false;
        }
        
        if (mBluetoothGatt != null) {
        	mBluetoothGatt.close();
        }
     //����ǰ�ȹر�֮ǰ ��  ò��Ҳ�޷������
        
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.//�˴�����������ȥ�뵱ǰ����ģ������  �ڶ������� �Ƿ��Զ����� ѡ��false
        //���ӳɹ���֮�� ��ص�BluetoothGattCallback �ӿڣ���������ȡ�豸�����豸��д���ݼ��豸����֪ͨ�ȶ���ص��ýӿڣ����бȽ���Ҫ����BluetoothGatt
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        System.out.println("device.getBondState=="+device.getBondState());
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        refreshDeviceCache(mBluetoothGatt);
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    //��ȡ���ݵĺ���
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    
  //д��ָ����characteristic
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     * ��ȡ�������豸֧�ֵ�����GATT���񼯺�
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    public BluetoothGattService getSupportedGattServices(UUID uuid) {
    	BluetoothGattService mBluetoothGattService;
    	if (mBluetoothGatt == null) return null;
    	mBluetoothGattService=mBluetoothGatt.getService(uuid);
        return mBluetoothGattService;
    } 
    
    /**
     * �����ص�BluetoothGatt �Ļ��棬�Ա�֤�����������豸��ʱ���豸�ķ������������µ�
     * @param gatt
     * @return
     */
    public boolean refreshDeviceCache(BluetoothGatt gatt) {
        if(null != gatt){
            try {
                BluetoothGatt localBluetoothGatt = gatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod( "refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(
                            localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
        return false;
    }
    
}
