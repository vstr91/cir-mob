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
public class UpdateMessageEnviadaTask extends AsyncTask<String, String, Boolean> {

    Context ctx;
    JSONObject jObj;
    UpdateMessageTaskListener listener;

    public UpdateMessageEnviadaTask(JSONObject obj, Context context){
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
            JSONArray mensagens = jObj.getJSONArray("processadas");

            // Objetos que contem os metodos de atualizacao
            Mensagem mensagem = new Mensagem();

            MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(ctx);

            int qtdMensagens = mensagens.length();

            if(qtdMensagens > 0){

                for(int i = 0; i < qtdMensagens; i++){
                    Integer mensagemId =  mensagens.getInt(i);
                    Mensagem umaMensagem = new Mensagem();

                    umaMensagem.setId(mensagemId);
                    umaMensagem = mensagemDBHelper.carregar(ctx, umaMensagem);
                    umaMensagem.setStatus(4);
                    mensagemDBHelper.salvarOuAtualizar(ctx, umaMensagem);
                }

            }
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
