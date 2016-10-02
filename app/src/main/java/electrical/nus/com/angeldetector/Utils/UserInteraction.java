package electrical.nus.com.angeldetector.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Lee Han Young on 02-Oct-16.
 */
public class UserInteraction {
    public static void showAlert(String msg, String title, Context context){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

}
