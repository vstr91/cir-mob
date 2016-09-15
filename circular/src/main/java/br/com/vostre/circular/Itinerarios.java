package br.com.vostre.circular;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.awt.font.NumericShaper;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ItinerarioLog;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.EstadoDBHelper;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.HorarioItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioLogDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.ParadaItinerarioDBHelper;
import br.com.vostre.circular.model.dao.SecaoItinerarioDBHelper;
import br.com.vostre.circular.utils.AnalyticsUtils;
import br.com.vostre.circular.utils.AnimaUtils;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.CustomAdapter;
import br.com.vostre.circular.utils.CustomSpinner;
import br.com.vostre.circular.utils.ItinerarioDestinoSpinner;
import br.com.vostre.circular.utils.ListviewComFiltro;
import br.com.vostre.circular.utils.ListviewComFiltroListener;
import br.com.vostre.circular.utils.LocalEstadoSpinner;
import br.com.vostre.circular.utils.MessageUtils;
import br.com.vostre.circular.utils.PreferencesUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.utils.ToolbarUtils;

public class Itinerarios extends BaseActivity implements View.OnClickListener,
        ListviewComFiltroListener {

    TextView textViewHorario;
    TextView textViewTarifa;
    TextView textViewTaxaDeEmbarque;
    TextView textViewEmpresa;
    TextView textViewProximoHorarioLabel;
    TextView textViewObsLabel;
    Button btnTodosHorarios;
    Button btnSecoes;

    Button btnLocal;
    Button btnPartida;
    Button btnDestino;

    ImageView imgValor;
    ImageView imgEmpresa;
    Space espacoBotoes;
    Space spaceTaxa;

    HorarioItinerario horarioItinerario;

    LocalDBHelper localDBHelper = new LocalDBHelper(getBaseContext());
    BairroDBHelper bairroDBHelper = new BairroDBHelper(getBaseContext());
    HorarioDBHelper horarioDBHelper = new HorarioDBHelper(getBaseContext());
    HorarioItinerarioDBHelper horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(getBaseContext());

    Local localEscolhido;
    Bairro partidaEscolhida;
    Bairro destinoEscolhido;

    Menu menu;
    BroadcastReceiver receiver;

    Tracker tracker;
    AnalyticsUtils analyticsUtils;

    Long tempoUtilizado;
    Long tempoInicial;
    Long tempoFinal;

    FloatingActionButton fabFavorito;
    Boolean flagFavorito = false;

    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        tempoInicial = SystemClock.elapsedRealtime();

        setContentView(R.layout.activity_itinerarios);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        analyticsUtils = new AnalyticsUtils();
        tracker = analyticsUtils.getTracker();

        if(tracker == null){
            tracker = analyticsUtils.iniciaAnalytics(getApplicationContext());
        }

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

        localDBHelper = new LocalDBHelper(getBaseContext());
        bairroDBHelper = new BairroDBHelper(getBaseContext());
        horarioDBHelper = new HorarioDBHelper(getBaseContext());
        horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(getBaseContext());

        v = (NestedScrollView) findViewById(R.id.nestedScroll);

        textViewHorario = (TextView) findViewById(R.id.textViewHorario);
        textViewTarifa = (TextView) findViewById(R.id.textViewTarifa);
        textViewTaxaDeEmbarque = (TextView) findViewById(R.id.textViewTaxaDeEmbarque);
        textViewEmpresa = (TextView) findViewById(R.id.textViewEmpresa);
        textViewProximoHorarioLabel = (TextView) findViewById(R.id.textViewProximoHorarioLabel);
        textViewObsLabel = (TextView) findViewById(R.id.textViewObs);
        btnTodosHorarios = (Button) findViewById(R.id.btnTodosHorarios);
        btnSecoes = (Button) findViewById(R.id.btnSecoes);

        btnLocal = (Button) findViewById(R.id.btnLocal);
        btnPartida = (Button) findViewById(R.id.btnPartida);
        btnDestino = (Button) findViewById(R.id.btnDestino);

        imgValor = (ImageView) findViewById(R.id.imgValor);
        imgEmpresa = (ImageView) findViewById(R.id.imgEmpresa);

        espacoBotoes = (Space) findViewById(R.id.espacoBotoes);
        spaceTaxa = (Space) findViewById(R.id.spaceTaxa);

        fabFavorito = (FloatingActionButton) findViewById(R.id.fabFavorito);

        ocultaCampos();

        btnTodosHorarios.setOnClickListener(this);
        btnLocal.setOnClickListener(this);
        btnPartida.setOnClickListener(this);
        btnDestino.setOnClickListener(this);
        btnSecoes.setOnClickListener(this);
        fabFavorito.setOnClickListener(this);

        btnPartida.setEnabled(false);
        btnDestino.setEnabled(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.itinerarios, menu);

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

    private void ocultaCampos(){
        btnTodosHorarios.setVisibility(View.INVISIBLE);
        btnSecoes.setVisibility(View.INVISIBLE);
        textViewTarifa.setVisibility(View.INVISIBLE);
        textViewTaxaDeEmbarque.setVisibility(View.GONE);
        textViewEmpresa.setVisibility(View.INVISIBLE);
        textViewProximoHorarioLabel.setVisibility(View.INVISIBLE);
        textViewObsLabel.setVisibility(View.INVISIBLE);
        imgValor.setVisibility(View.INVISIBLE);
        imgEmpresa.setVisibility(View.INVISIBLE);
        textViewHorario.setVisibility(View.INVISIBLE);
        espacoBotoes.setVisibility(View.INVISIBLE);
        fabFavorito.setVisibility(View.INVISIBLE);
        spaceTaxa.setVisibility(View.GONE);
    }

    private void exibeCampos(HorarioItinerario horarioItinerario){
        btnTodosHorarios.setVisibility(View.VISIBLE);

        SecaoItinerarioDBHelper siDBHelper = new SecaoItinerarioDBHelper(getApplicationContext());
        List<SecaoItinerario> secoes = siDBHelper.listarTodasSecoesPorItinerario(getApplicationContext(),
                horarioItinerario.getItinerario());

        if(secoes.size() > 0){
            btnSecoes.setVisibility(View.VISIBLE);
            espacoBotoes.setVisibility(View.VISIBLE);
        } else{
            btnSecoes.setVisibility(View.GONE);
            espacoBotoes.setVisibility(View.GONE);
        }

        textViewTarifa.setVisibility(View.VISIBLE);
        textViewEmpresa.setVisibility(View.VISIBLE);
        textViewProximoHorarioLabel.setVisibility(View.VISIBLE);
        textViewObsLabel.setVisibility(View.VISIBLE);
        imgValor.setVisibility(View.VISIBLE);
        imgEmpresa.setVisibility(View.VISIBLE);
        textViewHorario.setVisibility(View.VISIBLE);
        fabFavorito.setVisibility(View.VISIBLE);
    }

    //########################## LISTENER ###############################################

    @Override
    public void onClick(View view) {

        Intent intent;

        switch(view.getId()){
            case R.id.btnTodosHorarios:
                Bairro bairroPartida = partidaEscolhida;
                Bairro bairroDestino = destinoEscolhido;

                intent = new Intent(getBaseContext(), TodosHorariosNovo.class);
                intent.putExtra("id_partida", bairroPartida.getId());
                intent.putExtra("id_destino", bairroDestino.getId());
                intent.putExtra("itinerario", horarioItinerario.getItinerario().getId());

                if(!textViewHorario.getText().toString().equals("N/D")){
                    intent.putExtra("hora", textViewHorario.getText().toString());
                }

                startActivity(intent);
                break;
            case R.id.btnLocal:

                LocalDBHelper localDBHelper = new LocalDBHelper(getBaseContext());

                List<Local> locais = localDBHelper.listarTodosVinculados(getBaseContext());

                ListviewComFiltro lista = new ListviewComFiltro();
                lista.setDados(locais);
                lista.setTipoObjeto("local");
                lista.setOnDismissListener(this);
                lista.show(getSupportFragmentManager(), "lista");
                break;
            case R.id.btnPartida:
                List<Bairro> partidas = bairroDBHelper.listarPartidaPorItinerario(getBaseContext(), localEscolhido);

                ListviewComFiltro listaPartidas = new ListviewComFiltro();
                listaPartidas.setDados(partidas);
                listaPartidas.setTipoObjeto("partida");
                listaPartidas.setOnDismissListener(this);
                listaPartidas.show(getSupportFragmentManager(), "listaPartida");
                break;
            case R.id.btnDestino:
                List<Bairro> destinos = bairroDBHelper.listarDestinoPorPartida(getBaseContext(), partidaEscolhida);

                ListviewComFiltro listaDestinos = new ListviewComFiltro();
                listaDestinos.setDados(destinos);
                listaDestinos.setTipoObjeto("destino");
                listaDestinos.setOnDismissListener(this);
                listaDestinos.show(getSupportFragmentManager(), "listaDestino");
                break;
            case R.id.btnSecoes:
                intent = new Intent(Itinerarios.this, SecoesActivity.class);
                intent.putExtra("id_itinerario", horarioItinerario.getItinerario().getId());
                startActivity(intent);
                break;
            case R.id.fabFavorito:

                List<String> lstItinerarios = PreferencesUtils.carregaItinerariosFavoritos(getApplicationContext());

                if(!flagFavorito){
                    SnackbarHelper.notifica(this.v, "Itinerário adicionado aos favoritos!", Snackbar.LENGTH_LONG);
                    fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
                    flagFavorito = true;

                    analyticsUtils.gravaAcaoValor("Itinerarios", "interacao", "favorito", "adicionado", tracker, "itinerario", partidaEscolhida + "|" + destinoEscolhido);

                    lstItinerarios.add(String.valueOf(partidaEscolhida.getId()+"|"+destinoEscolhido.getId()));

                } else{
                    SnackbarHelper.notifica(this.v, "Itinerário removido dos favoritos!", Snackbar.LENGTH_LONG);
                    fabFavorito.setImageResource(R.drawable.ic_star_border_white_24dp);
                    flagFavorito = false;

                    analyticsUtils.gravaAcaoValor("Itinerarios", "interacao", "favorito", "removido", tracker, "itinerario", partidaEscolhida + "|" + destinoEscolhido);

                    lstItinerarios.remove(String.valueOf(partidaEscolhida.getId()+"|"+destinoEscolhido.getId()));

                }

                PreferencesUtils.gravaItinerariosFavoritos(lstItinerarios, getApplicationContext());

                break;
            default:
                ToolbarUtils.onMenuItemClick(view, this);
                break;
        }

    }

    @Override
    public void onListviewComFiltroDismissed(Object result, String tipoObjeto, List<?> dados) {

        ocultaCampos();

        switch (tipoObjeto){
            case "local":
                localEscolhido = (Local) result;

                String estado = "";

                if(localEscolhido.getCidade() != null){
                    estado = localEscolhido.getCidade().getNome()+" - ";
                }

                estado = estado.concat(localEscolhido.getEstado().getNome());

                btnLocal.setText(localEscolhido.getNome() + "\r\n" + estado);

                // Testa se retornou apenas um resultado. Em caso positivo, ja segue o preenchimento do restante do formulario,
                // tanto partida quanto destino
                if(dados.size() == 1){
                    Bairro bairro = (Bairro) dados.get(0);
                    partidaEscolhida = bairro;
                    btnPartida.setText(bairro.getNome());
                    List destinos = bairroDBHelper.listarDestinoPorPartida(getBaseContext(), bairro);
                    btnPartida.setEnabled(true);
                    btnDestino.setEnabled(true);

                    if(destinos.size() == 1){
                        Bairro bairroDestino = (Bairro) destinos.get(0);
                        destinoEscolhido = bairroDestino;
                        btnDestino.setText(destinoEscolhido.getNome()+"\r\n"+destinoEscolhido.getLocal().getNome());
                        carregaProximoHorario(partidaEscolhida, destinoEscolhido);
                    } else{
                        btnDestino.setText("Escolha o Destino");
                        AnimaUtils.animaBotao(btnDestino);
                    }

                } else{
                    btnPartida.setEnabled(true);
                    btnPartida.setText("Escolha a Partida");
                    btnDestino.setEnabled(false);
                    btnDestino.setText("Escolha Antes a Partida");
                    AnimaUtils.animaBotao(btnPartida);
                }

                break;
            case "partida":
                partidaEscolhida = (Bairro) result;
                btnPartida.setText(partidaEscolhida.getNome());

                if(dados.size() == 1){
                    Bairro bairroDestino = (Bairro) dados.get(0);
                    destinoEscolhido = bairroDestino;
                    btnDestino.setText(destinoEscolhido.getNome()+"\r\n"+destinoEscolhido.getLocal().getNome());
                    carregaProximoHorario(partidaEscolhida, destinoEscolhido);
                } else{
                    btnDestino.setText("Escolha o Destino");
                    AnimaUtils.animaBotao(btnDestino);
                }

                btnDestino.setEnabled(true);

                break;
            case "destino":
                destinoEscolhido = (Bairro) result;
                btnDestino.setText(destinoEscolhido.getNome() + "\r\n" + destinoEscolhido.getLocal().getNome());

                carregaProximoHorario(partidaEscolhida, destinoEscolhido);

                break;
        }

    }

    public void carregaProximoHorario(Bairro partidaEscolhida, Bairro destinoEscolhido){

        tempoFinal = SystemClock.elapsedRealtime();
        tempoUtilizado = tempoFinal - tempoInicial;

        analyticsUtils.gravaAcaoTempo("Itinerarios", "interacao", "consulta", "itinerario", tracker, "tempo utilizado", tempoUtilizado);
        analyticsUtils.gravaAcaoValor("Itinerarios", "interacao", "consulta", "itinerario", tracker, "itinerario", partidaEscolhida + "|" + destinoEscolhido);

        List<String> lstItinerarios = PreferencesUtils.carregaItinerariosFavoritos(getApplicationContext());

        int i = lstItinerarios.indexOf(String.valueOf(partidaEscolhida.getId()+"|"+destinoEscolhido.getId()));

        if(i >= 0){
            fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;
        } else{
            fabFavorito.setImageResource(R.drawable.ic_star_border_white_24dp);
            flagFavorito = false;
        }

        ItinerarioLogDBHelper itinerarioLogDBHelper = new ItinerarioLogDBHelper(getBaseContext());
        ParadaItinerarioDBHelper paradaItinerarioDBHelper = new ParadaItinerarioDBHelper(getBaseContext());

        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        format.setDecimalFormatSymbols(symbols);

        String hora = df.format(cal.getTime());

        horarioItinerario = horarioItinerarioDBHelper.listarProximoHorarioItinerario(getBaseContext(),
                partidaEscolhida, destinoEscolhido, hora);

        if(null != horarioItinerario){
            textViewHorario.setText(horarioItinerario.getHorario().toString());
            exibeCampos(horarioItinerario);

            if(null != horarioItinerario.getItinerario().getObservacao()){
                textViewObsLabel.setVisibility(View.VISIBLE);
            }

        } else {
            Calendar diaSeguinte = Calendar.getInstance();
            diaSeguinte.add(Calendar.DATE, 1);
            horarioItinerario = horarioItinerarioDBHelper
                    .listarPrimeiroHorarioItinerario(getBaseContext(), partidaEscolhida, destinoEscolhido, diaSeguinte);

            if (null != horarioItinerario) {
                textViewHorario.setText(horarioItinerario.getHorario().toString());
                exibeCampos(horarioItinerario);

                if (null != horarioItinerario.getItinerario().getObservacao()) {
                    textViewObsLabel.setVisibility(View.VISIBLE);
                } else {
                    textViewObsLabel.setVisibility(View.GONE);
                }

            } else {
                textViewHorario.setText("N/D");
                btnTodosHorarios.setVisibility(View.INVISIBLE);
            }
        }

        Parada parada = paradaItinerarioDBHelper.carregarParadaEmbarque(getBaseContext(), horarioItinerario.getItinerario());

        if(parada.getTaxaDeEmbarque() != null && parada.getTaxaDeEmbarque() > 0){
            textViewTaxaDeEmbarque.setText("Taxa de embarque no valor de R$ " + format.format(parada.getTaxaDeEmbarque()));
            textViewTaxaDeEmbarque.setVisibility(View.VISIBLE);
            spaceTaxa.setVisibility(View.VISIBLE);
        }

        if(horarioItinerario.isTrecho()){
            horarioItinerario.getItinerario().setPartida(partidaEscolhida);
            horarioItinerario.getItinerario().setDestino(destinoEscolhido);
        }

        ItinerarioLog itinerarioLog = new ItinerarioLog();
        itinerarioLog.setItinerario(horarioItinerario.getItinerario());
        itinerarioLog.setDataConsulta(Calendar.getInstance());

        itinerarioLogDBHelper.salvar(getBaseContext(), itinerarioLog);

        textViewTarifa.setText("R$ " + format.format(horarioItinerario.getItinerario().getValor()));
        textViewEmpresa.setText(horarioItinerario.getItinerario().getEmpresa().getFantasia());

        if(null != horarioItinerario.getItinerario().getObservacao() &&
                !horarioItinerario.getItinerario().getObservacao().equals("") &&
                !horarioItinerario.getItinerario().getObservacao().equals("null")){
            textViewObsLabel.setText("("+horarioItinerario.getItinerario().getObservacao()+")");
        } else{
            textViewObsLabel.setVisibility(View.GONE);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(menu != null){
            invalidateOptionsMenu();

            ToolbarUtils.atualizaBadge(MessageUtils.getQuantidadeMensagensNaoLidas(getApplicationContext()));
        }

        if(partidaEscolhida != null && destinoEscolhido != null){
            List<String> lstItinerarios = PreferencesUtils.carregaItinerariosFavoritos(getApplicationContext());

            int i = lstItinerarios.indexOf(String.valueOf(partidaEscolhida.getId()+"|"+destinoEscolhido.getId()));

            if(i >= 0){
                fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
                flagFavorito = true;
            } else{
                fabFavorito.setImageResource(R.drawable.ic_star_border_white_24dp);
                flagFavorito = false;
            }
        }

    }

}
