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
import android.widget.TextView;

import java.util.List;

import electrical.nus.com.angeldetector.R;

public class MainActivity extends AppCompatActivity {

    TextView bleaddressTextView;
    BluetoothDevice bluetoothDevice;

    private static final int GATT_STATE_DISCONNECTED = 0;
    private static final int GATT_STATE_CONNECTING = 1;
    private static final int GATT_STATE_CONNECTED = 2;
    private int gattState = GATT_STATE_DISCONNECTED;

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bleaddressTextView = (TextView)findViewById(R.id.bleaddressTextView);
    }

    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        assert(extras != null);
        bluetoothDevice = (BluetoothDevice)extras.getParcelable("bluetooth_device");
        bleaddressTextView.setText(bluetoothDevice.getAddress());

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
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState== BluetoothProfile.STATE_CONNECTED){
                gattState=GATT_STATE_CONNECTED;
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
