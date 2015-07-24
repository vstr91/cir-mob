package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.EstadoDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class Estado {

    private int id;
    private Pais pais;
    private String nome;
    private String sigla;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.getSigla();
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException{

        EstadoDBHelper estadoDBHelper = new EstadoDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject estadoObject =  dados.getJSONObject(i);
            Estado umEstado = new Estado();
            umEstado.setId(estadoObject.getInt("id"));
            umEstado.setNome(estadoObject.getString("nome"));
            umEstado.setStatus(estadoObject.getInt("status"));
            umEstado.setSigla(estadoObject.getString("sigla"));

            Pais umPais = new Pais();
            umPais.setId(estadoObject.getInt("pais"));

            umEstado.setPais(umPais);

            estadoDBHelper.salvarOuAtualizar(context, umEstado);

            estadoDBHelper.deletarInativos(context);

            //paises.get(i).nome;
        }

    }

}
