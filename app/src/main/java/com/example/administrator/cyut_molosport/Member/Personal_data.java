package com.example.administrator.cyut_molosport.Member;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.cyut_molosport.Appconfig.AppController;
import com.example.administrator.cyut_molosport.Appconfig.BitmapCache;
import com.example.administrator.cyut_molosport.Appconfig.Config;
import com.example.administrator.cyut_molosport.R;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 個人資訊畫面
 */
public class Personal_data extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private static final String TAG = Personal_data.class.getSimpleName();
    private static final float PERCENTAFE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAFE_TO_HIDE_TITLE_DETAILS    = 0.3f;
    private static final int   ALPHA_ANIMATIONS_DURATION           = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private ImageView mImageparallax;
    private FrameLayout mFrameParallax;
    private Toolbar mToolbar;

    private TextView mHeight,mWeight,mname,mBMI,mStWeight,txtTip1,txtTip2,txtTip3;
    private ImageView ViewImage;
    private ProgressDialog pDialog ;

    String account;
    String name,BMI;
    Double height,weight;
    int min,max;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private final static int CAMERA_CAPTURE_PHOTE_REQUEST_CODE = 99;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri; // file url to store image/video

    String Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Logger.e(TAG);

        bindActivity();

        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(mToolbar);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        initParallaxValues();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        SharedPreferences setting = getSharedPreferences("Preference",0);
        account = setting.getString("account","");

        Search_Personal_Data(account);
        Search_Image(account);

        ViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Personal_data.this);
                builder.setPositiveButton("相機", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureImage();
                    }
                });
               builder.setNeutralButton("從相簿選擇", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, CAMERA_CAPTURE_PHOTE_REQUEST_CODE);

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        if (!isDeviceSupportCamera()){
            Toast.makeText(getApplicationContext(),"抱歉，您的裝置不支援項機",Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void bindActivity(){

        //CollapsingToolbarLayout
        mToolbar        = (Toolbar) findViewById(R.id.personal_toolbar);
        mTitle          = (TextView) findViewById(R.id.personal_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.personal_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.personal_appbar);
        mImageparallax  = (ImageView) findViewById(R.id.personal_imageview_placeholder);
        mFrameParallax  = (FrameLayout) findViewById(R.id.personal_framelayout_title);

        //PersonalData
        mname     = (TextView) findViewById(R.id.name);
        mHeight   = (TextView) findViewById(R.id.txtYourHeight);
        mWeight   = (TextView) findViewById(R.id.txtYourWeight);
        mBMI      = (TextView) findViewById(R.id.txtYourBMI);
        mStWeight = (TextView) findViewById(R.id.txtYourBMIRange);
        txtTip1   = (TextView) findViewById(R.id.txtTip1);
        txtTip2   = (TextView) findViewById(R.id.txtTip2);
        txtTip3   = (TextView) findViewById(R.id.txtTip3);
        ViewImage = (ImageView) findViewById(R.id.viewImage);

    }

    private void initParallaxValues(){          //呼叫動畫函式
        CollapsingToolbarLayout.LayoutParams petDetailsLp =
                (CollapsingToolbarLayout.LayoutParams) mImageparallax.getLayoutParams();

        CollapsingToolbarLayout.LayoutParams petBackgroundLp =
                (CollapsingToolbarLayout.LayoutParams) mFrameParallax.getLayoutParams();

        petDetailsLp.setParallaxMultiplier(0.9f);
        petBackgroundLp.setParallaxMultiplier(0.3f);

        mImageparallax.setLayoutParams(petDetailsLp);
        mFrameParallax.setLayoutParams(petBackgroundLp);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage){
        if (percentage >= PERCENTAFE_TO_SHOW_TITLE_AT_TOOLBAR){

            if (!mIsTheTitleVisible){
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        }else {
            if (mIsTheTitleVisible){
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage){
        if (percentage >= PERCENTAFE_TO_HIDE_TITLE_DETAILS){
            if (mIsTheTitleContainerVisible){
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        }else {
            if (!mIsTheTitleContainerVisible){
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility){
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void Search_Image(final String account){        //搜尋個人大頭照
        String req_string = "req_searchImage";

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Sr_Image, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());
                try {
                    JSONObject json = new JSONObject(response);
                    Image = json.getString("image");
                    displayImg();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"Error: " + error.toString());
                Search_Image(account);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("account",account);
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq,req_string);
    }
    public void displayImg(){           //顯示個人圖片
        ImageView imageView = (ImageView)this.findViewById(R.id.viewImage);
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        //http://blog.csdn.net/guolin_blog/article/details/17482165
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,R.drawable.ic_action_articles, R.drawable.ic_action_articles);
        imageLoader.get(Config.getImage + Image , listener);
        //指定圖片允许的最大寬度和高度
        //imageLoader.get("http://developer.android.com/images/home/aw_dac.png",listener, 200, 200);
    }

    private void Search_Personal_Data(final String account){        //找尋個人資料

        String req_string = "req_search_personal_data";
        pDialog.setMessage("載入中");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.Search_Personal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               Logger.json(response);
                hideDialog();

                try {
                    JSONObject json = new JSONObject(response);
                    name = json.getString("username");
                    height = Double.parseDouble(json.getString("userHeight"));
                    weight = Double.parseDouble(json.getString("userWeight"));
                    BMI = json.getString("userBMI");

                    min = (int) ((height - 80) * 0.7 * 0.9);
                    max = (int) ((height - 80) * 0.7 * 1.1);
                    String spend = min + " ~ " + max;

                    mname.setText(name);
                    mHeight.setText(String.valueOf(height.intValue()));
                    mWeight.setText(String.valueOf(weight.intValue()));
                    mBMI.setText(BMI);
                    mStWeight.setText("健康體重範圍 : "+"       "+spend);

                    Double bmi = Double.parseDouble(BMI);
                    if (bmi <= 18.5) {
                        txtTip1.setText("已經夠瘦了~");
                        txtTip2.setText("請繼續保持健康");
                        txtTip3.setText("每週至少運動三次，每次最少三十分鐘");
                        mBMI.setTextColor(getResources().getColor(R.color.BMIThin));
                    } else if (bmi >= 24) {
                        txtTip1.setText("有點過胖喔~");
                        txtTip2.setText("請多運動保持身體健康");
                        txtTip3.setText("每週至少運動三次，每次最少三十分鐘");
                        mBMI.setTextColor(getResources().getColor(R.color.BMIFat));
                    } else {
                        txtTip1.setText("體重已經在標準範圍內~");
                        txtTip2.setText("請繼續保持~");
                        txtTip3.setText("每週至少運動三次，每次最少三十分鐘");
                        mBMI.setTextColor(getResources().getColor(R.color.BMINormal));
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e(error.toString());
                Search_Personal_Data(account);
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

    private boolean isDeviceSupportCamera(){        //判斷是否有相機
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                launchUploadActivity(true);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "取消拍攝", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "抱歉，不能開啟相機", Toast.LENGTH_SHORT)
                        .show();
            }
        }else if (requestCode ==CAMERA_CAPTURE_PHOTE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                Log.e("uri", uri.toString());

                fileUri = uri;
                Intent i = new Intent(Personal_data.this, UpImage.class);
                i.putExtra("filePath", fileUri.toString());
                i.putExtra("isPhote", true);
                startActivity(i);
                finish();
            }
        }
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(Personal_data.this, UpImage.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
        finish();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir + File.separator + "IMG_" + timeStamp + ".jpg");
        }
        else{
            return null;
        }
        return mediaFile;
    }
}
