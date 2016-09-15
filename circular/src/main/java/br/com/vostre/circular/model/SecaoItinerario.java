package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.model.dao.SecaoItinerarioDBHelper;

/**
 * Created by Almir on 15/03/2016.
 */
public class SecaoItinerario {

    private int id;
    private int ordem;
    private String nome;
    private Itinerario itinerario;
    private Double valor;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        SecaoItinerarioDBHelper siDBHelper = new SecaoItinerarioDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject siObject =  dados.getJSONObject(i);
            SecaoItinerario umaSI = new SecaoItinerario();
            umaSI.setId(siObject.getInt("id"));
            umaSI.setOrdem(siObject.getInt("ordem"));
            umaSI.setValor(siObject.getDouble("valor"));
            umaSI.setNome(siObject.getString("nome"));
            umaSI.setStatus(siObject.getInt("status"));

            Itinerario umItinerario = new Itinerario();
            umItinerario.setId(siObject.getInt("itinerario"));

            umaSI.setItinerario(umItinerario);

            siDBHelper.salvarOuAtualizar(context, umaSI);

        }

        siDBHelper.deletarInativos(context);

    }

}
