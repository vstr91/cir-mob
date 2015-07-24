package br.com.vostre.circular;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.CustomAdapter;

public class ItinerarioDetalhe extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_itinerario_detalhe);
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(getBaseContext());
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());

        TextView txtItinerario = (TextView) findViewById(R.id.textViewItinerario);
        ListView listaHorarios = (ListView) findViewById(R.id.listViewHorarios);

        Bundle valores = getIntent().getExtras();
        int idItinerario = Integer.parseInt(valores.get("id_itinerario").toString());

        Itinerario itinerario = new Itinerario();
        itinerario.setId(idItinerario);

        itinerario = itinerarioDBHelper.carregar(getBaseContext(), itinerario);
        List<Horario> listHorarios = horarioDBHelper.listarTodosHorariosPorItinerario(getBaseContext(),
                itinerario.getPartida(), itinerario.getDestino());

        CustomAdapter<Horario> adapterHorario = new CustomAdapter<Horario>(getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item, listHorarios, "", listHorarios.size());
        adapterHorario.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        txtItinerario.setText(itinerario.getPartida().getNome()+" x "+itinerario.getDestino().getNome());

        listaHorarios.setAdapter(adapterHorario);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.itinerario_detalhe, menu);
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
