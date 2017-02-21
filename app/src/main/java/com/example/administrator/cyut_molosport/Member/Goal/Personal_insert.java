package com.example.administrator.cyut_molosport.Member.Goal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 新增個人減重目標
 */
public class Personal_insert extends AppCompatActivity {

    private static final String TAG = Personal_insert.class.getSimpleName();

    private TextView txtBMI,txtBMR,txtStWeight,txtSt;
    private ButtonRectangle btnset;
    private Button  btnBMI, btnBMR,btnWeight, btnThin;
    private Toolbar toolbar;


    private ProgressDialog pDialog;


    String account;
    Double height,weight,age;
    String sex,BMI,wantweight;
    int BMR,min,max;
    int TotalKcal,GoalDate;

    Double numpick = 0.1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_insert);
        Logger.e(TAG);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtBMI      = (TextView) findViewById(R.id.txtBMI);
        txtBMR      = (TextView) findViewById(R.id.txtBMR);
        txtStWeight = (TextView) findViewById(R.id.txtstandard);
        txtSt       = (TextView) findViewById(R.id.ViewSt);

        btnWeight = (Button) findViewById(R.id.btnwantweight);
        btnThin = (Button) findViewById(R.id.btnNumberPick);
        btnset = (ButtonRectangle) findViewById(R.id.btnSet);
        btnBMI = (Button) findViewById(R.id.btnBMI);
        btnBMR = (Button) findViewById(R.id.btnBMR);

        SharedPreferences setting = getSharedPreferences("Preference",0);
        account = setting.getString("account","");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Bundle bundle = this.getIntent().getExtras();
        final int success = bundle.getInt("success");

        Search_Personal(account);

        btnBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Personal_insert.this);
                final View view = inflater.inflate(R.layout.txtbmi, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Personal_insert.this);
                builder.setTitle("BMI");
                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnBMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Personal_insert.this);
                final View view = inflater.inflate(R.layout.txtbmr, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Personal_insert.this);
                builder.setTitle("基礎代謝率(BMR)");
                builder.setView(view);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnThin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Personal_insert.this);
                final View view = inflater.inflate(R.layout.numberpick, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Personal_insert.this);
                builder.setView(view);


                final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.thin_numberpick1);
                numberPicker.setValue(1);
                numberPicker.setMaxValue(9);
                numberPicker.setMinValue(1);

                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        numpick = Double.valueOf(newVal)/10;
                        btnThin.setText(String.valueOf(numpick));
                    }
                });

                builder.setNegativeButton("設定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(Personal_insert.this);
                final View view =  inflater.inflate(R.layout.numberpick2,null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Personal_insert.this);
                builder.setTitle("目標體重");
                builder.setView(view);

                final NumberPicker WeightNumber1 = (NumberPicker) view.findViewById(R.id.weight_number1);
                final NumberPicker WeightNumber2 = (NumberPicker) view.findViewById(R.id.weight_number2);

                if (Double.parseDouble(BMI)<18.5){
                    WeightNumber1.setValue(weight.intValue());
                    Logger.d(String.valueOf(weight));
                    WeightNumber1.setMaxValue(min);
                    WeightNumber1.setMinValue(weight.intValue());

                }else if (weight<min) {
                    WeightNumber1.setValue(weight.intValue());
                    WeightNumber1.setMaxValue(min);
                    WeightNumber1.setMinValue(min-10);
                } else {
                    WeightNumber1.setValue(weight.intValue());
                    WeightNumber1.setMaxValue(weight.intValue());
                    WeightNumber1.setMinValue(min);
                }

                WeightNumber2.setValue(0);
                WeightNumber2.setMaxValue(9);
                WeightNumber2.setMinValue(0);


                builder.setNegativeButton("設定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Double weightNumber1 = Double.valueOf(WeightNumber1.getValue());
                        Double weightNumber2 = Double.valueOf(WeightNumber2.getValue());
                        Double total = weightNumber1+ (weightNumber2/10);
                        wantweight = String.valueOf(total);
                        Logger.e(String.valueOf(total));
                        btnWeight.setText(wantweight);
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


                    }
            });

        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e(String.valueOf(success));

                Double WantWeight = Double.valueOf(wantweight);

                GoalDate = (int) ((weight-WantWeight)/(numpick/7));
                Logger.d(String.valueOf(GoalDate));
                TotalKcal = GoalDate*BMR;
                if (success == 1 ){
                    Update_Goal(account);       //如果資料庫有資料，就會更新
                }else {
                    Insert_Goal(account);       //如果資料庫沒有資料，就會執行新增的動作
                }
            }
        });



    }
    private void Search_Personal(final String account){

        final String req_string = "req_search_personal";
        pDialog.setMessage("載入中");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Search_Personal,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();
                try {

                    JSONObject json = new JSONObject(response);
                    height = Double.parseDouble(json.getString("userHeight"));
                    weight = Double.parseDouble(json.getString("userWeight"));
                    age = Double.parseDouble(json.getString("userAge"));
                    sex = json.getString("userSex");
                    BMI = json.getString("userBMI");

                    min = (int) ((height - 80) * 0.7 * 0.9);
                    max = (int) ((height - 80) * 0.7 * 1.1);
                    String spend = min + " ~ " + max;

                    if ("男".equals(sex)) {
                        BMR = (int) (((13.7 * weight) + (5.0 * height) - (6.8 * age)) + 66);
                        Logger.e(sex);
                    } else {
                        BMR = (int) (((9.6 * weight) + (1.8 * height) - (4.7 * age)) + 655);
                        Logger.e(sex);
                    }
                    wantweight = String.valueOf(weight);
                    btnWeight.setText(String.valueOf((weight).intValue()));
                    txtBMI.setText(BMI);
                    txtBMR.setText(String.valueOf(BMR));
                    txtStWeight.setText("健康體重範圍 :　"+spend+""+"公斤");

                    if (Double.parseDouble(BMI)<18.5){
                        txtSt.setText("已經夠瘦囉~維持運動保持身體健康");
                    }else if (Double.parseDouble(BMI)>=24){
                        txtSt.setText("過度肥胖，請開始運動!!");
                    }else {
                        txtSt.setText("體重在標準值內~繼續保持!!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d(TAG,"Error" + error.toString());
                Search_Personal(account);
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

    private void Insert_Goal(final String account){
        String req_strint = "req_Insert";
        pDialog.setMessage("新增中");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Insert_Goal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();
                int success;
                try {

                    JSONObject json = new JSONObject(response);
                    success = json.getInt("success");
                    if (success == 1) {
                        startActivity(new Intent(Personal_insert.this, MainActivity.class));
                        finish();
                    } else {
                        Log.d(TAG, "error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"Error :" + error.toString());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("account",account);
                params.put("userGoalWeight",wantweight);
                params.put("userWantThin",String.valueOf(numpick));
                params.put("userDataKcal",String.valueOf(BMR));
                params.put("userWeight",String.valueOf(weight));
                params.put("userTotalKcal", String.valueOf(TotalKcal));
                params.put("userGoalDate", String.valueOf(GoalDate));
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq,req_strint);
    }

    private void Update_Goal(final String account){
        String req_strint = "req_Insert";
        pDialog.setMessage("新增中");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Update_Goal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();
                int success;
                try {

                    JSONObject json = new JSONObject(response);
                    success = json.getInt("success");
                    if (success == 1) {
                        startActivity(new Intent(Personal_insert.this, MainActivity.class));
                        finish();
                    } else {
                        Log.d(TAG, "error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"Error :" + error.toString());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("account",account);
                params.put("userGoalWeight",wantweight);
                params.put("userWantThin",String.valueOf(numpick));
                params.put("userDataKcal",String.valueOf(BMR));
                params.put("userWeight",String.valueOf(weight));
                params.put("userTotalKcal", String.valueOf(TotalKcal));
                params.put("userGoalDate",String.valueOf(GoalDate));
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq, req_strint);
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
