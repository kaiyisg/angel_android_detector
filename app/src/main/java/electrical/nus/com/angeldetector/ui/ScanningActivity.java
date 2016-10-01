package electrical.nus.com.angeldetector.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import electrical.nus.com.angeldetector.R;
import electrical.nus.com.angeldetector.adapter.BluetoothDeviceAdapter;

public class ScanningActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private ListView bluetoothDeviceListView;
    BluetoothAdapter mBluetoothAdapter;
    private BluetoothDeviceAdapter mLeDeviceListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);


        bluetoothDeviceListView = (ListView)findViewById(R.id.listView);
        if(this.mLeDeviceListAdapter==null){
            mLeDeviceListAdapter = new BluetoothDeviceAdapter(this,0);
        }

        //initializing bluetooth manager
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }

    @Override
    protected void onResume(){
        super.onResume();

        //checking if bluetooth is on or not
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
}
