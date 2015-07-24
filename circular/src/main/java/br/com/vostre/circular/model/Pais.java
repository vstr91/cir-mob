package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.PaisDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class Pais {

    private int id;
    private String nome;
    private String iso3;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.getId() +" - "+ this.getNome();
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        PaisDBHelper paisDBHelper = new PaisDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            JSONObject paisObject =  dados.getJSONObject(i);
            Pais umPais = new Pais();
            umPais.setId(paisObject.getInt("id"));
            umPais.setNome(paisObject.getString("nome"));
            umPais.setStatus(paisObject.getInt("status"));
            umPais.setIso3(paisObject.getString("iso3"));

            paisDBHelper.salvarOuAtualizar(context, umPais);

            progressDialog.incrementProgressBy(i+1);

        }

        paisDBHelper.deletarInativos(context);

    }

}
