package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

/**
 * Created by Almir on 18/12/2014.
 */
public class SensorUtils {

    public boolean checarSensores(Activity activity){
        boolean retorno = false;

        PackageManager pm = activity.getPackageManager();
        boolean compass = pm.hasSystemFeature(pm.FEATURE_SENSOR_COMPASS);
        boolean accelerometer = pm.hasSystemFeature(pm.FEATURE_SENSOR_ACCELEROMETER);

        if(compass && accelerometer){
            retorno = true;
        } else{
            retorno = false;
        }

        return retorno;

    }

}
