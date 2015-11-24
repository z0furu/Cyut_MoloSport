package com.example.administrator.cyut_molosport.Member;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.cyut_molosport.Appconfig.AppController;
import com.example.administrator.cyut_molosport.Appconfig.Config;
import com.example.administrator.cyut_molosport.R;
import com.gc.materialdesign.views.ButtonRectangle;
import com.orhanobut.logger.Logger;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private static final String TAG = Register.class.getSimpleName();

    private MaterialEditText acc, pass, user, mage, mheight, mweight;
    private ButtonRectangle register, Back;
    private RadioButton msex;
    private ProgressDialog pDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Logger.e(TAG);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        acc = (MaterialEditText) findViewById(R.id.edtAccount);
        pass = (MaterialEditText) findViewById(R.id.password);
        user = (MaterialEditText) findViewById(R.id.edtUsername);
        mage = (MaterialEditText) findViewById(R.id.edtAge);
        mheight = (MaterialEditText) findViewById(R.id.edtHeight);
        mweight = (MaterialEditText) findViewById(R.id.edtWeight);

        msex = (RadioButton) findViewById(R.id.radFemale);

        register = (ButtonRectangle) findViewById(R.id.btnRegister);
        Back = (ButtonRectangle) findViewById(R.id.btnBack);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ("".equals(acc.getText().toString().trim()) || "".equals(pass.getText().toString().trim()) ||
                        "".equals(user.getText().toString().trim()) || "".equals(mage.getText().toString().trim()) ||
                        "".equals(mheight.getText().toString().trim()) || "".equals(mweight.getText().toString())) {
                    if ("".equals(acc.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "帳號不得為空", Toast.LENGTH_LONG).show();
                    } else if ("".equals(pass.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "密碼不得為空", Toast.LENGTH_LONG).show();
                    } else if ("".equals(user.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "暱稱不得為空", Toast.LENGTH_LONG).show();
                    } else if ("".equals(mage.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "年齡不得為空", Toast.LENGTH_LONG).show();
                    } else if ("".equals(mheight.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "身高不得為空", Toast.LENGTH_LONG).show();
                    } else if ("".equals(mweight.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "體重不得為空", Toast.LENGTH_LONG).show();
                    }

                } else {
                    String account = acc.getText().toString();
                    String password = pass.getText().toString();
                    String username = user.getText().toString();
                    String age = mage.getText().toString();
                    String height = mheight.getText().toString();
                    String weight = mweight.getText().toString();
                    String sex;
                    if (msex.isChecked()) {
                        sex = "女";
                    } else {
                        sex = "男";
                    }
                    registerUser(account, password, username, age, height, weight, sex);
                }
            }
        });
    }

    private void registerUser(final String account, final String password, final String username,
                              final String age, final String height, final String weight, final String sex) {

        String tag_string_req = "req_register";

        pDialog.setMessage("註冊中");
        showDialog();

        StringRequest strRep = new StringRequest(Request.Method.POST, Config.Register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                int success;
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt("success");

                    if (success == 1) {
                        Log.d("Register", "success");
                        startActivity(new Intent(Register.this, Login.class));
                        Toast.makeText(Register.this, "註冊成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.d("Register", "error");
                        Toast.makeText(getApplicationContext(), "此帳號已註冊過!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Register Error:  " + error.toString());
                Toast.makeText(getApplicationContext(), "請確認網路連線狀態，請再重新連線一次", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("account", account);
                params.put("password", password);
                params.put("username", username);
                params.put("age", age);
                params.put("height", height);
                params.put("weight", weight);
                params.put("sex", sex);

                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strRep, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}