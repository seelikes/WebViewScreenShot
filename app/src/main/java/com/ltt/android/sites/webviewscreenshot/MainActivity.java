package com.ltt.android.sites.webviewscreenshot;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.java.lib.oil.lang.StringManager;
import com.ltt.android.lib.url2bitmap.Url2Bitmap;
import com.yanzhenjie.permission.AndPermission;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.content_main_url_edit)
    protected EditText urlEdit;

    @BindView(R.id.content_main_width_edit)
    protected EditText widthEdit;

    @BindView(R.id.content_main_preview_image)
    protected PhotoView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(MainActivity.class.getSimpleName(), "onCreate.UL5555LP.DI1211, enter.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AndPermission.with(this)
            .permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .send();

        urlEdit.setText(R.string.content_main_url_edit_default);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        widthEdit.setText(String.valueOf(size.x));
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

    public void onPreviewClick(View view) {
        String url = urlEdit.getText().toString();
        if (StringManager.getInstance().isEmpty(url)) {
            Toast.makeText(this, R.string.activity_main_error_url_empty, Toast.LENGTH_LONG).show();
            return;
        }

        if (!widthEdit.getText().toString().matches("\\d+")) {
            Toast.makeText(this, R.string.activity_main_error_width_format, Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            Log.i(MainActivity.class.getSimpleName(), "onPreviewClick.UL5555LP.DI1211, getBitmap call next line");
            Bitmap bitmap = Url2Bitmap.Builder().context(this).url(url).width(Integer.parseInt(widthEdit.getText().toString())).get();
            Log.i(MainActivity.class.getSimpleName(), "onPreviewClick.UL5555LP.DI1211, bitmap != null: " + (bitmap != null));
            Observable.just(bitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> previewImage.setImageBitmap(bitmap));
        }).start();
    }
}
