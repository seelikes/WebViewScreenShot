package com.ltt.android.sites.webviewscreenshot.with;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MainEntity {
    private Context context;

    public MainEntity(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public String getName() {
        return MainEntity.class.getSimpleName();
    }
}
