package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.LocalDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class Local {

    private int id;
    private Estado estado;
    private Local cidade;
    private String nome;
    private int status;
    private String slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Local getCidade() {
        return cidade;
    }

    public void setCidade(Local cidade) {
        this.cidade = cidade;
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

        if(null != this.getCidade()){
            return this.getNome() + " (" + this.getCidade() + ")";
        } else{
            return this.getNome();
        }



        //return this.getNome();

    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        LocalDBHelper localDBHelper = new LocalDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject localObject =  dados.getJSONObject(i);
            Local umLocal = new Local();
            umLocal.setId(localObject.getInt("id"));
            umLocal.setNome(localObject.getString("nome"));
            umLocal.setStatus(localObject.getInt("status"));

            Estado umEstado = new Estado();
            umEstado.setId(localObject.getInt("estado"));

            umLocal.setEstado(umEstado);

            Local umaCidade = new Local();
            umaCidade.setId(localObject.getInt("cidade"));

            umLocal.setCidade(umaCidade);
            umLocal.setSlug(localObject.getString("slug"));

            localDBHelper.salvarOuAtualizar(context, umLocal);

        }

        localDBHelper.deletarInativos(context);

    }

}
