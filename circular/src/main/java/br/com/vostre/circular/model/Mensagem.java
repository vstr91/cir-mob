package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.model.dao.PaisDBHelper;

/**
 * Created by Almir on 27/08/2015.
 */
public class Mensagem {

    private int id;
    private String titulo;
    private String descricao;
    private int status;
    private Calendar dataEnvio;
    private Calendar dataLeitura;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Calendar getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Calendar dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Calendar getDataLeitura() {
        return dataLeitura;
    }

    public void setDataLeitura(Calendar dataLeitura) {
        this.dataLeitura = dataLeitura;
    }

    public void atualizarDados(JSONArray dados, int qtdDados, Context context) throws JSONException, ParseException {

        MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(context);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));

        for(int i = 0; i < qtdDados; i++){


            JSONObject mensagemObject =  dados.getJSONObject(i);
            Mensagem umaMensagem = new Mensagem();
            umaMensagem.setId(mensagemObject.getInt("id"));
            umaMensagem.setTitulo(mensagemObject.getString("titulo"));
            umaMensagem.setDescricao(mensagemObject.getString("descricao"));
            umaMensagem.setStatus(mensagemObject.getInt("status"));
            cal.setTime(df.parse(mensagemObject.getString("ultima_alteracao")));
            umaMensagem.setDataEnvio(cal);

            mensagemDBHelper.salvarOuAtualizar(context, umaMensagem);

        }

        mensagemDBHelper.deletarInativos(context);

    }

}
