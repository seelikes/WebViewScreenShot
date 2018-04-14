package com.ltt.android.lib.url2bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ltt.android.lib.url2bitmap.callable.BitmapCallable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Url2Bitmap {
    /**
     * 通知尝试进行截图
     */
    private static final int WHAT_TRY_TO_CAPTURE = 10;

    /**
     * 获取指定地址的图片
     * 此方法会阻塞当前线程，请不要在主线程中执行此方法
     * @param context 应用上下文
     * @param url 网页地址
     * @param width 目标图片宽度
     * @return 指定网页的图片
     */
    public static Bitmap getBitmap(@NonNull Context context, String url, int width) {
        Log.i(Url2Bitmap.class.getSimpleName(), "getBitmap.UL5555LP.DI1211, url: " + url);
        Log.i(Url2Bitmap.class.getSimpleName(), "getBitmap.UL5555LP.DI1211, width: " + width);
        BitmapCallable callable = new BitmapCallable();
        FutureTask<Bitmap> task = new FutureTask<>(callable);
        new Thread(task).start();

        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                WebView view = new WebView(context);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view.setInitialScale(100);
                view.setVerticalScrollBarEnabled(false);
                view.getSettings().setBuiltInZoomControls(false);
                view.getSettings().setSupportZoom(false);

                HandlerThread shotThread = new HandlerThread("Url2Bitmap.shotThread");
                shotThread.start();
                Handler shotHandler = new Handler(shotThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == WHAT_TRY_TO_CAPTURE) {
                            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            Log.i(Url2Bitmap.class.getSimpleName(), "getBitmap.onPageFinished.UL5555LP.DI1211, view.getMeasuredWidth(): " + view.getMeasuredWidth() + "; view.getMeasuredHeight(): " + view.getMeasuredHeight());

                            if (view.getMeasuredHeight() <= 0) {
                                sendEmptyMessageDelayed(WHAT_TRY_TO_CAPTURE, 30);
                                return;
                            }

                            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, 0));
                            view.draw(canvas);

                            callable.setBitmap(bitmap);
                        }
                    }
                };
                view.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.i(Url2Bitmap.class.getSimpleName(), "getBitmap.onPageFinished.UL5555LP.DI1211, enter");
                        super.onPageFinished(view, url);

                        shotHandler.sendEmptyMessageDelayed(WHAT_TRY_TO_CAPTURE, 30);
                    }
                });

                view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

                Log.i(Url2Bitmap.class.getSimpleName(), "getBitmap.UL5555LP.DI1211, new WebView load.");
                view.loadUrl(url);
            }
        });
        try {
            return task.get();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
