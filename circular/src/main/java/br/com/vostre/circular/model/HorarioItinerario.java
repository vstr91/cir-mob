package br.com.vostre.circular.model;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.vostre.circular.model.dao.HorarioItinerarioDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class HorarioItinerario {

    private int id;
    private Horario horario;
    private Itinerario itinerario;
    private int domingo;
    private int segunda;
    private int terca;
    private int quarta;
    private int quinta;
    private int sexta;
    private int sabado;
    private int status;

    private boolean isTrecho;

    public Horario getHorario() {
        return horario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public int getDomingo() {
        return domingo;
    }

    public void setDomingo(int domingo) {
        this.domingo = domingo;
    }

    public int getSegunda() {
        return segunda;
    }

    public void setSegunda(int segunda) {
        this.segunda = segunda;
    }

    public int getTerca() {
        return terca;
    }

    public void setTerca(int terca) {
        this.terca = terca;
    }

    public int getQuarta() {
        return quarta;
    }

    public void setQuarta(int quarta) {
        this.quarta = quarta;
    }

    public int getQuinta() {
        return quinta;
    }

    public void setQuinta(int quinta) {
        this.quinta = quinta;
    }

    public int getSexta() {
        return sexta;
    }

    public void setSexta(int sexta) {
        this.sexta = sexta;
    }

    public int getSabado() {
        return sabado;
    }

    public void setSabado(int sabado) {
        this.sabado = sabado;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isTrecho() {
        return isTrecho;
    }

    public void setTrecho(boolean isTrecho) {
        this.isTrecho = isTrecho;
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException {

        HorarioItinerarioDBHelper horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(context);

        for(int i = 0; i < qtdDados; i++){

            progressDialog.setProgress(i+1);

            JSONObject horarioItinerarioObject =  dados.getJSONObject(i);
            HorarioItinerario umHorarioItinerario = new HorarioItinerario();
            umHorarioItinerario.setId(horarioItinerarioObject.getInt("id"));

            Horario umHorario = new Horario();
            umHorario.setId(horarioItinerarioObject.getInt("horario"));
            umHorarioItinerario.setHorario(umHorario);

            Itinerario umItinerario = new Itinerario();
            umItinerario.setId(horarioItinerarioObject.getInt("itinerario"));
            umHorarioItinerario.setItinerario(umItinerario);

            umHorarioItinerario.setDomingo(horarioItinerarioObject.getInt("domingo"));
            umHorarioItinerario.setSegunda(horarioItinerarioObject.getInt("segunda"));
            umHorarioItinerario.setTerca(horarioItinerarioObject.getInt("terca"));
            umHorarioItinerario.setQuarta(horarioItinerarioObject.getInt("quarta"));
            umHorarioItinerario.setQuinta(horarioItinerarioObject.getInt("quinta"));
            umHorarioItinerario.setSexta(horarioItinerarioObject.getInt("sexta"));
            umHorarioItinerario.setSabado(horarioItinerarioObject.getInt("sabado"));

            umHorarioItinerario.setStatus(horarioItinerarioObject.getInt("status"));

            horarioItinerarioDBHelper.salvarOuAtualizar(context, umHorarioItinerario);

        }

        horarioItinerarioDBHelper.deletarInativos(context);

    }

}
