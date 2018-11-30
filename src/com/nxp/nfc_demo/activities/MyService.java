//package com.nxp.nfc_demo.activities;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//public class MyService extends Service {
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // Query the database and show alarm if it applies
//
//        // Here you can return one of some different constants.
//        // This one in particular means that if for some reason
//        // this service is killed, we don't want to start it
//        // again automatically
//        return START_NOT_STICKY;
//    }
//
//}
