package com.ltt.android.sites.webviewscreenshot;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.java.lib.oil.file.FileUtils;
import com.ltt.android.lib.url2bitmap.Url2Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.content_main_web_view)
    protected WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(MainActivity.class.getSimpleName(), "onCreate.UL5555LP.DI1211, enter.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        webView.loadUrl("http://www.bvbcode.com/cn/36t8vp19-2710044");

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
        }
        else {
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
        }
        else if (id == R.id.nav_gallery) {

        }
        else if (id == R.id.nav_slideshow) {

        }
        else if (id == R.id.nav_manage) {

        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 保存图片到文件
     * @param bitmap 需要保存的图片对象
     * @param name 目标文件名称
     */
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
