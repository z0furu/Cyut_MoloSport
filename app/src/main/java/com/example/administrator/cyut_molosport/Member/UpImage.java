package com.example.administrator.cyut_molosport.Member;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.administrator.cyut_molosport.Appconfig.Config;
import com.example.administrator.cyut_molosport.Appconfig.MultipartRequest;
import com.example.administrator.cyut_molosport.R;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class UpImage extends AppCompatActivity {

    private static final String TAG = UpImage.class.getSimpleName();

    private ImageView imageView;
    private Button btnOk;

    String account,imagePath;

    private String filePath = null;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_image);
        Logger.e(TAG);

        imageView = (ImageView) findViewById(R.id.imgPreView);
        btnOk = (Button) findViewById(R.id.btnUpload);

        SharedPreferences setting = getSharedPreferences("Preference", 0);
        account = setting.getString("account", "");

        Intent i = getIntent();
        filePath = i.getStringExtra("filePath");
        boolean isImage = i.getBooleanExtra("isImage", true);
        boolean isPhote = i.getBooleanExtra("isPhote",true);
        if (filePath != null) {
           // prePhote(isPhote);
            previewMedia(isImage);

        } else {
            Toast.makeText(getApplicationContext(), "File is missing", Toast.LENGTH_SHORT).show();
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upImage(account);
            }
        });
    }

    private void previewMedia(boolean isImage) {
        if (isImage) {
            Log.d("isImage", "isImage");
            imageView.setVisibility(View.VISIBLE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            Log.e("Image", filePath.toString());
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            imageView.setImageBitmap(bitmap);
        }
    }
/*    private void prePhote(boolean isPhote){
        if (isPhote){
            Log.d("Phote", "Phote");
            imageView.setVisibility(View.VISIBLE);
            try{
                BitmapFactory.Options mOptions = new BitmapFactory.Options();
                //Size=2為將原始圖片縮小1/2，Size=4為1/4，以此類推
                mOptions.inSampleSize = 2;
                ContentResolver cr = this.getContentResolver();
                uri = Uri.parse(filePath);
                Log.d("Phote", uri.toString());

                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri),null,mOptions);
                imageView.setImageBitmap(bitmap);
                if(Build.VERSION.SDK_INT<19){
                    phote18();
                }else {
                    phote19();

                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }


    private void phote19(){
        Uri selectedImage = uri;
        String wholeID = DocumentsContract.getDocumentId(selectedImage);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);

        }
        System.out.println("圖片=="+filePath);
        cursor.close();

    }
    private void phote18(){
        Log.e("版本:","<19");
        Uri selectedImage = uri;
        Log.e("phote18",selectedImage.toString());
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver()
                .query(selectedImage, filePathColumn, null,
                        null, null);
        cursor.moveToFirst();

        int columnIndex = cursor
                .getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);

        System.out.println("圖片="+filePath);
        cursor.close();

    }*/


    private void upImage(final String account) {

        File file = new File(filePath);
        Logger.d(file.toString());
        String tag = "uploadImage";
        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        //params.put("uploadImage", String.valueOf(file));

        MultipartRequest multipartRequest = new MultipartRequest(Config.Up_Image, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                upImage(account);
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.json(response);
                startActivity(new Intent(UpImage.this, Personal_data.class));
                finish();
            }
        }, tag, file, params);
        Volley.newRequestQueue(this).add(multipartRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}