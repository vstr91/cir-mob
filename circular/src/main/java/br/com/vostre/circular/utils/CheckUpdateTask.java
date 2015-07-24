package br.com.vostre.circular.utils;

import android.os.AsyncTask;

import java.util.Map;

/**
 * Created by Almir on 24/01/2015.
 */
public class CheckUpdateTask extends AsyncTask<String, String, Integer>
        implements ServerUtilsListener, TokenTaskListener, BackGroudTaskListener {

    @Override
    protected Integer doInBackground(String... strings) {

        Integer registros = 0;

        return registros;

    }

    @Override
    public void onServerUtilsResultsSucceeded(boolean result) {

    }

    @Override
    public void onTokenTaskResultsSucceeded(String result) {

    }

    @Override
    public void onBackGroundTaskResultsSucceeded(Map<String, Object> result) {

    }

}
