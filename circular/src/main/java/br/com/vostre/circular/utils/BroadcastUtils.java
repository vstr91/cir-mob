package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * Created by Almir on 10/04/2016.
 */
public class BroadcastUtils {

    public static void registraReceiver(Activity activity, BroadcastReceiver receiver){
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, new IntentFilter("MensagensService"));
    }

    public static void removeRegistroReceiver(Activity activity, BroadcastReceiver receiver){
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
    }

}
