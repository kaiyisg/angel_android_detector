package electrical.nus.com.angeldetector.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import electrical.nus.com.angeldetector.R;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceItem;
import electrical.nus.com.angeldetector.adapter.GattServiceAdapter;
import electrical.nus.com.angeldetector.adapter.GattServiceItem;
import electrical.nus.com.angeldetector.services.SrvDictionary;

public class MainActivity extends AppCompatActivity {

    TextView bleaddressTextView;
    TextView gattStatusTextview;
    BluetoothDevice bluetoothDevice;
    ListView gattServicesListView;
    GattServiceAdapter mGattServiceAdapter;
    SrvDictionary srvDictionary;

    private static final int GATT_STATE_DISCONNECTED = 0;
    private static final int GATT_STATE_CONNECTED = 1;
    private static final int GATT_STATE_DISCOVERED_SERVICES = 2;
    private static final int GATT_STATE_NO_DISCOVERED_SERVICES = 3;
    private int gattState = GATT_STATE_DISCONNECTED;

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bleaddressTextView = (TextView)findViewById(R.id.bleaddressTextView);
        gattStatusTextview = (TextView)findViewById(R.id.gattStatusTextview);
        gattServicesListView = (ListView)findViewById(R.id.gattServicesListView);
        mGattServiceAdapter = new GattServiceAdapter(this,R.layout.list_item);
        gattServicesListView.setAdapter(mGattServiceAdapter);
        srvDictionary = new SrvDictionary();

    }

    private void setViewState(int state){
        if(state==GATT_STATE_DISCONNECTED){
            gattState=GATT_STATE_DISCONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_connection_not_connected);
                }
            });
        }else if(state==GATT_STATE_CONNECTED){
            gattState=GATT_STATE_CONNECTED;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_connection_connected);
                }
            });
        }else if(state==GATT_STATE_DISCOVERED_SERVICES){
            gattState=GATT_STATE_DISCOVERED_SERVICES;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_discovered_gatt_services);
                }
            });
        }else if(state==GATT_STATE_NO_DISCOVERED_SERVICES){
            gattState=GATT_STATE_NO_DISCOVERED_SERVICES;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gattStatusTextview.setText(R.string.status_no_discovered_gatt_services);
                }
            });
        }
    }

    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        assert(extras != null);
        bluetoothDevice = (BluetoothDevice)extras.getParcelable("bluetooth_device");
        bleaddressTextView.setText(bluetoothDevice.getAddress());

        setViewState(GATT_STATE_DISCONNECTED);

        connect(bluetoothDevice);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            displaySignalStrength(0);
        }
        unscheduleUpdaters();
        mBleDevice.disconnect();*/
    }

    private void connect(BluetoothDevice bluetoothDevice){
        bluetoothDevice.connectGatt(this,false,mGattCallback);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setViewState(GATT_STATE_DISCOVERED_SERVICES);
                final List<BluetoothGattService> services = gatt.getServices();
                /*for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                }*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(BluetoothGattService service:services){
                            GattServiceItem newService = new GattServiceItem(
                                    srvDictionary.map.get(service.getUuid()),
                                    service.getUuid().toString(),
                                    service);
                            mGattServiceAdapter.add(newService);
                            mGattServiceAdapter.addItem(newService);
                            mGattServiceAdapter.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                setViewState(GATT_STATE_NO_DISCOVERED_SERVICES);
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState== BluetoothProfile.STATE_CONNECTED){
                setViewState(GATT_STATE_CONNECTED);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        gatt.discoverServices());
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gattState = GATT_STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

}
