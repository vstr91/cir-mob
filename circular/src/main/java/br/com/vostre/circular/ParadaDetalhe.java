package br.com.vostre.circular;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.AnalyticsUtils;
import br.com.vostre.circular.utils.ItinerarioList;
import br.com.vostre.circular.utils.PreferencesUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.utils.ToolbarUtils;

public class ParadaDetalhe extends BaseActivity implements View.OnClickListener {

    View v;
    FloatingActionButton fabFavorito;
    Boolean flagFavorito = false;
    int idParada;

    Tracker tracker;
    AnalyticsUtils analyticsUtils;

    TextView textViewTaxaDeEmbarque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parada_detalhe);
        //PreferencesUtils.salvarPreferencia(getBaseContext(), getPackageName() + ".paradas_favoritas", "");

        v = (LinearLayout) findViewById(R.id.baseParadaDetalhe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        analyticsUtils = new AnalyticsUtils();
        tracker = analyticsUtils.getTracker();

        if(tracker == null){
            tracker = analyticsUtils.iniciaAnalytics(getApplicationContext());
        }

        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(getBaseContext());
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());

        TextView txtReferencia = (TextView) findViewById(R.id.textViewReferencia);
        textViewTaxaDeEmbarque = (TextView) findViewById(R.id.textViewTaxaDeEmbarque);
        TextView txtBairro = (TextView) findViewById(R.id.textViewBairroParada);
        ListView listaItinerarios = (ListView) findViewById(R.id.listViewItinerarios);
        fabFavorito = (FloatingActionButton) findViewById(R.id.fabFavorito);

        textViewTaxaDeEmbarque.setVisibility(View.GONE);

        Bundle valores = getIntent().getExtras();
        idParada = Integer.parseInt(valores.get("id_parada").toString());


        List<String> lstParadas = PreferencesUtils.carregaParadasFavoritas(getApplicationContext());

        int i = lstParadas.indexOf(String.valueOf(idParada));

        if(i >= 0){
            fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;
        }

        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();

        DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        format.setDecimalFormatSymbols(symbols);

        String hora = df.format(cal.getTime());

        Parada parada = new Parada();
        parada.setId(idParada);

        parada = paradaDBHelper.carregar(getBaseContext(), parada);

        if(parada.getTaxaDeEmbarque() > 0){
            textViewTaxaDeEmbarque.setText("Taxa de embarque no valor de R$ "+format.format(parada.getTaxaDeEmbarque()));
            textViewTaxaDeEmbarque.setVisibility(View.VISIBLE);
        }

        List<HorarioItinerario> listItinerarios = itinerarioDBHelper.listarTodosPorParada(getBaseContext(), parada, hora);

        final ItinerarioList adapterItinerario = new ItinerarioList(ParadaDetalhe.this,
                android.R.layout.simple_spinner_dropdown_item, listItinerarios, parada);
        adapterItinerario.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        listaItinerarios.setAdapter(adapterItinerario);

        txtReferencia.setText(parada.getReferencia());
        txtBairro.setText(parada.getBairro().getNome()+" - "+parada.getBairro().getLocal().getNome());

        listaItinerarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HorarioItinerario itinerario = adapterItinerario.getItem(i);

                Intent intent = new Intent(getBaseContext(), TodosHorarios.class);
                intent.putExtra("id_partida", itinerario.getItinerario().getPartida().getId());
                intent.putExtra("id_destino", itinerario.getItinerario().getDestino().getId());
                intent.putExtra("itinerario", itinerario.getItinerario().getId());
                intent.putExtra("hora", itinerario.getHorario().toString());
                startActivity(intent);
                /*
                Intent intent = new Intent(getBaseContext(), ItinerarioDetalhe.class);
                intent.putExtra("id_itinerario", itinerario.getItinerario().getId());
                startActivity(intent);
                */
            }
        });

        fabFavorito.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.parada_detalhe, menu);

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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fabFavorito:

                List<String> lstParadas = PreferencesUtils.carregaParadasFavoritas(getApplicationContext());

                if(!flagFavorito){
                    SnackbarHelper.notifica(this.v, "Parada adicionada aos favoritos!", Snackbar.LENGTH_LONG);
                    fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
                    flagFavorito = true;

                    analyticsUtils.gravaAcaoValor("Parada Detalhe", "interacao", "favorito", "adicionado", tracker, "parada", String.valueOf(idParada));

                    lstParadas.add(String.valueOf(idParada));

                } else{
                    SnackbarHelper.notifica(this.v, "Parada removida dos favoritos!", Snackbar.LENGTH_LONG);
                    fabFavorito.setImageResource(R.drawable.ic_star_border_white_24dp);
                    flagFavorito = false;

                    analyticsUtils.gravaAcaoValor("Parada Detalhe", "interacao", "favorito", "removido", tracker, "parada", String.valueOf(idParada));

                    lstParadas.remove(String.valueOf(idParada));

                }

                PreferencesUtils.gravaParadasFavoritas(lstParadas, getApplicationContext());

                break;
            default:
                ToolbarUtils.onMenuItemClick(v, this);
                break;
        }



    }




}
