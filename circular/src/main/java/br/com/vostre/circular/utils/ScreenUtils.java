package br.com.vostre.circular.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.TypedValue;

import br.com.vostre.circular.R;

/**
 * Created by Almir on 08/08/2016.
 */
public class ScreenUtils {

    public static void modificaHeaderBandeja(Activity activity){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = activity.getTheme();
            theme.resolveAttribute(R.color.cinza_circular, typedValue, true);
            int color = typedValue.data;

            Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_2016);
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, bm, color);

            activity.setTaskDescription(td);
            bm.recycle();

        }

    }

}
