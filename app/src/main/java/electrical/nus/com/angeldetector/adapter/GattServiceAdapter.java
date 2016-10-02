package electrical.nus.com.angeldetector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import electrical.nus.com.angeldetector.R;

/**
 * Created by Lee Han Young on 02-Oct-16.
 */
public class GattServiceAdapter extends ArrayAdapter<GattServiceItem> {

    private final ArrayList<GattServiceItem> mGattServiceItems;

    private Context context;

    public GattServiceAdapter(Context context, int resource){
        super(context,resource);
        this.context = context;
        this.mGattServiceItems = new ArrayList<GattServiceItem>();
    }

    public void addItem(GattServiceItem item){
        mGattServiceItems.add(item);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            try {
                view = getInflator().inflate(R.layout.list_item, parent, false);
            } catch (Exception e) {
                return null; //TODO: handle exception
            }
        }

        if (mGattServiceItems.size() == 0) {
            return view;
        }
        GattServiceItem item = mGattServiceItems.get(position);
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
