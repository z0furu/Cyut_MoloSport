package com.example.administrator.cyut_molosport.Member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.preference.EditTextPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.administrator.cyut_molosport.Appconfig.AppController;
import com.example.administrator.cyut_molosport.Appconfig.Config;
import com.example.administrator.cyut_molosport.R;
import com.example.administrator.cyut_molosport.utils.ProgressGenerator;
import com.orhanobut.logger.Logger;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements ProgressGenerator.OnCompleteListener{

    private static final String TAG = Login.class.getSimpleName();

    private Toolbar toolbar;

    private ActionProcessButton BtnSignIn;
    private MaterialEditText edtAccount, edtPassword;
    private Button btnRegister;
    final ProgressGenerator progressGenerator = new ProgressGenerator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Logger.e(TAG);

        edtAccount  = (MaterialEditText) findViewById(R.id.account);
        edtPassword = (MaterialEditText) findViewById(R.id.password);
        BtnSignIn   = (ActionProcessButton) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnSignUp);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BtnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String account = edtAccount.getText().toString();
                String password = edtPassword.getText().toString();
                MemberLogin(account, password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));

            }
        });
    }

    private void MemberLogin(final String account, final String password){
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.d(TAG,response.toString());
                int success;

                try{
                    JSONObject json = new JSONObject(response);
                    success = json.getInt("success");
                    if (success == 1) {
                        SharedPreferences setting = getSharedPreferences("Preference", 0);
                        setting.edit().putString("account", account).commit();

                        progressGenerator.start(BtnSignIn);
                        //Toast.makeText(Login.this, "登入成功", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Login.this, "帳號或密碼有誤", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d(TAG,"Login Error" + error.toString());
                Toast.makeText(getApplicationContext(),"請確認網路連線狀態，請再重新連線一次",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("account",account);
                params.put("password",password);

                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq,tag_string_req);
    }

    @Override
    public void onComplete() {
        startActivity(new Intent(Login.this, MainActivity.class));
        finish();
    }
}
