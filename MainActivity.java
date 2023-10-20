package com.example.telnet123;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.telnet123.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView imgView;
    String imageUrl = "http://kiokahn.synology.me:30000/uploads/-/system/appearance/logo/1/Gazzi_Labs_CI_type_B_-_big_logo.png"; // 접속할 URL
    Bitmap bmImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = findViewById(R.id.imgView);
    }

    public void onClickForLoad(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap loadedImage = downloadImage(imageUrl);
                if (loadedImage != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgView.setImageBitmap(loadedImage);
                            Toast.makeText(getApplicationContext(), "Load", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
    public void onClickForSave(View v) {
        if (bmImg != null) {
            if (saveBitmaptoJpeg(bmImg, "DCIM", "image")) {
                Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to save image", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No image to save", Toast.LENGTH_LONG).show();
        }
    }
    private Bitmap downloadImage(String imageUrl) {
        try {
            URL myFileUrl = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folder_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + folder_name;
        Log.d("경로", string_path);
        File file_path = new File(string_path);

        if (!file_path.exists()) {
            file_path.mkdirs();
        }
        File file = new File(file_path, file_name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
