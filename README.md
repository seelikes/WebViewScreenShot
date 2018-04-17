# WebViewScreenShot
capture WebView whole content to bitmap
## dependency
* add repository

        maven {
            url "http://112.74.29.196:34272/repository/maven-public/"
        }
* add it to your dependencies

        implementation "com.ltt.android.lib:url2bitmap:1.0.2"

## sample
make sure that the Url2Bitmap.getBitmap method is called from a thread other than the main thread

        new Thread(() -> {
            String url = "https://github.com/seelikes/WebViewScreenShot";
            Bitmap bitmap = Url2Bitmap.Builder().context(this).url(url).width(720).get();
            Observable.just(bitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> previewImage.setImageBitmap(bitmap));
        }).start();
