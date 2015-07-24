package br.com.vostre.circular;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import br.com.vostre.circular.utils.HorarioList;
import br.com.vostre.circular.utils.ScreenPagerAdapter;

public class TodosHorariosNovo extends ActionBarActivity {

    private TextView txtVia;
    private TextView txtObs;
    private TextView txtAviso;
    private ViewPager pager;
    private ScreenPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_todos_horarios_novo);

        txtAviso = (TextView) findViewById(R.id.textViewAviso);

        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());
        BairroDBHelper bairroDBHelper = new BairroDBHelper(getBaseContext());

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(pagerAdapter);

        Bundle valores = getIntent().getExtras();
        String proximoHorario = valores.getString("hora");

        Itinerario itinerario = new Itinerario();
        itinerario.setId(valores.getInt("itinerario"));

        itinerario = itinerarioDBHelper.carregar(getBaseContext(), itinerario);

        Bairro bairroPartida = new Bairro();
        bairroPartida.setId(valores.getInt("id_partida"));
        bairroPartida = bairroDBHelper.carregar(getBaseContext(), bairroPartida);

        Bairro bairroDestino = new Bairro();
        bairroDestino.setId(valores.getInt("id_destino"));
        bairroDestino = bairroDBHelper.carregar(getBaseContext(), bairroDestino);

        itinerario.setPartida(bairroPartida);
        itinerario.setDestino(bairroDestino);

        FragmentTodosHorarios f = new FragmentTodosHorarios();
        Bundle args = new Bundle();
        args.putInt("itinerario", itinerario.getId());
        args.putString("proximoHorario", proximoHorario);
        f.setArguments(args);

        pagerAdapter.addView(f, 0);
        pagerAdapter.notifyDataSetChanged();

        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();

        String hora = df.format(cal.getTime());

        List<HorarioItinerario> itinerarios = itinerarioDBHelper.listarOutrasOpcoesItinerario(getBaseContext(), itinerario, hora);

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
        getMenuInflater().inflate(R.menu.todos_horarios, menu);
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
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;
            case R.id.icon_sobre:
                intent = new Intent(this, Sobre.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
