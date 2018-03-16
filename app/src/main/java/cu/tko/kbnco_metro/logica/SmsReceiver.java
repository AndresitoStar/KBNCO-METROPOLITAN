package cu.tko.kbnco_metro.logica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            /* Get Messages */
            Object[] sms = (Object[]) intentExtras.get("pdus");

            for (int i = 0; i < sms.length; ++i) {
                /* Parse Each Message */
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String phone = smsMessage.getOriginatingAddress();
                if (phone != null && phone.equalsIgnoreCase("pagoxmovil")) {
                    String message = smsMessage.getMessageBody().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Detalles de la operacion");
                    builder.setMessage(message);
                    builder.create().show();
                }
            }
        }
    }
}
