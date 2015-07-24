package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.ParadaItinerarioDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class ParadaItinerario {

    private int id;
    private Parada parada;
    private Itinerario itinerario;
    private int ordem;
    private int status;
    private int destaque;
    private double valor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Parada getParada() {
        return parada;
    }

    public void setParada(Parada parada) {
        this.parada = parada;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDestaque() {
        return destaque;
    }

    public void setDestaque(int destaque) {
        this.destaque = destaque;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        ParadaItinerarioDBHelper paradaItinerarioDBHelper = new ParadaItinerarioDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject paradaItinerarioObject =  dados.getJSONObject(i);
            ParadaItinerario umaParadaItinerario = new ParadaItinerario();
            umaParadaItinerario.setId(paradaItinerarioObject.getInt("id"));

            Parada umaParada = new Parada();
            umaParada.setId(paradaItinerarioObject.getInt("parada"));
            umaParadaItinerario.setParada(umaParada);

            Itinerario umItinerario = new Itinerario();
            umItinerario.setId(paradaItinerarioObject.getInt("itinerario"));
            umaParadaItinerario.setItinerario(umItinerario);

            umaParadaItinerario.setOrdem(paradaItinerarioObject.getInt("ordem"));
            umaParadaItinerario.setStatus(paradaItinerarioObject.getInt("status"));
            umaParadaItinerario.setDestaque(paradaItinerarioObject.getInt("destaque"));

            Object valor = paradaItinerarioObject.get("valor");

            if(!valor.toString().equals("null") && !valor.toString().equals("")){
                umaParadaItinerario.setValor(paradaItinerarioObject.getDouble("valor"));
            }

            //umaParadaItinerario.setValor(paradaItinerarioObject.getDouble("valor"));

            paradaItinerarioDBHelper.salvarOuAtualizar(context, umaParadaItinerario);

        }

        paradaItinerarioDBHelper.deletarInativos(context);

    }

}
