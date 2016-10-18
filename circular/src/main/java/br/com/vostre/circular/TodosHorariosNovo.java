package br.com.vostre.circular;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.HorarioItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaItinerarioDBHelper;
import br.com.vostre.circular.utils.AnalyticsUtils;
import br.com.vostre.circular.utils.HorarioList;
import br.com.vostre.circular.utils.PreferencesUtils;
import br.com.vostre.circular.utils.ScreenPagerAdapter;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.utils.ToolbarUtils;

public class TodosHorariosNovo extends BaseActivity implements View.OnClickListener {

    private TextView txtVia;
    private TextView txtObs;
    private TextView txtAviso;
    private ViewPager pager;
    private ScreenPagerAdapter pagerAdapter;
    FloatingActionButton fabFavorito;
    boolean flagFavorito = false;

    Bairro bairroPartida;
    Bairro bairroDestino;

    View v;

    Tracker tracker;
    AnalyticsUtils analyticsUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_todos_horarios_novo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        analyticsUtils = new AnalyticsUtils();
        tracker = analyticsUtils.getTracker();

        if(tracker == null){
            tracker = analyticsUtils.iniciaAnalytics(getApplicationContext());
        }

        v = findViewById(R.id.principal_todos_horarios);

        txtAviso = (TextView) findViewById(R.id.textViewAviso);

        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());
        BairroDBHelper bairroDBHelper = new BairroDBHelper(getBaseContext());

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(pagerAdapter);

        Bundle valores = getIntent().getExtras();

        fabFavorito = (FloatingActionButton) findViewById(R.id.fabFavorito);

        fabFavorito.setOnClickListener(this);

        String proximoHorario = valores.getString("horaProximo");

        Itinerario itinerario = new Itinerario();
        itinerario.setId(valores.getInt("itinerario"));

        itinerario = itinerarioDBHelper.carregar(getBaseContext(), itinerario);

        bairroPartida = new Bairro();
        bairroPartida.setId(valores.getInt("id_partida"));
        bairroPartida = bairroDBHelper.carregar(getBaseContext(), bairroPartida);

        bairroDestino = new Bairro();
        bairroDestino.setId(valores.getInt("id_destino"));
        bairroDestino = bairroDBHelper.carregar(getBaseContext(), bairroDestino);

        itinerario.setPartida(bairroPartida);
        itinerario.setDestino(bairroDestino);

        String hora = valores.getString("hora");
        int diaDaSemana = valores.getInt("dia_semana");

        if(diaDaSemana == -1){
            diaDaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        }

        if(hora == null){
            DateFormat df = new SimpleDateFormat("HH:mm");
            hora = df.format(Calendar.getInstance().getTime());
        }

        List<String> lstItinerarios = PreferencesUtils.carregaItinerariosFavoritos(getApplicationContext());

        int i = lstItinerarios.indexOf(String.valueOf(bairroPartida.getId() + "|" + bairroDestino.getId()));

        if(i >= 0){
            fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;
        } else{
            fabFavorito.setImageResource(R.drawable.ic_star_border_white_24dp);
            flagFavorito = false;
        }

        if(proximoHorario == null){
            HorarioItinerarioDBHelper hiDBHelper = new HorarioItinerarioDBHelper(getApplicationContext());
            HorarioItinerario hi = hiDBHelper.listarProximoHorarioItinerario(getApplicationContext(), bairroPartida, bairroDestino, hora, diaDaSemana);
            proximoHorario = hi.getHorario().toString();
        }

        FragmentTodosHorarios f = new FragmentTodosHorarios();
        Bundle args = new Bundle();
        args.putInt("itinerario", itinerario.getId());
        args.putString("proximoHorario", proximoHorario);
        f.setArguments(args);

        pagerAdapter.addView(f, 0);
        pagerAdapter.notifyDataSetChanged();

        List<HorarioItinerario> itinerarios = itinerarioDBHelper.listarOutrasOpcoesItinerario(getBaseContext(), itinerario, hora, diaDaSemana);

        if(itinerarios.size() > 0){

            int cont = 1;

            for(HorarioItinerario umItinerario : itinerarios){
                FragmentTodosHorarios fh = new FragmentTodosHorarios();
                Bundle args1 = new Bundle();
                args1.putInt("itinerario", umItinerario.getItinerario().getId());
                args1.putString("proximoHorario", umItinerario.getHorario().toString());
                fh.setArguments(args1);

                pagerAdapter.addView(fh, cont);
                cont++;
            }

            pagerAdapter.notifyDataSetChanged();

        }

        if(pagerAdapter.getCount() < 2){
            txtAviso.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.todos_horarios, menu);

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

                List<String> lstItinerarios = PreferencesUtils.carregaItinerariosFavoritos(getApplicationContext());

                if(!flagFavorito){
                    SnackbarHelper.notifica(this.v, "Itinerário adicionado aos favoritos!", Snackbar.LENGTH_LONG);
                    fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
                    flagFavorito = true;

                    analyticsUtils.gravaAcaoValor("Todos Horarios", "interacao", "favorito", "adicionado", tracker, "itinerario", bairroPartida.getId() + "|" + bairroDestino.getId());

                    lstItinerarios.add(String.valueOf(bairroPartida.getId()+"|"+bairroDestino.getId()));

                } else{
                    SnackbarHelper.notifica(this.v, "Itinerário removido dos favoritos!", Snackbar.LENGTH_LONG);
                    fabFavorito.setImageResource(R.drawable.ic_star_border_white_24dp);
                    flagFavorito = false;

                    analyticsUtils.gravaAcaoValor("Todos Horarios", "interacao", "favorito", "removido", tracker, "itinerario", bairroPartida.getId() + "|" + bairroDestino.getId());

                    lstItinerarios.remove(String.valueOf(bairroPartida.getId()+"|"+bairroDestino.getId()));

                }

                PreferencesUtils.gravaItinerariosFavoritos(lstItinerarios, getApplicationContext());

                break;
            default:
                ToolbarUtils.onMenuItemClick(v, this);
                break;
        }



    }

}
