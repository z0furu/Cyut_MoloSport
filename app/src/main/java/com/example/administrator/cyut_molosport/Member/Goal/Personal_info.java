package com.example.administrator.cyut_molosport.Member.Goal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.cyut_molosport.Appconfig.AppController;
import com.example.administrator.cyut_molosport.Appconfig.Config;
import com.example.administrator.cyut_molosport.Member.MainActivity;
import com.example.administrator.cyut_molosport.R;
import com.gc.materialdesign.views.ButtonRectangle;
import com.orhanobut.logger.Logger;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Personal_info extends AppCompatActivity {

    private static final String TAG = Personal_info.class.getSimpleName();

    private MaterialEditText mage,mheight,mweight;
    private RadioButton msex, msex1;
    private ButtonRectangle confirm;
    private Button btnsetgoal;
    private TextView tvStatus;

    private Toolbar toolbar;
    private ProgressDialog pDialog;

    String account;
    String height,weight,age,BMI;

    boolean setGoal ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);
        Logger.e(TAG);

        mage    = (MaterialEditText) findViewById(R.id.edtAge);
        mheight = (MaterialEditText) findViewById(R.id.edtHeight);
        mweight = (MaterialEditText) findViewById(R.id.edtWeight);

        tvStatus = (TextView) findViewById(R.id.status);

        msex    = (RadioButton) findViewById(R.id.radMale);
        msex1   = (RadioButton) findViewById(R.id.radFemale);

        confirm = (ButtonRectangle) findViewById(R.id.btnOK);
        btnsetgoal = (Button) findViewById(R.id.btnsetgoal);

        SharedPreferences setting = getSharedPreferences("Preference",0);
        account = setting.getString("account","");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Search(account);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(mage.getText().toString().trim()) || "".equals(mheight.getText().toString()) || "".equals(mweight.getText().toString().trim())) {
                    if ("".equals(mage.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "請輸入年齡", Toast.LENGTH_SHORT).show();
                    } else if ("".equals(mheight.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "請輸入身高", Toast.LENGTH_SHORT).show();
                    } else if ("".equals(mweight.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "請輸入體重", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String Age = mage.getText().toString();
                    String Height = mheight.getText().toString();
                    String Weight = mweight.getText().toString();
                    String sex;
                    if (msex.isChecked()) {
                        sex = "男";
                    } else {
                        sex = "女";
                    }
                    setGoal = false;
                    UpdatePersonalInfo(account, Age, Height, Weight, sex);

                }
            }
        });

        btnsetgoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(mage.getText().toString().trim()) || "".equals(mheight.getText().toString()) || "".equals(mweight.getText().toString().trim())) {
                    if ("".equals(mage.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "請輸入年齡", Toast.LENGTH_SHORT).show();
                    } else if ("".equals(mheight.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "請輸入身高", Toast.LENGTH_SHORT).show();
                    } else if ("".equals(mweight.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "請輸入體重", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String Age = mage.getText().toString();
                    String Height = mheight.getText().toString();
                    String Weight = mweight.getText().toString();
                    String sex;
                    if (msex.isChecked()) {
                        sex = "男";
                    } else {
                        sex = "女";
                    }
                    setGoal = true;
                    UpdatePersonalInfo(account, Age, Height, Weight, sex);

                }
            }
        });
    }

    private void Search(final String account){

        final String req_string = "req_search";
        pDialog.setMessage("載入中");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Search_Personal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();

                try {
                    JSONObject json = new JSONObject(response);
                    age = json.getString("userAge");
                    height = json.getString("userHeight");
                    weight = json.getString("userWeight");
                    BMI    = json.getString("userBMI");
                    String sex = json.getString("userSex");

                    if ("男".equals(sex)){
                        msex.setChecked(true);
                    }else {
                        msex1.setChecked(true
                        );
                    }

                    mage.setText(age);
                    mheight.setText(height);
                    mweight.setText(weight);

                    if (Double.parseDouble(BMI)<18.5){
                        tvStatus.setText("已經夠瘦囉~維持運動保持身體健康");
                        tvStatus.setTextColor(getResources().getColor(R.color.BMINormal));
                    }else if (Double.parseDouble(BMI)>=24){
                        tvStatus.setText("過度肥胖，請開始運動!!");
                        tvStatus.setTextColor(getResources().getColor(R.color.BMIFat));
                    }else {
                        tvStatus.setText("體重在標準值內~繼續保持!!");
                        tvStatus.setTextColor(getResources().getColor(R.color.BMIThin));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"Error : " + error.toString());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                hideDialog();
                Search(account);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("account",account);
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq, req_string);
    }

    private void UpdatePersonalInfo(final String account, final String userAge, final String userHeight, final String userWeight, final String sex ){

        String req_string = "req_Update";
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Update_Personal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();
                int success;
                try {
                    JSONObject json = new JSONObject(response);
                    success = json.getInt("success");
                    if (setGoal == true){
                        if (success == 1) {
                            Logger.d("Personal_exist");
                            Intent intent = new Intent();
                            intent.setClass(Personal_info.this, Personal_insert.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("success", success);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            Logger.d("Personal_Insert");
                            Intent intent = new Intent();
                            intent.setClass(Personal_info.this, Personal_insert.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("success", success);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        startActivity(new Intent(Personal_info.this, MainActivity.class));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"Error " + error.toString());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("account",account);
                params.put("age",userAge);
                params.put("height",userHeight);
                params.put("weight",userWeight);
                params.put("sex",sex);
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq,req_string);
    }

    private void showDialog(){
        if (!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hideDialog(){
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
