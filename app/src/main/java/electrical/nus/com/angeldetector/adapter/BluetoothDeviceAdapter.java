package electrical.nus.com.angeldetector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import electrical.nus.com.angeldetector.R;

/**
 * Created by Lee Han Young on 01-Oct-16.
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDeviceItem> {

    private final ArrayList<BluetoothDeviceItem> mBluetoothDevicesItems;
    private Context context;

    public BluetoothDeviceAdapter(Context context, int resource){
        super(context,resource);
        this.context = context;
        this.mBluetoothDevicesItems = new ArrayList<BluetoothDeviceItem>();
    }

    public void addItem(BluetoothDeviceItem item){
        mBluetoothDevicesItems.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            try {
                view = getInflator().inflate(R.layout.list_item, parent, false);
            } catch (Exception e) {
                return null; //TODO: handle exception
            }
        }

        if (mBluetoothDevicesItems.size() == 0) {
            return view;
        }
        BluetoothDeviceItem item = mBluetoothDevicesItems.get(position);
        if (item != null) {

            TextView acountNameView = (TextView) view.findViewById(R.id.item_name);
            if (acountNameView != null) {
                acountNameView.setText(item.getItemName());
            }

            TextView itemKeyView = (TextView) view.findViewById(R.id.item_key);
            if (itemKeyView != null) {
                itemKeyView.setText(item.getItemKey());
            }
        }

        return view;
    }


    private LayoutInflater getInflator() {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
