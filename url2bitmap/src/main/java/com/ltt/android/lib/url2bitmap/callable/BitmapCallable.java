package com.ltt.android.lib.url2bitmap.callable;

import android.graphics.Bitmap;

import java.util.concurrent.Callable;

public class BitmapCallable implements Callable<Bitmap> {
    private Bitmap bitmap;

    public BitmapCallable() {

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public Bitmap call() throws Exception {
        synchronized (this) {
            wait();
        }
        return bitmap;
    }
}
