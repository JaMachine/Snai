package com.owa.snai.store.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.owa.snai.store.app.Main.act;

public class Web extends AppCompatActivity {

    public static String page;
    private int REQc = 2 - 1;
    private int RESc = 2 - 1;
    private WebView web;
    private ValueCallback<Uri> msg;
    private Uri ciUri = null;
    private TextView conny;
    boolean bbc;
    private IntentFilter f;
    private ValueCallback<Uri[]> fc;
    private String cpf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);


        conny = findViewById(R.id.conny);


        web = findViewById(R.id.web_view);
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Web.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        removeNavs();

// web
        web.getSettings().setLoadsImagesAutomatically(true);

        web.getSettings().setDomStorageEnabled(true);

        web.getSettings().setAllowFileAccess(true);


        if (savedInstanceState != null)
            web.restoreState(savedInstanceState.getBundle("webViewState"));

        web.setWebChromeClient(new WebChromeClient() {
            private File createImageFile() throws IOException {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File imageFile = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                );
                return imageFile;

            }

            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
                if (fc != null) {
                    fc.onReceiveValue(null);
                }
                fc = filePath;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", cpf);
                    } catch (IOException e) {
                        e.fillInStackTrace();
                    }
                    if (photoFile != null) {
                        cpf = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, REQc);
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                msg = uploadMsg;
                File imageStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES)
                        , "AndroidExampleFolder");
                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }
                File file = new File(
                        imageStorageDir + File.separator + "IMG_"
                                + String.valueOf(System.currentTimeMillis())
                                + ".jpg");
                ciUri = Uri.fromFile(file);
                final Intent captureIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, ciUri);
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                        , new Parcelable[]{captureIntent});
                startActivityForResult(chooserIntent, RESc);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType,
                                        String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });
        f = new IntentFilter();
        f.addAction(act);
        Intent intent = new Intent(this, netListener.class);
        startService(intent);
        if (net(getApplicationContext()))
            s();
        else h();
    }

    public BroadcastReceiver r = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(act)) {
                if (intent.getStringExtra("online_status").equals("true"))
                    s();
                else h();
            }
        }
    };


    // hd
    public void h() {

        conny.setVisibility(View.VISIBLE);

        bbc = false;

        web.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != REQc || fc == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    if (cpf != null) {
                        results = new Uri[]{Uri.parse(cpf)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            fc.onReceiveValue(results);
            fc = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != RESc || msg == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == RESc) {
                if (null == this.msg) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        result = data == null ? ciUri : data.getData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg.onReceiveValue(result);
                msg = null;
            }
        }
    }

    // undo
    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    //register
    @Override
    protected void onResume() {
        removeNavs();
        registerReceiver(r, f);
        super.onResume();
    }

    //remove
    private void removeNavs() {
        View screen = findViewById(R.id.yyy);
        screen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //register
    @Override
    protected void onRestart() {
        registerReceiver(r, f);
        removeNavs();
        super.onRestart();
    }

    //unregister
    @Override
    protected void onPause() {
        unregisterReceiver(r);
        removeNavs();
        super.onPause();
    }

    // check
    public boolean net(Context context) {
        ConnectivityManager m = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = m.getActiveNetworkInfo();
        if (i != null && i.isConnectedOrConnecting()) {
            return true;
        } else {
            {
                {
                    return false;
                }
            }
        }
    }


    // shw
    public void s() {
        if (!bbc) {

            bbc = true;

            conny.setVisibility(View.GONE);

            web.loadUrl(page);

            web.setVisibility(View.VISIBLE);

        }
    }
}