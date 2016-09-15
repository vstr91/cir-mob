package br.com.vostre.circular.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

/**
 * Created by Cefet on 30/09/2015.
 */
public class AnimaUtils {

    public static void animaBotao(Button botao){
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(400);
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(2);
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        botao.startAnimation(animation);
    }

}
