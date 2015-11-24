package com.example.administrator.cyut_molosport.Member;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.cyut_molosport.Alarm.AlarmActivity;
import com.example.administrator.cyut_molosport.Appconfig.AppController;
import com.example.administrator.cyut_molosport.Appconfig.BitmapCache;
import com.example.administrator.cyut_molosport.Appconfig.Config;
import com.example.administrator.cyut_molosport.Member.Chart.Chart;
import com.example.administrator.cyut_molosport.Member.Goal.Personal_info;
import com.example.administrator.cyut_molosport.Member.Goal.Personal_insert;
import com.example.administrator.cyut_molosport.R;
import com.example.administrator.cyut_molosport.Welcome;
import com.gc.materialdesign.views.ButtonRectangle;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import molosports.bike.BikeDataReceiveThread;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView ViewGoalKcal,ViewDate,ViewTDkcal,ViewTotalKcal;
    private ButtonRectangle btntobike;
    private Toolbar toolbar;
    private ProgressDialog pDialog;
    String account, date, username, Image;
    int GoalDate, GoalKcal, Todaykcal, TotalKcal;
    Double userWeight, userWantThin, userGoalWeight;

    private long exittime = 0;

    boolean start = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e(TAG);
        setContentView(R.layout.main);

        ViewGoalKcal = (TextView) findViewById(R.id.main_view_date_kcal);           //今日需消耗
        ViewDate = (TextView) findViewById(R.id.main_view_date);                    //今日目標天數
        ViewTDkcal = (TextView) findViewById(R.id.main_view_date_total_kcal);       //今日目標
        ViewTotalKcal = (TextView) findViewById(R.id.main_view_total_kcal);         //總共需消耗

        btntobike = (ButtonRectangle) findViewById(R.id.main_to_bike);

        SharedPreferences setting = getSharedPreferences("Preference",0);
        account = setting.getString("account","");
        searchPersonal(account);

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setCancelable(false);

        Search_Goal(account);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        date = sDateFormat.format(new java.util.Date());
        Logger.e(date);

        btntobike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TotalKcal == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("請先設定目標後再開始運動~");
                    builder.setPositiveButton("設定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, Personal_info.class));
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Bike.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Todaykcal",Todaykcal);
                    bundle.putInt("TotalKcal",TotalKcal);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }


    @Override
    protected void onRestart() {
        searchPersonal(account);
        Search_Goal(account);
        super.onRestart();
    }

    private void Search_Goal(final String account){

        final String req_string = "req_search";
        pDialog.setMessage("載入中");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Search_Goal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();
                int success;
                try {
                    search(account,date);

                    JSONObject json = new JSONObject(response);
                    success = json.getInt("success");
                    if (success == 1) {
                        userWeight     = Double.parseDouble(json.getString("userWeight"));
                        userGoalWeight = Double.parseDouble(json.getString("userGoalWeight"));
                        userWantThin   = Double.parseDouble(json.getString("userWantThin"));
                        //GoalDate       = json.getInt("userGoalDate");
                        Todaykcal      = json.getInt("userDataKcal");
                        TotalKcal      = Integer.parseInt(json.getString("userTotalKcal"));

                        if (TotalKcal == 0 && GoalDate ==0 ){           //如果設定體重與現在體重一樣，則顯示0
                            Todaykcal = 0;

                        }else {
                            //Todaykcal = TotalKcal/GoalDate;
                            GoalDate = TotalKcal / Todaykcal;
                        }

                    } else {
                        Log.d(TAG, "No Goal");
                        GoalKcal = 0;
                        GoalDate = 0;
                        Todaykcal=0;
                    }
                    ViewTotalKcal.setText(String.valueOf(TotalKcal));
                    ViewDate.setText(String.valueOf(GoalDate));
                    ViewTDkcal.setText(String.valueOf(Todaykcal));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
                Search_Goal(account);
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

    private void search(final String account, final String date){
        final String req_string = "req_search";
        showDialog();
        StringRequest strreq = new StringRequest(Request.Method.POST, Config.Main_Bike, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonArray= json.getJSONArray(account);

                    int array = jsonArray.length();
                    for (int i = 0 ; i < array ; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int kcal = jsonObject.getInt(date);
                        int kcaltoday = Todaykcal - kcal;
                        Logger.e(String.valueOf(kcaltoday));
                        if (kcaltoday<0){
                            ViewGoalKcal.setText(String.valueOf(0));
                        }else {
                            ViewGoalKcal.setText(String.valueOf(kcaltoday));
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e(error.toString());
                search(account, date);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("account",account);
                params.put("date",date);
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strreq, req_string);
    }

    private void searchPersonal(final String account){
        final String req_string = "search_personal";

        StringRequest sre_req = new StringRequest(Request.Method.POST, Config.Search_Personal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);

                try {
                    JSONObject json = new JSONObject(response);
                    username = json.getString("username");
                    Image = json.getString("image");
                    if (start == true){
                        drawable(); //側邊欄
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                searchPersonal(account);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("account",account);

                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(sre_req, req_string);
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


    private void drawable(){
        start = false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView name = (TextView) header.findViewById(R.id.name);
        name.setText("Hello " + username);

        ImageView imageView = (ImageView) header.findViewById(R.id.nav_image_view);

        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        //http://blog.csdn.net/guolin_blog/article/details/17482165
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,R.drawable.ic_action_articles, R.drawable.ic_action_articles);
        imageLoader.get(Config.getImage+ Image , listener);
        //指定图片允许的最大宽度和高度
        //imageLoader.get("http://developer.android.com/images/home/aw_dac.png",listener, 200, 200);



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            startActivity(new Intent(MainActivity.this, Personal_data.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this, Chart.class));
        } else if (id == R.id.nav_goal) {
            startActivity(new Intent(MainActivity.this, Personal_info.class));
        } else if (id == R.id.nav_clock) {
            startActivity(new Intent(MainActivity.this, AlarmActivity.class));
        } else if (id == R.id.nav_signup) {
            SharedPreferences setting = getSharedPreferences("Preference", 0);
            setting.edit().clear().commit();
            startActivity(new Intent(MainActivity.this, Welcome.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit(){
        if (System.currentTimeMillis() - exittime > 2000) {
            Toast.makeText(this, "在按一次退出MoloSport健康計畫", Toast.LENGTH_SHORT).show();
            exittime = System.currentTimeMillis();
        }else {
            new exitActivity();
            System.exit(0);
        }
    }

    class exitActivity implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }
}
