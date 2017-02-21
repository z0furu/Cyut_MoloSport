package com.example.administrator.cyut_molosport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.cyut_molosport.Member.Login;
import com.example.administrator.cyut_molosport.Member.MainActivity;
import com.orhanobut.logger.Logger;

/**
 *判斷是否有登入過
 */
public class Welcome extends AppCompatActivity {

    private SharedPreferences setting;

    private static final String TAG = Welcome.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        Logger.e(TAG);
        setting = getSharedPreferences("Preference",0);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    Thread.sleep(0);
                    if (setting.getString("account","").isEmpty()){
                         startActivity(new Intent(Welcome.this, Login.class));
                         finish();
                    }else {
                        startActivity(new Intent(Welcome.this,MainActivity.class));
                        finish();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
