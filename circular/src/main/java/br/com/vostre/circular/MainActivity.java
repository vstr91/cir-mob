package br.com.vostre.circular;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.vostre.circular.model.BackGroundTask;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.PaisDBHelper;
import br.com.vostre.circular.model.dao.ParametroDBHelper;
import br.com.vostre.circular.utils.BackGroudTaskListener;
import br.com.vostre.circular.utils.Constants;
import br.com.vostre.circular.utils.Crypt;
import br.com.vostre.circular.utils.FileUtils;
import br.com.vostre.circular.utils.SensorUtils;
import br.com.vostre.circular.utils.ServerUtils;
import br.com.vostre.circular.utils.ServerUtilsListener;
import br.com.vostre.circular.utils.TokenTask;
import br.com.vostre.circular.utils.TokenTaskListener;

public class MainActivity extends ActionBarActivity implements ServerUtilsListener, BackGroudTaskListener, TokenTaskListener {

    boolean iniciaModoCamera = false;
    boolean buscaAtualizacao = true;
    static Button btnModoCamera;
    static boolean temSensores;
    String dataUltimoAcesso = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PaisDBHelper paisDBHelper = new PaisDBHelper(getApplicationContext());
        boolean dbExiste = false;
        boolean dbPopulado = false;
        SensorUtils sensorUtils = new SensorUtils();
        temSensores = sensorUtils.checarSensores(this);

        File dbFile =
                new File(Environment.getDataDirectory() + "/data/br.com.vostre.circular/databases/circular.db");
        //System.out.println("Pasta: "+ getCacheDir());

        if(dbFile.exists()){
            dbExiste = true;
        }

        List<Pais> paises = paisDBHelper.listarTodos(getApplicationContext());

        if(paises.size() > 0){
            dbPopulado = true;
        }

        if(!dbExiste || !dbPopulado){
            moveBancoDeDados();
        }

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);

        iniciaModoCamera = preference.getBoolean("tela_inicial_checkbox", false);
        //buscaAtualizacao = preference.getBoolean("atualizacao_checkbox", true);

        if(iniciaModoCamera){
            abreTelaRealidade(null);
        }

        setContentView(R.layout.activity_main);

        /*
        if(buscaAtualizacao){
            verificaAtualizacoes();
        }
        */

        verificaCheckAtualizacao();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    private void verificaCheckAtualizacao() {
        Calendar diaAtual = Calendar.getInstance();
        Date diaUltimaVerificacao;
        Calendar calUltimaVerificacao = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        diaAtual.set(Calendar.HOUR_OF_DAY, 0);
        diaAtual.set(Calendar.MINUTE, 0);
        diaAtual.set(Calendar.SECOND, 0);
        diaAtual.set(Calendar.MILLISECOND, 0);

        String ultimaVerificacao = getDataUltimaVerificacao(getBaseContext());

        ultimaVerificacao = ultimaVerificacao.equals("-") ? "2015-06-15" : ultimaVerificacao;

        try {
            diaUltimaVerificacao = dateFormat.parse(ultimaVerificacao);
            calUltimaVerificacao.setTime(diaUltimaVerificacao);

            if(calUltimaVerificacao.before(diaAtual)){
                verificaAtualizacoes();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.

        /*if(iniciaModoCamera){
            getMenuInflater().inflate(R.menu.realidade_aumentada, menu);
        } else{
            getMenuInflater().inflate(R.menu.main, menu);
        }*/

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        switch (id){
            case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;
            case R.id.icon_sobre:
                intent = new Intent(this, Sobre.class);
                startActivity(intent);
                break;
            case R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            btnModoCamera = (Button) rootView.findViewById(R.id.buttonModoCamera);

            if(!temSensores){
                btnModoCamera.setEnabled(false);
                btnModoCamera.setText(btnModoCamera.getText()+"\r\nNão Suportado");
            }

            return rootView;
        }
    }

    public void abreTelaItinerario(View view){
        Intent intent = new Intent(this, Itinerarios.class);
        startActivity(intent);
    }

    public void abreTelaAtualizar(View view){
        Intent intent = new Intent(this, AtualizarDados.class);
        startActivity(intent);
    }

    public void abreTelaParadas(View view){
        Intent intent = new Intent(this, Paradas.class);
        startActivity(intent);
    }

    public void abreTelaRealidade(View view){

        //LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent intent = new Intent(this, RealidadeNova.class);
            startActivity(intent);
        //} else{
        //    buildAlertDisabledGps();
        //}

    }

    public void abreTelaMapa(View view){
/*
        FileUtils.exportDatabse("circular.db", "circularbkp.db", MainActivity.this);

        Toast.makeText(MainActivity.this, "Banco de Dados Exportado", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, Mapa.class);
        startActivity(intent);
*/
    }

    public void buildAlertDisabledGps(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Para acessar o modo câmera, por favor ative o GPS.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                })
                .setNegativeButton("Agora Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        final AlertDialog dialog = alert.create();
        dialog.show();

    }

    public void verificaAtualizacoes(){
        testarDisponibilidadeServidor(Constants.SERVIDOR, 80);
    }

    public void reconhecimentoVoz(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, Locale.getDefault());
        System.out.println(Locale.getDefault());

        try{
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException ex){
            Toast.makeText(getApplicationContext(), "Erro: "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 100:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), "Texto: "+result.get(0), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }

    private boolean moveBancoDeDados(){
        boolean retorno = false;
        AtualizarDados atualizarDados = new AtualizarDados();

        try {
            String nomeDbInterno = Environment.getDataDirectory() + "/data/br.com.vostre.circular/databases/circular.db";
            InputStream dbExterno = getAssets().open("circular.db");
            OutputStream dbInterno = new FileOutputStream(nomeDbInterno);

            int tamanho = dbExterno.available();

            byte[] buffer = new byte[tamanho];
            int length;

            while ((length = dbExterno.read(buffer)) > 0){
                dbInterno.write(buffer, 0, length);
            }

            dbInterno.flush();
            dbInterno.close();
            dbExterno.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return retorno;

    }

    public void criaNotificacao(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setSmallIcon(R.drawable.logo_resumida);
        builder.setContentTitle("Vostrè Circular");
        builder.setContentText("Existem atualizações disponíveis!");
        builder.setAutoCancel(true);
        //Notification notification = builder.build();

        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.putExtra("flagVerifica", false);
        Intent intent = new Intent(this, AtualizarDados.class);

        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent =
                PendingIntent.getActivities(this, 200, new Intent[]{backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(2, builder.build());
    }

    public void testarDisponibilidadeServidor(String ip, int porta){

        ServerUtils serverUtils = new ServerUtils(MainActivity.this, true);
        serverUtils.setOnResultsListener(this);

        serverUtils.execute(new String[]{ip, String.valueOf(porta)});

    }

    public static String getDataUltimoAcesso(Context context){
        return AtualizarDados.getDataUltimoAcesso(context);
    }

    public static String getDataUltimaVerificacao(Context context){
        SharedPreferences sp = context.getSharedPreferences("br.com.vostre.circular", Context.MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat dateFormatWeb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = null;

        if(sp.getString("br.com.vostre.circular.dataUltimaVerificacao", "").trim().length() > 0){
            try {

                String ultimaData = sp.getString("br.com.vostre.circular.dataUltimaVerificacao", "");

                if(!ultimaData.equals("-")){
                    data = dateFormat.parse(ultimaData);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else{
            data = null;
        }

        if(null != data){
            return dateFormat.format(data);
        } else{
            return "-";
        }


    }

    public void setDataUltimaVerificacao(Context context, String dataUltimaVerificacao){
        SharedPreferences sp = context.getSharedPreferences("br.com.vostre.circular", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("br.com.vostre.circular.dataUltimaVerificacao", dataUltimaVerificacao);
        editor.commit();

    }

    @Override
    public void onBackGroundTaskResultsSucceeded(Map<String, Object> map) {

        JSONObject jObj = (JSONObject) map.get("json");

        int registros = 0;
        int status = 0;

        try {
            JSONArray metadados = jObj.getJSONArray("meta");
            JSONObject objMetadados = metadados.getJSONObject(0);

            registros = objMetadados.getInt("registros");
            status = objMetadados.getInt("status");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(registros > 0){
            criaNotificacao();
        }

        String dataUltimoAcesso;
        Calendar diaAtual = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        setDataUltimaVerificacao(getBaseContext(), dateFormat.format(diaAtual.getTime()));

    }

    @Override
    public void onServerUtilsResultsSucceeded(boolean result) {
        if(result){

            String urlToken = Constants.URLTOKEN;

            TokenTask tokenTask = new TokenTask(urlToken, MainActivity.this, true);
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

            dataUltimoAcesso = getDataUltimoAcesso(this.getBaseContext());
            dataUltimoAcesso = dataUltimoAcesso.equals("") ? "-" : dataUltimoAcesso;

            String url = Constants.URLSERVIDOR+tokenCriptografado+"/"+dataUltimoAcesso;
            //String url = Constants.URLSERVIDOR+tokenCriptografado+"/-";

            BackGroundTask bgt = new BackGroundTask(url, "GET", MainActivity.this, null, true);
            bgt.setOnResultsListener(this);
            bgt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
