package sga.securetravel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.SmsManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static double latitude;
    public static double longitude;
    String number,name,message;
    AppLocationService appLocationService;
    Location nwLocation,gpsLocation;
    Timer timer;
    boolean flag;
    public static boolean fragmentVisible;

    TextView contact_confirm_message_name;
    TextView contact_confirm_message_number;
    TextView contact_confirm_message_1;
    TextView welcome_head;
    TextView welcome_body;

    Button choose_contact;
    Button choose_another_contact;
    Button confirm_contact;
    Button send_location;
    Button send_single_update;
    Button send_continuous_update;
    Button back_button;

    final String PREFS_NAME = "PrefsStorage";
    SharedPreferences settings;
    SharedPreferences.Editor settingsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(PREFS_NAME, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contact_confirm_message_name = (TextView) findViewById(R.id.contact_confirm_message_name);
        contact_confirm_message_number = (TextView) findViewById(R.id.contact_confirm_message_number);
        contact_confirm_message_1 = (TextView)findViewById(R.id.contact_confirm_message_1);
        choose_contact=(Button)findViewById(R.id.choose_contact);
        choose_another_contact = (Button)findViewById(R.id.choose_another_contact);
        confirm_contact=(Button)findViewById(R.id.confirm_contact);
        welcome_body=(TextView)findViewById(R.id.welcome_body);
        welcome_head=(TextView)findViewById(R.id.welcome_head);
        send_location=(Button)findViewById(R.id.send_location);
        send_single_update=(Button)findViewById(R.id.send_single_update);
        send_continuous_update=(Button)findViewById(R.id.send_continuous_update);
        back_button=(Button)findViewById(R.id.back);

        flag=false;
        fragmentVisible=false;

        if (settings.getBoolean("is_contact_set", false)) {
            sendSMSMode();
        }

        appLocationService = new AppLocationService(MainActivity.this);
    }

    public void locationThread() {
        final LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();

                if (!service.isProviderEnabled(LocationManager.GPS_PROVIDER) && !service.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

                flag = false;

                while(!flag) {
                    if (service.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                        if (nwLocation != null) {
                            flag = true;
                            latitude = nwLocation.getLatitude();
                            longitude = nwLocation.getLongitude();
                        }
                    }

                    if (!flag && service.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
                        if (gpsLocation != null) {
                            flag = true;
                            latitude = gpsLocation.getLatitude();
                            longitude = gpsLocation.getLongitude();
                        }
                    }
                }

                    for (int i = 0; i < 5; i++) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                        }
                }
            }
        }).start();
    }

    public void sendSMSMode() {
        fragmentVisible=true;
        name=settings.getString("selected_contact_name", "defaultName");
        number=settings.getString("selected_contact_number","defaultNumber");
        contact_confirm_message_name.setText("Name : "+name);
        contact_confirm_message_number.setText("Number : " + number);
        welcome_head.setVisibility(View.INVISIBLE);
        welcome_body.setVisibility(View.INVISIBLE);
        choose_contact.setVisibility(View.INVISIBLE);
        choose_another_contact.setVisibility(View.VISIBLE);
        choose_another_contact.setText("Change emergency contact");
        send_location.setVisibility(View.VISIBLE);
        contact_confirm_message_1.setVisibility(View.VISIBLE);
        contact_confirm_message_number.setVisibility(View.VISIBLE);
        contact_confirm_message_name.setVisibility(View.VISIBLE);
        locationThread();
    }

    public void sendSMS() {
        SmsManager manager = SmsManager.getDefault();
        if(nwLocation==null && gpsLocation==null) {
            message = "I am in danger, HELP!";
            manager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(
                    getApplicationContext(),
                    "Could not retrieve location - Message sent without location. Stay safe!",
                    Toast.LENGTH_LONG).show();
        }
        else {
            message = "I am in danger, HELP! https://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
            manager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(
                    getApplicationContext(),
                    "Message successfully sent. Stay safe!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void singleSend(View v) {
        sendSMS();
        backAction(v);
    }

    public void continuousSend(View v) {
        back_button.setText("Terminate Continuous Location Update");
        send_single_update.setVisibility(View.INVISIBLE);
        send_continuous_update.setVisibility(View.INVISIBLE);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        sendSMS();
                    }
                });
            }
        }, 0, 60000);
    }

    public void chooseSend(View v) {
        send_location.setVisibility(View.INVISIBLE);
        choose_another_contact.setVisibility(View.INVISIBLE);
        send_single_update.setVisibility(View.VISIBLE);
        send_continuous_update.setVisibility(View.VISIBLE);
        back_button.setVisibility(View.VISIBLE);
    }

    public void backAction(View v) {
        if(timer!=null) timer.cancel();
        back_button.setText(R.string.back);
        send_location.setVisibility(View.VISIBLE);
        choose_another_contact.setVisibility(View.VISIBLE);
        send_single_update.setVisibility(View.INVISIBLE);
        send_continuous_update.setVisibility(View.INVISIBLE);
        back_button.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent about_intent;
            about_intent=new Intent(this,AboutActivity.class);
            startActivity(about_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSelectedNumber(String number, String name) {
        choose_contact.setVisibility(View.INVISIBLE);
        contact_confirm_message_name.setText("Name : "+name);
        contact_confirm_message_name.setVisibility(View.VISIBLE);
        contact_confirm_message_number.setText("Number : "+number);
        contact_confirm_message_number.setVisibility(View.VISIBLE);
        contact_confirm_message_1.setVisibility(View.VISIBLE);
        confirm_contact.setVisibility(View.VISIBLE);
        choose_another_contact.setVisibility(View.VISIBLE);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                        Cursor c = null;
                        try {

                            c = getContentResolver()
                                    .query(uri,
                                            new String[]{
                                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                                    ContactsContract.CommonDataKinds.Phone.TYPE,
                                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                            null, null, null);

                            if (c != null && c.moveToFirst()) {
                                number = c.getString(0);
                                number = number.replace("-", "").replace(" ", "")
                                        .replace("(", "").replace(")", "").replace(".", "");
                                name = c.getString(2);
                                showSelectedNumber(number,name);
                            }
                        } finally {
                            if (c != null) {
                                c.close();
                            }
                        }
                    }
                }
    }

    public void chooseContact(View v) {
        if(send_location.getVisibility()==View.VISIBLE)
        {
            send_location.setVisibility(View.INVISIBLE);
            confirm_contact.setVisibility(View.VISIBLE);
        }
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    public void saveContact(View v){
        settingsEditor=settings.edit();
        settingsEditor.putString("selected_contact_name",name).commit();
        settingsEditor.putString("selected_contact_number",number).commit();
        settingsEditor.putBoolean("is_contact_set", true).commit();
        confirm_contact.setVisibility(View.INVISIBLE);
        sendSMSMode();
    }
}
