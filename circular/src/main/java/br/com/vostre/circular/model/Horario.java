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

import br.com.vostre.circular.model.dao.HorarioDBHelper;

/**
 * Created by Almir on 14/04/2014.
 */
public class Horario {

    private int id;
    private Calendar nome;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getNome() {
        return nome;
    }

    public void setNome(Calendar nome) {
        this.nome = nome;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(this.getNome().getTime());
    }

    public void atualizarDados(JSONArray dados, int qtdDados, ProgressDialog progressDialog, Context context) throws JSONException, ParseException {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);

        for(int i = 0; i < qtdDados; i++){
            JSONObject horarioObject =  dados.getJSONObject(i);

            progressDialog.setProgress(i+1);

            Horario umHorario = new Horario();
            umHorario.setId(horarioObject.getInt("id"));
            cal.setTime(df.parse(horarioObject.getString("nome")));
            umHorario.setNome(cal);
            umHorario.setStatus(horarioObject.getInt("status"));

            horarioDBHelper.salvarOuAtualizar(context, umHorario);

        }

        horarioDBHelper.deletarInativos(context);

    }

}
