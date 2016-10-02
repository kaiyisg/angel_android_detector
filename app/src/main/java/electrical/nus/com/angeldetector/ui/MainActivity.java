package electrical.nus.com.angeldetector.ui;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import electrical.nus.com.angeldetector.R;

public class MainActivity extends AppCompatActivity {

    String mBleDeviceAddress;
    TextView bleaddressTextView;

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
        mBleDeviceAddress = extras.getString("ble_device_address");
        bleaddressTextView.setText(mBleDeviceAddress);

        //connect(mBleDeviceAddress);
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
}
