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
public class GattCharacteristicsAdapter extends ArrayAdapter<GattCharacteristicsItem> {
    private final ArrayList<GattCharacteristicsItem> mGattCharacteristicsItems;

    private Context context;

    public GattCharacteristicsAdapter(Context context, int resource){
        super(context,resource);
        this.context = context;
        this.mGattCharacteristicsItems = new ArrayList<GattCharacteristicsItem>();
    }

    public void addItem(GattCharacteristicsItem item){
        mGattCharacteristicsItems.add(item);
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

        if (mGattCharacteristicsItems.size() == 0) {
            return view;
        }
        GattCharacteristicsItem item = mGattCharacteristicsItems.get(position);
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
