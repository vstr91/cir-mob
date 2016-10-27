package br.com.vostre.circular.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.util.Log;

import javax.xml.transform.Result;

import br.com.vostre.circular.utils.BackGroudTaskListener;
import br.com.vostre.circular.utils.HttpUtils;

public class BackGroundTask extends AsyncTask<String, String, Map<String, Object>> {

    String URL = null;
    String method = null;
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    JSONArray jsonArray = null;
    Context context = null;
    Map<String, Object> map = new HashMap<String, Object>();
    String dataUltimoAcesso;
    ProgressDialog progressDialog;
    BackGroudTaskListener listener;
    boolean isBackground;

    public BackGroundTask(String url, String method, Context context, boolean isBackground) {

        this.URL = url;
        //System.out.println("URL: "+this.URL);
        //this.postparams = params;
        this.method = method;
        this.context = context;
        this.isBackground = isBackground;

    }

    public void setOnResultsListener(BackGroudTaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        if(!isBackground){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Conectando ao Servidor...");
            progressDialog.setCancelable(false);
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }


    }

    @Override
    protected void onPostExecute(Map<String, Object> stringObjectMap) {

        if(null != progressDialog && !isBackground){
            progressDialog.dismiss();
            progressDialog = null;
        }

        listener.onBackGroundTaskResultsSucceeded(stringObjectMap);

    }

    @Override
    protected Map<String, Object> doInBackground(String... params) {
        // TODO Auto-generated method stub

        JSONObject jsonObj = null;

        // Making HTTP request
        try {
            // Making HTTP request
            // check for request method

            if (method.equals("POST")) {
                // request method is POST
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost(URL);
//                httpPost.setEntity(new UrlEncodedFormEntity(postparams));
//
//                HttpResponse httpResponse = httpClient.execute(httpPost);
//                HttpEntity httpEntity = httpResponse.getEntity();
//                is = httpEntity.getContent();

            } else if (method == "GET") {

                HttpURLConnection conn = HttpUtils.sendGetRequest(URL);

                String[] resposta = HttpUtils.readMultipleLinesRespone();

                HttpUtils.disconnect();

                json = resposta[0];
                dataUltimoAcesso = conn.getHeaderField("Date");

//                // request method is GET
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//
//                /*String paramString = URLEncodedUtils
//                        .format(postparams, "utf-8");
//                URL += "?" + paramString;*/
//
//                HttpGet httpGet = new HttpGet(URL);
//
//                HttpResponse httpResponse = httpClient.execute(httpGet);
//
//                dataUltimoAcesso = httpResponse.getFirstHeader("Date").getValue();
//
//
//
//                HttpEntity httpEntity = httpResponse.getEntity();
//                is = httpEntity.getContent();
            }

            // read input stream returned by request into a string using StringBuilder
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            is.close();
//            json = sb.toString();

            jsonObj = new JSONObject(json);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //Log.e("JSON Writer", e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (Exception e) {
            //Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        map.put("json", (Object) jsonObj);
        map.put("data", (Object) dataUltimoAcesso);


        // return JSONObject (this is a class variable and null is returned if something went bad)
        return map;

    }

}