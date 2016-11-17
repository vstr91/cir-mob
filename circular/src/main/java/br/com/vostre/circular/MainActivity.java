package br.com.vostre.circular;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.model.dao.PaisDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;
import br.com.vostre.circular.utils.AnalyticsUtils;
import br.com.vostre.circular.utils.BackGroudTaskListener;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.Constants;
import br.com.vostre.circular.utils.Crypt;
import br.com.vostre.circular.utils.FileUtils;
import br.com.vostre.circular.utils.MessageService;
import br.com.vostre.circular.utils.MessageUtils;
import br.com.vostre.circular.utils.NotificacaoUtils;
import br.com.vostre.circular.utils.ScreenUtils;
import br.com.vostre.circular.utils.SendMessageService;
import br.com.vostre.circular.utils.SensorUtils;
import br.com.vostre.circular.utils.ServerUtils;
import br.com.vostre.circular.utils.ServerUtilsListener;
import br.com.vostre.circular.utils.ServiceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.utils.TipoToken;
import br.com.vostre.circular.utils.TokenTask;
import br.com.vostre.circular.utils.TokenTaskListener;
import br.com.vostre.circular.utils.ToolbarUtils;
import br.com.vostre.circular.utils.Unique;

public class MainActivity extends BaseActivity implements ServerUtilsListener, BackGroudTaskListener, TokenTaskListener,
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    boolean iniciaModoCamera = false;
    boolean buscaAtualizacao = true;
    static Button btnModoCamera;
    static boolean temSensores;
    String dataUltimoAcesso = null;
    DrawerLayout drawer;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navView;

    public static Tracker tracker;
    BroadcastReceiver receiver;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(getApplicationContext());
        //mensagemDBHelper.deletarCadastrados(getApplicationContext());

        //ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(getApplicationContext());
        //paradaColetaDBHelper.deletarCadastrados(getApplicationContext());

        //FileUtils.exportDatabase("circular.db", "circular-exp3.db", this);

        AnalyticsUtils analyticsUtils = new AnalyticsUtils();
        tracker = analyticsUtils.getTracker();

        if(tracker == null){
            tracker = analyticsUtils.iniciaAnalytics(getApplicationContext());
        }

        iniciaServicoMensagem();

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

        PaisDBHelper paisDBHelper = new PaisDBHelper(getApplicationContext());
        boolean dbExiste = false;
        boolean dbPopulado = false;
        SensorUtils sensorUtils = new SensorUtils();
        temSensores = sensorUtils.checarSensores(this);

        File dbFile =
                new File(Environment.getDataDirectory() + "/data/"+getPackageName()+"/databases/circular.db");
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

        String identificador = Unique.getIdentificadorUnico(this);

        if(identificador.equals("")){
            identificador = geraIdentificadorUnico();

            SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(getPackageName()+".identificadorUnico", identificador);
            editor.commit();

        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.container);
        navView = (NavigationView) findViewById(R.id.nav);

        navView.setNavigationItemSelectedListener(this);

        navView.getMenu().getItem(0).setChecked(true);

        // --------------------------------------------------------

        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                drawerToggle.syncState();
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                drawerToggle.syncState();
            }

        };

        // --------------------------------------------------------

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        btnModoCamera = (Button) findViewById(R.id.buttonModoCamera);

        if(!temSensores){
            btnModoCamera.setEnabled(false);
            btnModoCamera.setText(btnModoCamera.getText()+" Não Suportado");
        }

        verificaCheckAtualizacao();

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
            /*case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;*/
            case R.id.icon_msg:
                intent = new Intent(this, Mensagens.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }


        return super.onOptionsItemSelected(item);
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

        int permissionGPS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if(permissionGPS != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED){

            if(permissionGPS != PackageManager.PERMISSION_GRANTED && permissionCamera != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 111);
            } else if(permissionGPS != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
            } else if(permissionCamera != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
            }


        } else{
            Intent intent = new Intent(this, RealidadeNova.class);
            startActivity(intent);
        }

        //LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

        //} else{
        //    buildAlertDisabledGps();
        //}

    }

    public void abreTelaFavoritos(View view){
        Intent intent = new Intent(this, FavoritosActivity.class);
        startActivity(intent);
    }

//    public void abreTelaMapa(View view){
//
//        int permissionGPS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
//        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//
//        if(permissionGPS != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED){
//
//            if(permissionGPS != PackageManager.PERMISSION_GRANTED && permissionCamera != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 111);
//            } else if(permissionGPS != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
//            } else if(permissionCamera != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
//            }
//
//
//        } else{
//            Intent intent = new Intent(this, MapaConsultaActivity.class);
//            startActivity(intent);
//        }
//
//    }

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
        testarDisponibilidadeServidor(Constants.SERVIDOR, Constants.PORTASERVIDOR);
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
            String nomeDbInterno = Environment.getDataDirectory() + "/data/"+getPackageName()+"/databases/circular.db";
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
        builder.setSmallIcon(R.drawable.icon_2016_transparente);
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
        manager.notify(Constants.ID_NOTIFICACAO_ATUALIZACAO, builder.build());
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
                criaNotificacao();
            }

            String dataUltimoAcesso;
            Calendar diaAtual = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            setDataUltimaVerificacao(getBaseContext(), dateFormat.format(diaAtual.getTime()));
        }

    }

    @Override
    public void onServerUtilsResultsSucceeded(boolean result) {
        if(result){

            String urlToken = Constants.URLTOKEN;

            TokenTask tokenTask = new TokenTask(urlToken, MainActivity.this, true, TipoToken.DADOS.getTipo());
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

            BackGroundTask bgt = new BackGroundTask(url, "GET", MainActivity.this, true);
            bgt.setOnResultsListener(this);
            bgt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        Intent i;

        switch (menuItem.getItemId()){
//            case R.id.nav_consulta:
//                menuItem.setChecked(true);
//                drawer.closeDrawers();
//                break;
//            case R.id.nav_cadastro:
//                i = new Intent(getBaseContext(), ColetaMainActivity.class);
//                menuItem.setChecked(true);
//                drawer.closeDrawers();
//                startActivity(i);
//                break;
            case R.id.opcoes:
                i = new Intent(this, Parametros.class);
                drawer.closeDrawers();
                startActivity(i);
                break;
            case R.id.sobre:
                i = new Intent(this, Sobre.class);
                drawer.closeDrawers();
                startActivity(i);
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawer.closeDrawers();
        navView.getMenu().getItem(0).setChecked(true);


        if(menu != null){
            invalidateOptionsMenu();

            ToolbarUtils.atualizaBadge(MessageUtils.getQuantidadeMensagensNaoLidas(getApplicationContext()));
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

    private void iniciaServicoMensagem(){

        final ServiceUtils serviceUtils = new ServiceUtils();
        Intent serviceIntent = new Intent(getBaseContext(), MessageService.class);

        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if(!serviceUtils.isMyServiceRunning(MessageService.class, manager)){
            stopService(serviceIntent);
            startService(serviceIntent);
            //Toast.makeText(this, "Iniciando serviço...", Toast.LENGTH_LONG).show();
        } else{
            //Toast.makeText(this, "Serviço já rodando...", Toast.LENGTH_LONG).show();
        }

    }

    private String geraIdentificadorUnico(){
        Unique unique = new Unique();
        return unique.geraIdentificadorUnico();
    }

    @Override
    public void onClick(View v) {

        ToolbarUtils.onMenuItemClick(v, this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case 111:

                int tamanho = grantResults.length;
                int tamanhoAceitos = 0;

                if(tamanho > 0){

                    for(int i = 0; i < tamanho; i++){

                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            tamanhoAceitos++;
                        }

                    }

                    if(tamanhoAceitos == tamanho){
                        Intent intent = new Intent(this, RealidadeNova.class);
                        startActivity(intent);
                    } else{
                        Toast.makeText(getApplicationContext(), "Para utilizar a Realidade Aumentada é necessário permitir acesso " +
                                        "à sua localização atual e à câmera de seu dispositivo.",
                                Toast.LENGTH_LONG).show();
                    }

                } else{
                    Toast.makeText(getApplicationContext(), "Para utilizar a Realidade Aumentada é necessário permitir acesso " +
                                    "à sua localização atual e à câmera de seu dispositivo.",
                            Toast.LENGTH_LONG).show();
                }

                break;
        }

    }
}
