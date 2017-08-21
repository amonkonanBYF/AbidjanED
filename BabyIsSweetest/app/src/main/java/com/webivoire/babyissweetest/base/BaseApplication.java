package com.webivoire.babyissweetest.base;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BaseApplication extends Application {
    private static Realm realm;
    private static RealmConfiguration config;

    @Override public void onCreate() {
        super.onCreate();
        Realm.init(this);
        config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        //Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                RealmInspectorModulesProvider.builder(this).build()
                        )
                        .build());
    }

    public static Realm getRealmInstance() {
        if (realm == null)
            realm = Realm.getInstance(config);
 //       else
   //         realm = Realm.getDefaultInstance();

        return realm;
    }
}
