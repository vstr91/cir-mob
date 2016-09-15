package br.com.vostre.circular.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import br.com.vostre.circular.AtualizarDados;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.CircularDBHelper;
import br.com.vostre.circular.model.dao.PaisDBHelper;

/**
 * Created by Almir on 02/01/2015.
 */
public class UpdateTask extends AsyncTask<String, String, Boolean> {

    TextView viewLog;
    JSONObject jObj;
    Context ctx;
    ProgressDialog progressDialog;
    int index = 0;
    UpdateTaskListener listener;

    public UpdateTask(TextView umaViewLog, JSONObject obj, Context context, ProgressDialog progressDialog){
        this.viewLog = umaViewLog;
        this.jObj = obj;
        this.ctx = context;
        //this.progressDialog = progressDialog;
    }

    public void setOnResultsListener(UpdateTaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        //super.onPreExecute();
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Progresso");
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing() && AtualizarDados.isVisible){
            progressDialog.show();
        }

        viewLog.setText("");
        viewLog.append("Iniciando atualização."+System.getProperty("line.separator")+System.getProperty("line.separator"));
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

        viewLog.append("Fim da atualização."+System.getProperty("line.separator"));

        progressDialog.dismiss();
        progressDialog = null;
        listener.onUpdateTaskResultsSucceeded(aBoolean);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        try{

            // Arrays recebidos pela API
            JSONArray paises = jObj.getJSONArray("paises");
            JSONArray horarios = jObj.getJSONArray("horarios");
            JSONArray estados = jObj.getJSONArray("estados");
            JSONArray locais = jObj.getJSONArray("locais");
            JSONArray bairros = jObj.getJSONArray("bairros");
            JSONArray paradas = jObj.getJSONArray("paradas");
            JSONArray empresas = jObj.getJSONArray("empresas");
            JSONArray itinerarios = jObj.getJSONArray("itinerarios");
            JSONArray paradasItinerarios = jObj.getJSONArray("paradasItinerarios");
            JSONArray horariosItinerarios = jObj.getJSONArray("horariosItinerarios");
            JSONArray secoesItinerarios = jObj.getJSONArray("secoesItinerarios");

            // Objetos que contem os metodos de atualizacao
            Pais pais = new Pais();
            Horario horario = new Horario();
            Estado estado = new Estado();
            Local local = new Local();
            Bairro bairro = new Bairro();
            Parada parada = new Parada();
            Empresa empresa = new Empresa();
            Itinerario itinerario = new Itinerario();
            ParadaItinerario paradaItinerario = new ParadaItinerario();
            HorarioItinerario horarioItinerario = new HorarioItinerario();
            SecaoItinerario secaoItinerario = new SecaoItinerario();

            CircularDBHelper circularDBHelper = new CircularDBHelper(ctx);

            int qtdPaises = paises.length();

            if(qtdPaises > 0){
                PaisDBHelper paisDBHelper = new PaisDBHelper(ctx);

                mostraProgressBar(progressDialog, qtdPaises, "Atualizando Países...");

                // Atualizando paises
                pais.atualizarDados(paises, qtdPaises, progressDialog, ctx);

                escondeProgressBar(progressDialog);
                publishProgress(qtdPaises+" país(es) atualizado(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Horários");
            } else{
                publishProgress("Países já atualizados."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Horários");
            }

            int qtdHorarios = horarios.length();

            if(qtdHorarios > 0){
                mostraProgressBar(progressDialog, qtdHorarios, "Atualizando Horários...");

                // Atualizando horarios
                horario.atualizarDados(horarios, qtdHorarios, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdHorarios+ " horário(s) atualizado(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Estados");
            } else{
                publishProgress("Horários já atualizados."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Estados");
            }

            int qtdEstados = estados.length();

            if(qtdEstados > 0){
                mostraProgressBar(progressDialog, qtdEstados, "Atualizando Estados...");

                // Atualizando estados
                estado.atualizarDados(estados, qtdEstados, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdEstados+ " estado(s) atualizado(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Locais");
            } else{
                publishProgress("Estados já atualizados."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Locais");
            }

            int qtdLocais = locais.length();

            if(qtdLocais > 0){
                mostraProgressBar(progressDialog, qtdLocais, "Atualizando Locais...");

                // Atualizando locais
                local.atualizarDados(locais, qtdLocais, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdLocais+" local(is) atualizado(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Bairros");
            } else{
                publishProgress("Locais já atualizados."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Bairros");
            }

            int qtdBairros = bairros.length();

            if(qtdBairros > 0){
                mostraProgressBar(progressDialog, qtdBairros, "Atualizando Bairros...");

                // Atualizando bairros
                bairro.atualizarDados(bairros, qtdBairros, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdBairros+" bairro(s) atualizado(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Paradas");
            } else{
                publishProgress("Bairros já atualizados."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Paradas");
            }

            int qtdParadas = paradas.length();

            if(qtdParadas > 0){
                mostraProgressBar(progressDialog, qtdParadas, "Atualizando Paradas...");

                // Atualizando paradas
                parada.atualizarDados(paradas, qtdParadas, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdParadas+" parada(s) atualizada(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Empresas");
            } else{
                publishProgress("Paradas já atualizadas."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Empresas");
            }

            int qtdEmpresas = empresas.length();

            if(qtdEmpresas > 0){
                mostraProgressBar(progressDialog, qtdParadas, "Atualizando Empresas...");

                // Atualizando empresas
                empresa.atualizarDados(empresas, qtdEmpresas, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdEmpresas+" empresa(s) atualizada(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Itinerários");
            } else{
                publishProgress("Empresas já atualizadas."+System.getProperty("line.separator")+System.getProperty("line.separator"),"Atualizando Itinerários");
            }

            int qtdItinerarios = itinerarios.length();

            if(qtdItinerarios > 0){
                mostraProgressBar(progressDialog, qtdItinerarios, "Atualizando Itinerários...");

                // Atualizando itinerarios
                itinerario.atualizarDados(itinerarios, qtdItinerarios, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdItinerarios+" itinerário(s) atualizado(s)."+System.getProperty("line.separator")
                        +System.getProperty("line.separator"),"Atualizando Parada-Itinerário");
            } else{
                publishProgress("Itinerários já atualizados."+System.getProperty("line.separator")
                        +System.getProperty("line.separator"),"Atualizando Parada-Itinerário");
            }

            int qtdParadasItinerarios = paradasItinerarios.length();

            if(qtdParadasItinerarios > 0){
                mostraProgressBar(progressDialog, qtdParadasItinerarios, "Atualizando Parada-Itinerário...");

                // Atualizando paradas-itinerario
                paradaItinerario.atualizarDados(paradasItinerarios, qtdParadasItinerarios, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdParadasItinerarios+" parada(s)-itinerário(s) atualizada(s)."+System.getProperty("line.separator")+System.getProperty("line.separator"),
                        "Atualizando Horários-Itinerários");
            } else{
                publishProgress("Paradas-itinerários já atualizadas."+System.getProperty("line.separator")+System.getProperty("line.separator"),
                        "Atualizando Horários-Itinerários");
            }

            int qtdHorariosItinerarios = horariosItinerarios.length();

            if(qtdHorariosItinerarios > 0){
                mostraProgressBar(progressDialog, qtdHorariosItinerarios, "Atualizando Horários-Itinerários...");

                // Atualiza horarios-itinerario
                horarioItinerario.atualizarDados(horariosItinerarios, qtdHorariosItinerarios, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdHorariosItinerarios+" horário(s)-itinerário(s) atualizado(s)."+System.getProperty("line.separator")
                        +System.getProperty("line.separator"),"");
            } else{
                publishProgress("Horários-itinerários já atualizados."+System.getProperty("line.separator")
                        +System.getProperty("line.separator"),"");
            }

            // Atualiza secoes-itinerario

            int qtdSecoesItinerarios = secoesItinerarios.length();

            if(qtdSecoesItinerarios > 0){
                mostraProgressBar(progressDialog, qtdSecoesItinerarios, "Atualizando Seções-Itinerários...");

                // Atualiza secoes-itinerario
                secaoItinerario.atualizarDados(secoesItinerarios, qtdSecoesItinerarios, progressDialog, ctx);

                escondeProgressBar(progressDialog);

                publishProgress(qtdSecoesItinerarios+" seções-itinerário(s) atualizada(s)."+System.getProperty("line.separator")
                        +System.getProperty("line.separator"),"");
            } else{
                publishProgress("Seções-itinerários já atualizados."+System.getProperty("line.separator")
                        +System.getProperty("line.separator"),"");
            }

            /*
            File dbFile =
                    new File(Environment.getDataDirectory() + "/data/br.com.vostre.circular/databases/circular-old.db");

            File exportDir = new File(Environment.getExternalStorageDirectory()+"/db", "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, dbFile.getName());

            try {
                file.createNewFile();
                //ctx.copyFile(dbFile, file);
                //return true;
            } catch (IOException e) {
                Log.e("mypck", e.getMessage(), e);
                return false;
            }
            */
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //super.onProgressUpdate(values);
        viewLog.append(values[0]);

        if(!values[1].equals("")){
            progressDialog.setMessage(values[1]);
        }

    }

    private void mostraProgressBar(ProgressDialog progressDialog, int qtd, String mensagem){
        //progressDialog.setMessage(mensagem);
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(qtd);
        //progressDialog.show();
    }

    private void escondeProgressBar(ProgressDialog progressDialog){
        progressDialog.setProgress(0);
        //progressDialog.dismiss();
    }

}
