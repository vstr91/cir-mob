package br.com.vostre.circular.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Cefet on 24/11/2015.
 */
public class SnackbarHelper {

    public static void notifica(View rootView, CharSequence texto, int duracao){
        Snackbar.make(rootView, texto, duracao).show();
    }

}
