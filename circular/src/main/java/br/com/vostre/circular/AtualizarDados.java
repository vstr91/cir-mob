package br.com.vostre.circular;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import br.com.vostre.circular.model.BackGroundTask;
import br.com.vostre.circular.model.dao.ParametroDBHelper;
import br.com.vostre.circular.utils.AnalyticsUtils;
import br.com.vostre.circular.utils.BackGroudTaskListener;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.Constants;
import br.com.vostre.circular.utils.Crypt;
import br.com.vostre.circular.utils.MessageUtils;
import br.com.vostre.circular.utils.ServerUtils;
import br.com.vostre.circular.utils.ServerUtilsListener;
import br.com.vostre.circular.utils.TipoToken;
import br.com.vostre.circular.utils.TokenTask;
import br.com.vostre.circular.utils.TokenTaskListener;
import br.com.vostre.circular.utils.ToolbarUtils;
import br.com.vostre.circular.utils.UpdateTask;
import br.com.vostre.circular.utils.UpdateTaskListener;

public class AtualizarDados extends BaseActivity implements ServerUtilsListener, BackGroudTaskListener,
        UpdateTaskListener, TokenTaskListener, View.OnClickListener {

    ProgressDialog progressDialog = null;
    //boolean exists = false;
    Map<String, Object> map = null;
    BackGroundTask bgt = null;
    JSONObject jObj = null;
    Context ctx = null;
    String dataUltimoAcesso = null;
    public static boolean isVisible = false;

    Tracker tracker;
    AnalyticsUtils analyticsUtils;

    Menu menu;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atualizar_dados);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        analyticsUtils = new AnalyticsUtils();
        tracker = analyticsUtils.getTracker();

        if(tracker == null){
            tracker = analyticsUtils.iniciaAnalytics(getApplicationContext());
        }

        Button btnAtualizar = (Button) findViewById(R.id.btnAtualizar);
        btnAtualizar.setOnClickListener(this);

        isVisible = true;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                Integer mensagens = extras.getInt("mensagens");

                if(mensagens != null){

                    if(menu != null){
                        invalidateOptionsMenu();

                        ToolbarUtils.atualizaBadge(mensagens);
                    }

                }

            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.atualizar_dados, menu);

        this.menu = menu;
        ToolbarUtils.preparaMenu(menu, this, this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            /*case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;
            case R.id.icon_sobre:
                intent = new Intent(this, Sobre.class);
                startActivity(intent);
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    public void testarDisponibilidadeServidor(String ip, int porta){

        ServerUtils serverUtils = new ServerUtils(AtualizarDados.this, false);
        serverUtils.setOnResultsListener(this);

        serverUtils.execute(new String[]{ip, String.valueOf(porta)});

    }

    public void atualizaDados(){

        boolean servidorDisponivel = false;

        ProgressDialog progressDialog = new ProgressDialog(AtualizarDados.this);

        testarDisponibilidadeServidor(Constants.SERVIDOR_TESTE, 80);

    }

    private AlertDialog criaAlert(String titulo, String mensagem, boolean botaoOk){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setCancelable(false);

        if(botaoOk){
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }

        return alertDialog.create();
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public static String getDataUltimoAcesso(Context context){
        SharedPreferences sp = context.getSharedPreferences("br.com.vostre.circular", Context.MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("E dd MMM yyyy hh:mm:ss Z", Locale.ENGLISH);
        DateFormat dateFormatWeb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = null;
        ParametroDBHelper parametroDBHelper = new ParametroDBHelper(context);

        /*if(sp.getString("br.com.vostre.circular.dataUltimoAcesso", "").trim().length() > 0){
            try {

                String ultimaData = sp.getString("br.com.vostre.circular.dataUltimoAcesso", "");

                if(!ultimaData.equals("-")){
                    data = dateFormat.parse(ultimaData.replace(",", "").replace("%20", " "));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else{
            data = null;
        }

        if(null != data){
            return dateFormatWeb.format(data).replace(" ", "%20");
        } else{
            return "-";
        }*/

        String ultimaData = parametroDBHelper.carregarUltimoAcesso(context);

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

    public void setDataUltimoAcesso(Context context, String dataUltimoAcesso){
        /*SharedPreferences sp = context.getSharedPreferences("br.com.vostre.circular", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("br.com.vostre.circular.dataUltimoAcesso", dataUltimoAcesso);
        editor.commit();*/

        ParametroDBHelper parametroDBHelper = new ParametroDBHelper(context);
        parametroDBHelper.gravarUltimoAcesso(context, dataUltimoAcesso);

    }

    private void mostraProgressBar(ProgressDialog progressDialog, int qtd, String mensagem){
        progressDialog.setMessage(mensagem);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(qtd);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void escondeProgressBar(ProgressDialog progressDialog){
        progressDialog.setProgress(0);
        progressDialog.dismiss();
    }

    @Override
    public void onServerUtilsResultsSucceeded(boolean result) {

        if(result){

            String urlToken = Constants.URLTOKEN;

            TokenTask tokenTask = new TokenTask(urlToken, AtualizarDados.this, false, TipoToken.DADOS.getTipo());
            tokenTask.setOnTokenTaskResultsListener(this);
            tokenTask.execute();

        } else{

            AlertDialog alertDialog = criaAlert("Servidor Indisponível", "Não foi possível se conectar ao servidor. " +
                    "Por favor, verifique sua conexão com a Internet e tente novamente.", true);
            alertDialog.show();

        }

    }

    @Override
    public void onTokenTaskResultsSucceeded(String token) {

        Crypt crypt = new Crypt();

        String tokenCriptografado = null;
        try {
            tokenCriptografado = crypt.bytesToHex(crypt.encrypt(token));

            dataUltimoAcesso = getDataUltimoAcesso(this.getBaseContext());
            dataUltimoAcesso = dataUltimoAcesso.equals("") ? "-" : dataUltimoAcesso;

            String url = Constants.URLSERVIDOR+tokenCriptografado+"/"+dataUltimoAcesso;
            //String url = Constants.URLSERVIDOR+tokenCriptografado+"/-";

            ctx = this;

            bgt = new BackGroundTask(url, "GET", AtualizarDados.this, false);
            bgt.setOnResultsListener(this);
            bgt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackGroundTaskResultsSucceeded(Map<String, Object> result) {
        map = result;

        final TextView viewLog = (TextView) findViewById(R.id.textViewLog);
        jObj = (JSONObject) map.get("json");
        dataUltimoAcesso = (String) map.get("data");

        int registros = 0;
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

            if(registros > 0){
                UpdateTask updateTask = new UpdateTask(viewLog, jObj, ctx, progressDialog);
                updateTask.setOnResultsListener(this);
                updateTask.execute();
            } else{
                setDataUltimoAcesso(this.getBaseContext(), dataUltimoAcesso);
                AlertDialog alertDialog = criaAlert("Nenhum dado a receber", "Parabéns! " +
                        "Seu sistema já está atualizado! Não há dados a serem recebidos.", true);
                alertDialog.show();
                viewLog.setText("Sistema já atualizado!");
            }

        } else{
            AlertDialog alertDialog = criaAlert("Erro ao Receber Dados", "Não foi possível receber os dados com sucesso. " +
                    "Por favor tente novamente.", true);
            alertDialog.show();
        }
    }

    @Override
    public void onUpdateTaskResultsSucceeded(boolean result) {
        boolean sucesso = result;

        if(sucesso){
            setDataUltimoAcesso(this.getBaseContext(), dataUltimoAcesso);

            if(isVisible){
                AlertDialog dialog = criaAlert("Fim da Atualização", "Atualização finalizada com sucesso!", true);
                dialog.show();
            } else{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
                builder.setSmallIcon(R.drawable.logo_resumida);
                builder.setContentTitle("Vostrè Circular");
                builder.setContentText("Atualização finalizada com sucesso!");
                builder.setAutoCancel(true);
                //Notification notification = builder.build();

                Intent intent = new Intent(this, MainActivity.class);

                android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(pendingIntent);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(1, builder.build());
            }

        } else{
            AlertDialog dialog = criaAlert("Erro ao Atualizar", "Ocorreu erro ao atualizar os dados. Por favor, tente novamente.", true);
            dialog.show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        BroadcastUtils.registraReceiver(this, receiver);
    }

    @Override
    protected void onStop() {
        BroadcastUtils.removeRegistroReceiver(this, receiver);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;

        if(menu != null){
            invalidateOptionsMenu();

            ToolbarUtils.atualizaBadge(MessageUtils.getQuantidadeMensagensNaoLidas(getApplicationContext()));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAtualizar:

                analyticsUtils.gravaAcaoEvento("Itinerarios", "interacao", "consulta", "atualizacao", tracker);

                atualizaDados();
                break;
            default:
                ToolbarUtils.onMenuItemClick(v, this);
                break;
        }
    }
}
