package com.example.harjacober.obouncechat;

import android.app.Application;
import android.content.Intent;

import com.example.harjacober.obouncechat.services.NetworkStateService;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class App extends Application {
    public static Picasso picassoWithCache;
    @Override
    public void onCreate() {
        super.onCreate();

        //Allows firebase to persist data offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);
        picassoWithCache = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClientBuilder.build())).build();
        Intent serviceIntent = new Intent(this,
                NetworkStateService.class);
        startService(serviceIntent);

    }
}
