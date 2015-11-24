package com.example.administrator.cyut_molosport.Alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import com.example.administrator.cyut_molosport.Alarm.preference.AlarmPreferencesActivity;
import com.example.administrator.cyut_molosport.Alarm.service.AlarmServiceBroadcastReciever;
import com.example.administrator.cyut_molosport.R;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2015/10/14.
 */
public abstract class BaseActivity  extends AppCompatActivity implements android.view.View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = null;
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_item_new:
                Intent newAlarmIntent = new Intent(this, AlarmPreferencesActivity.class);
                startActivity(newAlarmIntent);
                break;
		/*case R.id.menu_item_rate:
			url = "market://details?id=" + getPackageName();
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the market", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.menu_item_website:
			url = "http://www.neilson.co.za";
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the website", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.menu_item_report:

			url = "https://github.com/SheldonNeilson/Android-Alarm-Clock/issues";
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the bug reporting website", Toast.LENGTH_LONG).show();
			}

			/*
			Intent send = new Intent(Intent.ACTION_SENDTO);
			String uriText;

			String emailAddress = "dontusemyemailaddress@yourdomain.com";
			String subject = R.string.app_name + " Bug Report";
			String body = "Debug:";
			body += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
			body += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
			body += "\n Device: " + android.os.Build.DEVICE;
			body += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
			body += "\n Screen Width: " + getWindow().getWindowManager().getDefaultDisplay().getWidth();
			body += "\n Screen Height: " + getWindow().getWindowManager().getDefaultDisplay().getHeight();
			body += "\n Hardware Keyboard Present: " + (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);

			uriText = "mailto:" + emailAddress + "?subject=" + subject + "&body=" + body;

			uriText = uriText.replace(" ", "%20");
			Uri emalUri = Uri.parse(uriText);

			send.setData(emalUri);
			startActivity(Intent.createChooser(send, "Send mail..."));

			break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    protected void callMathAlarmScheduleService() {
        Intent mathAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
        sendBroadcast(mathAlarmServiceIntent, null);
    }
}