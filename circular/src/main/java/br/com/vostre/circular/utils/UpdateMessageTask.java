package br.com.vostre.circular.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.CircularDBHelper;
import br.com.vostre.circular.model.dao.MensagemDBHelper;

/**
 * Created by Almir on 30/08/2015.
 */
public class UpdateMessageTask extends AsyncTask<String, String, Boolean> {

    Context ctx;
    JSONObject jObj;
    UpdateMessageTaskListener listener;

    public UpdateMessageTask(JSONObject obj, Context context){
        this.jObj = obj;
        this.ctx = context;
    }

    public void setOnResultsListener(UpdateMessageTaskListener listener){
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        try{

            // Arrays recebidos pela API
            JSONArray mensagens = jObj.getJSONArray("mensagens");

            // Objetos que contem os metodos de atualizacao
            Mensagem mensagem = new Mensagem();

            CircularDBHelper circularDBHelper = new CircularDBHelper(ctx);

            int qtdMensagens = mensagens.length();

            if(qtdMensagens > 0){

                // Atualizando mensagens
                mensagem.atualizarDados(mensagens, qtdMensagens, ctx);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //atualiza icone de mensagens
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        listener.onUpdateMessageTaskResultsSucceeded(aBoolean);
    }
}
