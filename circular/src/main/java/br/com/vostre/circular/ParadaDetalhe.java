package br.com.vostre.circular;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.CustomAdapter;
import br.com.vostre.circular.utils.ItinerarioList;

public class ParadaDetalhe extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_parada_detalhe);
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(getBaseContext());
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());

        TextView txtReferencia = (TextView) findViewById(R.id.textViewReferencia);
        TextView txtBairro = (TextView) findViewById(R.id.textViewBairroParada);
        ListView listaItinerarios = (ListView) findViewById(R.id.listViewItinerarios);

        Bundle valores = getIntent().getExtras();
        int idParada = Integer.parseInt(valores.get("id_parada").toString());

        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();

        String hora = df.format(cal.getTime());

        Parada parada = new Parada();
        parada.setId(idParada);

        parada = paradaDBHelper.carregar(getBaseContext(), parada);
        List<HorarioItinerario> listItinerarios = itinerarioDBHelper.listarTodosPorParada(getBaseContext(), parada, hora);

        final ItinerarioList adapterItinerario = new ItinerarioList(ParadaDetalhe.this,
                android.R.layout.simple_spinner_dropdown_item, listItinerarios);
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parada_detalhe, menu);
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
