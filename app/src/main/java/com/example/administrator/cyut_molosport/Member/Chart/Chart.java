package com.example.administrator.cyut_molosport.Member.Chart;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.cyut_molosport.Appconfig.AppController;
import com.example.administrator.cyut_molosport.Appconfig.Config;
import com.example.administrator.cyut_molosport.R;
import com.gc.materialdesign.views.Slider;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chart extends AppCompatActivity  implements Slider.OnValueChangedListener,OnChartValueSelectedListener {


    private static final String TAG = Chart.class.getSimpleName();

    String account;
    String kkcal = "todayTotalKcal";
    String kcal,date;

    HashMap<String, String> map = new HashMap<>();

    int num;

    private ProgressDialog pDialog;
    private Slider slider;
    private BarChart mChart;
    boolean chartzoom = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Logger.e(TAG);
        slider = (Slider) findViewById(R.id.sliderNumber);

        mChart = (BarChart) findViewById(R.id.chart1);

        SharedPreferences settings = getSharedPreferences("Preference",0);
        account = settings.getString("account","");

        mChart.setDescription("");
        mChart.setNoDataText("您還有任何運動紀錄");

        //if more than 60 entries are displayed in the chart, no values will be
        //drawn
        mChart.setMaxVisibleValueCount(25);

        mChart.setPinchZoom(true); //如果禁止，縮放可以在X軸及Y軸分別進行
        mChart.setScaleEnabled(true); //縮放

        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mChart.setMarkerView(mv);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        mChart.getAxisLeft().setDrawGridLines(false);

        mChart.animateY(2500);

        mChart.getLegend().setEnabled(false);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        BarChartVolley();

        slider.setValue(1);
        slider.setOnValueChangedListener(this);


    }

    private void BarChartVolley(){
        String str_req = "bar_chart";
        pDialog.setMessage("Loading...");
        showDialog();


        StringRequest req = new StringRequest(Request.Method.POST, Config.Bike_Chart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                hideDialog();

                try {

                    ArrayList<BarEntry> yVals = new ArrayList<>();
                    ArrayList<String> xVals = new ArrayList<>();

                    JSONObject json = new JSONObject(response);

                    JSONArray jsonArray = json.getJSONArray(account);
                    num = jsonArray.length();
                    for (int i = 0; i < slider.getValue(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Logger.json(jsonObject.toString());
                        kcal = jsonObject.getString(kkcal);
                        date = jsonObject.getString("date");
                        //Log.e(TAG, kcal);

                        map.put("kcal", kcal);
                        map.put("date", date);
                        //Log.e("Hash", map.get("date"));
                        xVals.add(map.get("date"));
                        yVals.add(new BarEntry(Float.parseFloat(map.get("kcal")), i));

                    }
                    Logger.e(String.valueOf(num));
                    slider.setMax(num);

                    /*if ((slider.getValue()>6) ){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Chart.this);
                        builder.setMessage("點擊圖表兩下可放大圖表，兩指縮放圖表大小");

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }*/


                    BarDataSet set1 = new BarDataSet(yVals, "Data Set");
                    set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
                    set1.setDrawValues(false);
                    //Log.d("Yvals", yVals.toString());
                    ArrayList<BarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);

                    BarData data = new BarData(xVals, dataSets);
                    //Log.d("Xvals",xVals.toString());

                    mChart.setData(data);
                    mChart.invalidate();

                    for (DataSet<?> set : mChart.getData().getDataSets())     //顯示數值
                        set.setDrawValues(!set.isDrawValuesEnabled());
                    mChart.invalidate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d(TAG, error.toString());
                BarChartVolley();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("account",account);
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(req,str_req);
    }

    @Override
    public void onValueChanged(int value) {
        BarChartVolley();
        mChart.invalidate();
        Log.d(TAG, "value Change");
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
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Logger.e(e.toString());
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

