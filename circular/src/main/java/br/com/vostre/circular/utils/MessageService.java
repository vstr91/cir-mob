package br.com.vostre.circular.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.Itinerarios;
import br.com.vostre.circular.MainActivity;
import br.com.vostre.circular.Mensagens;
import br.com.vostre.circular.model.BackGroundTask;
import br.com.vostre.circular.model.dao.ParametroDBHelper;

/**
 * Created by Almir on 30/08/2015.
 */
public class MessageService extends Service implements ServerUtilsListener, TokenTaskListener,
        MessageTaskListener, UpdateMessageTaskListener {

    ServerUtils serverUtils;
    String dataUltimoAcesso;
    static final long TIME_TO_UPDATE = TimeUnit.MINUTES.toMillis(Constants.TEMPO_ATUALIZACAO_MSG);
    int registros = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                serverUtils = new ServerUtils(MessageService.this, true);
                serverUtils.setOnResultsListener(MessageService.this);
                serverUtils.execute(new String[]{Constants.SERVIDOR_TESTE, String.valueOf(Constants.PORTASERVIDOR)});

            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, TIME_TO_UPDATE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(MessageService.this, "Iniciando rodada", Toast.LENGTH_LONG).show();
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onServerUtilsResultsSucceeded(boolean result) {

        if(result){

            String urlToken = Constants.URLTOKEN;

            TokenTask tokenTask = new TokenTask(urlToken, MessageService.this, true, TipoToken.MENSAGEM.getTipo());
            tokenTask.setOnTokenTaskResultsListener(this);
            tokenTask.execute();

        }

    }

    @Override
    public void onTokenTaskResultsSucceeded(String token) {

        Crypt crypt = new Crypt();

        String tokenCriptografado = null;
        try {
            tokenCriptografado = crypt.bytesToHex(crypt.encrypt(token));

            dataUltimoAcesso = getDataUltimoAcessoMensagem(this.getBaseContext());
            dataUltimoAcesso = dataUltimoAcesso.equals("") ? "-" : dataUltimoAcesso;

            String url = Constants.URLSERVIDORMSG+tokenCriptografado+"/"+dataUltimoAcesso;
//            String url = Constants.URLSERVIDORMSG+tokenCriptografado+"/-";

            MessageTask mst = new MessageTask(url, "GET", MessageService.this, null);
            mst.setOnResultsListener(this);
            mst.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageTaskResultsSucceeded(Map<String, Object> map) {

        JSONObject jObj = (JSONObject) map.get("json");
        dataUltimoAcesso = (String) map.get("dataMensagem");

        int status = 0;

        if(jObj != null){
            try {
                JSONArray metadados = jObj.getJSONArray("meta");
                JSONObject objMetadados = metadados.getJSONObject(0);

                registros = objMetadados.getInt("registros");
                status = objMetadados.getInt("status");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Erro ao receber mensagens... uma nova tentativa será feita em breve.", Toast.LENGTH_LONG).show();
        }

        if(registros > 0){
            UpdateMessageTask updateMessageTask = new UpdateMessageTask(jObj, this);
            updateMessageTask.setOnResultsListener(this);
            updateMessageTask.execute();
        } else{
            setDataUltimoAcessoMensagem(getBaseContext(), dataUltimoAcesso);
        }

    }

    @Override
    public void onUpdateMessageTaskResultsSucceeded(boolean result) {

        if(result){
            setDataUltimoAcessoMensagem(getBaseContext(), dataUltimoAcesso);

            String titulo = "";
            String mensagem = "";

            if(registros > 1){
                titulo = "Novas Mensagens";
                mensagem = registros+" novas mensagens foram recebidas.";
            } else{
                titulo = "Nova Mensagem";
                mensagem = registros+" nova mensagem foi recebida.";
            }

            NotificacaoUtils.criaNotificacao(MainActivity.class, Mensagens.class,
                    getApplicationContext(), titulo, mensagem, Constants.ID_NOTIFICACAO_MSG);
            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
            Intent intent = new Intent("MensagensService");
            intent.putExtra("mensagens", registros);
            broadcaster.sendBroadcast(intent);
        }

    }

    public static String getDataUltimoAcessoMensagem(Context context){
        DateFormat dateFormat = new SimpleDateFormat("E dd MMM yyyy hh:mm:ss Z", Locale.ENGLISH);
        DateFormat dateFormatWeb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = null;
        ParametroDBHelper parametroDBHelper = new ParametroDBHelper(context);

        String ultimaData = parametroDBHelper.carregarUltimoAcessoMensagem(context);

        try {

            if(!ultimaData.equals("-")){
                data = dateFormat.parse(ultimaData.replace(",", "").replace("%20", " "));
            }

        } catch(ParseException ex){
            ex.printStackTrace();
        }

        if(null != data){
            return dateFormatWeb.format(data).replace(" ", "%20");
        } else{
            return "-";
        }

    }

    public void setDataUltimoAcessoMensagem(Context context, String dataUltimoAcesso){
        ParametroDBHelper parametroDBHelper = new ParametroDBHelper(context);
        parametroDBHelper.gravarUltimoAcessoMensagem(context, dataUltimoAcesso);
    }

}
