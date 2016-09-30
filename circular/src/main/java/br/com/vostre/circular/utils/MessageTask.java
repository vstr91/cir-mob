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
import java.util.StringTokenizer;

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
    Map<String, String> mapDados = new HashMap<String, String>();
    String dataUltimoAcesso;
    MessageTaskListener listener;

    public MessageTask(String url, String method, Context context, Map<String, String> map) {

        this.URL = url;
        this.method = method;
        this.context = context;
        this.mapDados = map;

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

        switch(method){
            case "GET":
                try{
                    HttpURLConnection conn = HttpUtils.sendGetRequest(URL);

                    String[] resposta = HttpUtils.readMultipleLinesRespone();

                    HttpUtils.disconnect();

                    String json = resposta[0];
                    dataUltimoAcesso = conn.getHeaderField("Date");

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
                break;
            case "POST":

                try {
                    HttpURLConnection conn = HttpUtils.sendPostRequest(URL, mapDados);
                    String[] resposta = HttpUtils.readMultipleLinesRespone();

                    HttpUtils.disconnect();

                    String json = resposta[0];
                    jsonObj = new JSONObject(json);

                    map.put("json", (Object) jsonObj);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }




        // return JSONObject (this is a class variable and null is returned if something went bad)
        return map;

    }

}
