package br.com.vostre.circular.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

/**
 * Created by Almir on 14/01/2015.
 */
public class TokenTask extends AsyncTask<String, String, String> {

    String url = null;
    Context context = null;
    ProgressDialog progressDialog;
    static InputStream is = null;
    TokenTaskListener listener;
    boolean isBackground;

    public TokenTask(String url, Context context, boolean isBackground){
        this.url = url;
        this.context = context;
        this.isBackground = isBackground;
    }

    public void setOnTokenTaskResultsListener(TokenTaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        if(!isBackground){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Solicitando Permissão de Acesso...");
            progressDialog.setCancelable(false);
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

    }

    @Override
    protected void onPostExecute(String response) {

        if(!isBackground){
            if(null != progressDialog){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }

        listener.onTokenTaskResultsSucceeded(response);

    }

    @Override
    protected String doInBackground(String... strings) {
        DefaultHttpClient httpClient = new DefaultHttpClient();

                /*String paramString = URLEncodedUtils
                        .format(postparams, "utf-8");
                URL += "?" + paramString;*/

        HttpGet httpGet = new HttpGet(url);

        HttpResponse httpResponse = null;
        StringBuilder sb = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            // read input stream returned by request into a string using StringBuilder
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            sb = new StringBuilder();
            String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

}
