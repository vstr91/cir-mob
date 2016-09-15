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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;

/**
 * Created by Almir on 16/12/2015.
 */
public abstract class EnviaDadosTask extends AsyncTask<String, String, Object> {

//    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
//    String URL = null;
//    static InputStream is = null;
//    static JSONObject jObj = null;
//    static String json = "";
//    JSONArray jsonArray = null;
//    Context context = null;
//    String dataUltimoAcesso;
//    ProgressDialog progressDialog;
//    EnviaDadosTaskListener listener;
//    boolean isBackground;
//
//    public EnviaDadosTask(String url, Context context, List<NameValuePair> params, boolean isBackground) {
//
//        this.URL = url;
//        this.context = context;
//        this.isBackground = isBackground;
//
//    }
//
//    public void setOnResultsListener(EnviaDadosTaskListener listener){
//        this.listener = listener;
//    }
//
//    @Override
//    protected void onPreExecute() {
//
//        if(!isBackground){
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage("Conectando ao Servidor...");
//            progressDialog.setCancelable(false);
//            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.show();
//        }
//
//
//    }
//
//    @Override
//    protected void onPostExecute(Object result) {
//
//        if(null != progressDialog && !isBackground){
//            progressDialog.dismiss();
//            progressDialog = null;
//        }
//
//        listener.onEnviaDadosTaskResultsSucceeded(result);
//
//    }
//
//    @Override
//    protected Object doInBackground(String... params) {
//        JSONObject jsonObj = null;
//        ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(context);
//        try {
//            List<ParadaColeta> paradas = paradaColetaDBHelper.listarTodos(context);
//            String json = "{\"paradas\":[";
//            int qtdParadas = paradas.size();
//            int cont = 1;
//
//            for(ParadaColeta umaParada : paradas){
//
//                if(cont < qtdParadas){
//                    json = json.concat(umaParada.toJson()+",");
//                } else{
//                    json = json.concat(umaParada.toJson());
//                }
//
//                cont++;
//
//            }
//
//            json = json.concat("]}");
//            System.out.println(json);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//
//        // Making HTTP request
//        try {
//            // Making HTTP request
//            // check for request method
//
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost(URL);
//            httpPost.setEntity(new UrlEncodedFormEntity(postparams));
//
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            is = httpEntity.getContent();
//
//            // read input stream returned by request into a string using StringBuilder
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            is.close();
//            json = sb.toString();
//
//            jsonObj = new JSONObject(json);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.e("JSON Writer", e.getMessage());
//            e.printStackTrace();
//        } catch (JSONException e) {
//            Log.e("JSON Parser", "Error parsing data " + e.toString());
//        } catch (Exception e) {
//            Log.e("Buffer Error", "Error converting result " + e.toString());
//        }
//
//        return jsonObj;
//    }

}
