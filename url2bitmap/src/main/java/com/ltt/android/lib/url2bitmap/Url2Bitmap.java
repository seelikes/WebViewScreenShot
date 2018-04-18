package com.ltt.android.lib.url2bitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.java.lib.oil.KVPair;
import com.java.lib.oil.lang.StringManager;
import com.ltt.android.lib.url2bitmap.callable.BitmapCallable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Url2Bitmap {
    /**
     * 通知尝试进行截图
     */
    private static final int WHAT_TRY_TO_CAPTURE = 10;

    /**
     * 检查加载进度
     * 该消息只对提供WebView有效
     */
    private static final int WHAT_CHECK_PROGRESS = 11;

    /**
     * 清除view修改
     */
    private static final int WHAT_ERASE_CHANGE_ON_VIEW = 12;

    /**
     * 应用上下文
     */
    private Context context;

    /**
     * 用于绘制网页的WebView
     * 这不是必须的，也是不推荐的
     */
    private WebView view;

    /**
     * 网页地址
     * assets目录请加前缀file:///android_asset/
     */
    private String url;

    /**
     * 启动Js
     * 默认启动Js
     */
    private boolean enableJs;

    /**
     * 目标图片宽度
     */
    private int width;

    /**
     * 生成图片的等待超时时间
     */
    private long timeout;

    /**
     * 注入到WebView组件中的JavaScriptInterface对象
     */
    private List<KVPair<String, Object>> interfaces;

    /**
     * 推荐使用Builder做为入口
     * @return {@link Builder}
     */
    public static Builder Builder() {
        return new Builder();
    }

    /**
     * 请使用 {@link #Builder()}
     */
    @Deprecated
    public Url2Bitmap() {
        enableJs = true;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public WebView getView() {
        return view;
    }

    public void setView(WebView view) {
        this.view = view;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnableJs() {
        return enableJs;
    }

    public void setEnableJs(boolean enableJs) {
        this.enableJs = enableJs;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public List<KVPair<String, Object>> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<KVPair<String, Object>> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * 获取指定地址的图片
     * 此方法会阻塞当前线程，请不要在主线程中执行此方法
     * @return 指定网页的图片
     */
    private Bitmap getBitmap() {
        BitmapCallable callable = new BitmapCallable();
        FutureTask<Bitmap> task = new FutureTask<>(callable);
        new Thread(task).start();

        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {
            @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
            @Override
            public void run() {
                boolean hasView = view != null;
                boolean bakEnableJs = hasView && view.getSettings().getJavaScriptEnabled();

                if (view == null) {
                    view = new WebView(context);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    view.setInitialScale(100);
                    view.setVerticalScrollBarEnabled(false);
                    view.getSettings().setBuiltInZoomControls(false);
                    view.getSettings().setSupportZoom(false);
                }

                view.getSettings().setJavaScriptEnabled(enableJs);

                if (interfaces != null) {
                    for (KVPair<String, Object> with: interfaces) {
                        view.addJavascriptInterface(with.getValue(), with.getKey());
                    }
                }

                long startTime = System.currentTimeMillis();
                Handler shotHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == WHAT_ERASE_CHANGE_ON_VIEW) {
                            if (hasView) {
                                view.getSettings().setJavaScriptEnabled(bakEnableJs);
                                if (interfaces != null) {
                                    for (KVPair<String, Object> with: interfaces) {
                                        view.removeJavascriptInterface(with.getKey());
                                    }
                                }
                            }
                        }
                        else if (msg.what == WHAT_CHECK_PROGRESS) {
                            if (view.getProgress() < 100) {
                                sendEmptyMessageDelayed(WHAT_CHECK_PROGRESS, 30);
                                return;
                            }
                            sendEmptyMessageDelayed(WHAT_TRY_TO_CAPTURE, 30);
                        }
                        else if (msg.what == WHAT_TRY_TO_CAPTURE) {
                            if (!hasView) {
                                view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            }

                            if (view.getMeasuredHeight() <= 0) {
                                if (timeout > 0 && System.currentTimeMillis() - startTime > timeout) {
                                    sendEmptyMessage(WHAT_ERASE_CHANGE_ON_VIEW);
                                    callable.setBitmap(null);
                                    return;
                                }
                                sendEmptyMessageDelayed(WHAT_TRY_TO_CAPTURE, 30);
                                return;
                            }

                            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, 0));
                            view.draw(canvas);

                            sendEmptyMessage(WHAT_ERASE_CHANGE_ON_VIEW);

                            callable.setBitmap(bitmap);
                        }
                    }
                };

                if (!hasView) {
                    view.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);

                            shotHandler.sendEmptyMessageDelayed(WHAT_TRY_TO_CAPTURE, 30);
                        }
                    });

                    view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
                    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                }
                else {
                    shotHandler.sendEmptyMessage(WHAT_CHECK_PROGRESS);
                }

                if (StringManager.getInstance().isEmpty(url)) {
                    return;
                }
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

    public static class Builder {
        private Url2Bitmap bitmap;

        private Builder() {
            bitmap = new Url2Bitmap();
        }

        /**
         * 设定应用上下文
         * 强引用保存，至到get方法返回
         * @param context 应用上下文
         * @return {@link Builder} 实例自身
         */
        public Builder context(@NonNull Context context) {
            bitmap.setContext(context);
            return this;
        }

        /**
         * 设定用于绘制网页WebView
         * 这不是必须的，也是不推荐的
         * @param view 用于绘制网页WebView
         * @return {@link Builder} 实例自身
         */
        public Builder view(WebView view) {
            bitmap.setView(view);
            return this;
        }

        /**
         * 设定网页地址
         * assets目录下需加file:///android_asset/前缀
         * @param url 目标网页地址
         * @return {@link Builder} 实例自身
         */
        public Builder url(String url) {
            bitmap.setUrl(url);
            return this;
        }

        /**
         * 设定是否启动Js
         * @param enableJs true 启动，false 禁止，默认 true
         * @return {@link Builder} 实例自身
         */
        public Builder enableJs(boolean enableJs) {
            bitmap.setEnableJs(enableJs);
            return this;
        }

        /**
         * 设定目标图片宽度
         * 宽度的单位是逻辑单位，但是通常等同于物理尺寸像素
         * @param width 目标图片宽度
         * @return {@link Builder} 实例自身
         */
        public Builder width(int width) {
            bitmap.setWidth(width);
            return this;
        }

        /**
         * 设定超时时间
         * 单位ms
         * 大于0才起效
         * 其他值，则 {@link #get()} 方法将阻塞至到获取到图片
         * @param timeout 超时时间
         * @return {@link Builder} 实例自身
         */
        public Builder timeout(long timeout) {
            bitmap.setTimeout(timeout);
            return this;
        }

        /**
         * 注入Javascript接口
         * @param name 名字
         * @param javascript 接口对象
         * @return {@link Builder} 实例自身
         */
        public Builder with(String name, Object javascript) {
            if (bitmap.getInterfaces() == null) {
                bitmap.setInterfaces(new ArrayList<>());
            }
            bitmap.getInterfaces().add(new KVPair<>(name, javascript));
            return this;
        }

        /**
         * 获取图片对象
         * 此方法会阻塞当前线程，请不要在主线程中执行此方法
         * @return 指定网页的图片
         */
        public Bitmap get() {
            return bitmap.getBitmap();
        }
    }
}
