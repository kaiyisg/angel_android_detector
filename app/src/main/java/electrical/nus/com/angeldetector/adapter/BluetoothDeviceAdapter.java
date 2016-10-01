package electrical.nus.com.angeldetector.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee Han Young on 01-Oct-16.
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private List<BluetoothDevice> bluetoothDevices;
    private Context context;

    public BluetoothDeviceAdapter(Context context, int resource){
        super(context,resource);
        this.context = context;
        bluetoothDevices = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {
    }
}
