package br.com.vostre.circular.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.MainActivity;
import br.com.vostre.circular.Mensagens;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.model.dao.ParametroDBHelper;

/**
 * Created by Almir on 30/08/2015.
 */
public class SendMessageService extends Service implements ServerUtilsListener, TokenTaskListener,
        MessageTaskListener, UpdateMessageTaskListener {

    ServerUtils serverUtils;
    int registros = 0;
    static final long TIME_TO_UPDATE = TimeUnit.MINUTES.toMillis(Constants.TEMPO_ATUALIZACAO_ENVIO_MSG);
    List<Mensagem> mensagens;

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
                MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(getApplicationContext());
                mensagens = mensagemDBHelper.listarTodosAEnviar(getApplicationContext());

                registros = mensagens.size();

                if(registros > 0){

                    if(HttpUtils.isOnline(getApplicationContext())){
                        serverUtils = new ServerUtils(SendMessageService.this, true);
                        serverUtils.setOnResultsListener(SendMessageService.this);
                        serverUtils.execute(new String[]{Constants.SERVIDOR_TESTE, String.valueOf(Constants.PORTASERVIDOR)});
                    }

                } else{
                    stopSelf();
                }



            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, TIME_TO_UPDATE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(MessageService.this, "Iniciando rodada", Toast.LENGTH_LONG).show();
        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onServerUtilsResultsSucceeded(boolean result) {

        if(result){

            String urlToken = Constants.URLTOKEN;

            TokenTask tokenTask = new TokenTask(urlToken, SendMessageService.this, true, TipoToken.ENVIO_MENSAGEM.getTipo());
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

            String url = Constants.URLSERVIDORENVIAMSG+tokenCriptografado;
//            String url = Constants.URLSERVIDORMSG+tokenCriptografado+"/-";

            String json = "{\"mensagens\":[";
            int cont = 1;

            for(Mensagem umaMensagem : mensagens){

                if(cont < registros){
                    json = json.concat(umaMensagem.toJson()+",");
                } else{
                    json = json.concat(umaMensagem.toJson());
                }

                cont++;

            }

            json = json.concat("]}");

            Map<String, String> map = new HashMap<>();
            map.put("dados", json);
            map.put("total", String.valueOf(mensagens.size()));

            MessageTask mst = new MessageTask(url, "POST", SendMessageService.this, map);
            mst.setOnResultsListener(this);
            mst.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageTaskResultsSucceeded(Map<String, Object> map) {

        JSONObject jObj = (JSONObject) map.get("json");

        int status = 0;

        if(jObj != null){
            try {
                JSONArray metadados = jObj.getJSONArray("meta");
                JSONObject objMetadados = metadados.getJSONObject(0);

                registros = objMetadados.getInt("registros");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            registros = 0;
        }

        if(registros > 0){
            UpdateMessageEnviadaTask updateMessageEnviadaTask = new UpdateMessageEnviadaTask(jObj, this);
            updateMessageEnviadaTask.setOnResultsListener(this);
            updateMessageEnviadaTask.execute();
        }

    }

    @Override
    public void onUpdateMessageTaskResultsSucceeded(boolean result) {

        if(result){

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
