package sga.securetravel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    String message1="I am in danger, HELP!";
                    String message2= message.substring(0,Math.min(message.length(),21));
                    if(message2.equals(message1))
                    {
                        MediaPlayer mp;
                        mp=MediaPlayer.create(context,R.raw.siren);
                        mp.start();
                        Toast toast1 = Toast.makeText(context,
                                senderNum + " is in danger! See received SMS to locate them", Toast.LENGTH_LONG);
                        toast1.show();
                    }
                }
            }

        }
        catch (Exception e) { }
    }
}