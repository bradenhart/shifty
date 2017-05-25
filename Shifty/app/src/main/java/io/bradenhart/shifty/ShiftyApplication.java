package io.bradenhart.shifty;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by bradenhart on 19/05/17.
 */

public class ShiftyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
