package com.ltt.android.sites.webviewscreenshot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.java.lib.oil.file.FileUtils;
import com.ltt.android.lib.url2bitmap.Url2Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CaptureWebViewActivity extends AppCompatActivity implements TextWatcher, Runnable {
    @BindView(R.id.activity_capture_web_view)
    protected WebView view;

    @BindView(R.id.activity_capture_web_view_url)
    protected EditText url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capture_web_view);

        ButterKnife.bind(this);

        url.addTextChangedListener(this);
        afterTextChanged(url.getText());
    }

    public void onCapture(View view) {
        new Thread(this).start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        view.loadUrl(url.getText().toString());
    }

    @Override
    public void run() {
        Bitmap bitmap = Url2Bitmap.Builder().context(this).view(view).timeout(3000).get();
        if (bitmap != null) {
            File bitmapFile = FileUtils.getInstance().locateFile(getExternalCacheDir(), "screenshot", CaptureWebViewActivity.class.getSimpleName() + "_" + System.currentTimeMillis() + ".png");
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(bitmapFile));
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Observable.just(bitmapFile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> Toast.makeText(this, getString(R.string.activity_capture_web_view_screenshot_success, file.getAbsolutePath()), Toast.LENGTH_LONG).show());
        }
    }
}
