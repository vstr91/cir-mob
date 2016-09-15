package br.com.vostre.circular.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Cefet on 27/08/2015.
 */
public class MessageTask extends AsyncTask<String, String, Map<String, Object>> {

    String URL = null;
    String method = null;
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    JSONArray jsonArray = null;
    Context context = null;
    Map<String, Object> map = new HashMap<String, Object>();
    String dataUltimoAcesso;
    MessageTaskListener listener;

    public MessageTask(String url, String method, Context context) {

        this.URL = url;
        this.method = method;
        this.context = context;

    }

    public void setOnResultsListener(MessageTaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Map<String, Object> stringObjectMap) {

        listener.onMessageTaskResultsSucceeded(stringObjectMap);

    }

    @Override
    protected Map<String, Object> doInBackground(String... params) {
        // TODO Auto-generated method stub

        JSONObject jsonObj = null;

        try{
            HttpURLConnection conn = HttpUtils.sendGetRequest(URL);

            String[] resposta = HttpUtils.readMultipleLinesRespone();

            HttpUtils.disconnect();

            String json = resposta[0];
            dataUltimoAcesso = conn.getHeaderField("Date");
//
//        // Making HTTP request
//        try {
//
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
            Log.e("JSON Writer", e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        map.put("json", (Object) jsonObj);
        map.put("dataMensagem", (Object) dataUltimoAcesso);


        // return JSONObject (this is a class variable and null is returned if something went bad)
        return map;

    }

}
