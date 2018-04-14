package com.ltt.android.sites.webviewscreenshot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.java.lib.oil.file.FileUtils;
import com.ltt.android.lib.url2bitmap.Url2Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.content_main_web_view)
    protected WebView webView;

    protected WebView view;
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(MainActivity.class.getSimpleName(), "onCreate.UL5555LP.DI1211, enter.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        handler = new Handler();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        webView.loadUrl("http://www.bvbcode.com/cn/36t8vp19-2710044");

//        view = new WebView(this);
//        view.setInitialScale(100);
//        view.setVerticalScrollBarEnabled(false);
//        view.getSettings().setBuiltInZoomControls(false);
//        view.getSettings().setSupportZoom(false);
//
//        view.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                Log.i(MainActivity.class.getSimpleName(), "onCreate.onPageFinished.UL5555LP.DI1211, enter");
//                super.onPageFinished(view, url);
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        MainActivity.this.view.measure(View.MeasureSpec.makeMeasureSpec(384, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                        MainActivity.this.view.layout(0, 0, MainActivity.this.view.getMeasuredWidth(), MainActivity.this.view.getMeasuredHeight());
//                        Log.i(MainActivity.class.getSimpleName(), "onCreate.onPageFinished.UL5555LP.DI1211, view.getMeasuredWidth(): " + MainActivity.this.view.getMeasuredWidth() + "; view.getMeasuredHeight(): " + MainActivity.this.view.getMeasuredHeight());
//
//                        Bitmap bitmap = Bitmap.createBitmap(MainActivity.this.view.getMeasuredWidth(), MainActivity.this.view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//                        Canvas canvas = new Canvas(bitmap);
//                        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, 0));
//                        MainActivity.this.view.draw(canvas);
//
//                        saveBitmap(bitmap, "web_view");
//                        Toast.makeText(MainActivity.this, "onCreate.onPageFinished.DI1211", Toast.LENGTH_LONG).show();
//                        finish();
//                    }
//                }, 200);
//            }
//        });
//
//        view.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//                Log.i(MainActivity.class.getSimpleName(), "onCreate.onProgressChanged.UL5555LP.DI1211, newProgress: " + newProgress);
//            }
//        });
//
//        Log.i(MainActivity.class.getSimpleName(), "onCreate.UL5555LP.DI1211, new WebView load.");
//        view.loadUrl("http://docs.groovy-lang.org/latest/html/gapi/groovy/json/JsonSlurper.html");


        new Thread(() -> {
            Log.i(MainActivity.class.getSimpleName(), "onCreate.UL5555LP.DI1211, getBitmap call next line");
            Bitmap bitmap = Url2Bitmap.getBitmap(this, "https://zhidao.baidu.com/question/1823614687845736468.html?fr=iks&word=FutureTask&ie=gbk", 384);
            Log.i(MainActivity.class.getSimpleName(), "onCreate.UL5555LP.DI1211, bitmap != null: " + (bitmap != null));
            saveBitmap(bitmap, "Url2Bitmap");
        }).start();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveBitmap(Bitmap bitmap, String name) {
        File screenshot = FileUtils.getInstance().locateFile(getExternalCacheDir(), "screenshot", name + "_" + System.currentTimeMillis() + ".png");
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(screenshot));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
