package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.BairroDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class Bairro {

    private int id;
    private Local local;
    private String nome;
    private int status;
    private String slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return this.getNome();
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject bairroObject =  dados.getJSONObject(i);
            Bairro umBairro = new Bairro();
            umBairro.setId(bairroObject.getInt("id"));
            umBairro.setNome(bairroObject.getString("nome"));
            umBairro.setStatus(bairroObject.getInt("status"));

            Local umLocal = new Local();
            umLocal.setId(bairroObject.getInt("local"));

            //umLocal = localDBHelper.carregar(getBaseContext(), umLocal);
            umBairro.setLocal(umLocal);
            umBairro.setSlug(bairroObject.getString("slug"));

            bairroDBHelper.salvarOuAtualizar(context, umBairro);

            //paises.get(i).nome;
        }

        bairroDBHelper.deletarInativos(context);

    }

}
