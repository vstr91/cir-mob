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

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.utils.ItinerarioList;


public class OpcoesItinerario extends ActionBarActivity {

    private TextView textViewItinerarioBase;
    private ListView listOpcoesItinerario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_opcoes_itinerario);

        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());
        BairroDBHelper bairroDBHelper = new BairroDBHelper(getBaseContext());

        textViewItinerarioBase = (TextView) findViewById(R.id.textViewItinerarioBase);
        listOpcoesItinerario = (ListView) findViewById(R.id.listOpcoesItinerario);

        Bundle valores = getIntent().getExtras();
        int id = valores.getInt("itinerario");
        String hora = valores.getString("hora");

        Itinerario itinerario = new Itinerario();
        itinerario.setId(id);
        itinerario = itinerarioDBHelper.carregar(getBaseContext(), itinerario);

        Bairro bairroPartida = new Bairro();
        bairroPartida.setId(valores.getInt("id_partida"));
        bairroPartida = bairroDBHelper.carregar(getBaseContext(), bairroPartida);

        Bairro bairroDestino = new Bairro();
        bairroDestino.setId(valores.getInt("id_destino"));
        bairroDestino = bairroDBHelper.carregar(getBaseContext(), bairroDestino);

        itinerario.setPartida(bairroPartida);
        itinerario.setDestino(bairroDestino);

        List<HorarioItinerario> itinerarios = itinerarioDBHelper.listarOutrasOpcoesItinerario(getBaseContext(), itinerario, hora);

        final ItinerarioList adapterItinerario = new ItinerarioList(OpcoesItinerario.this,
                android.R.layout.simple_spinner_dropdown_item, itinerarios);
        adapterItinerario.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        listOpcoesItinerario.setAdapter(adapterItinerario);

        if(itinerario.getPartida().getLocal().getId() !=
                itinerario.getDestino().getLocal().getId()){
            textViewItinerarioBase.setText(itinerario.getPartida().getLocal().getNome()+" X "
                    +itinerario.getDestino().getLocal().getNome());
        } else{
            textViewItinerarioBase.setText(itinerario.getPartida().getNome()+" X "
                    +itinerario.getDestino().getNome());
        }

        listOpcoesItinerario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        getMenuInflater().inflate(R.menu.menu_opcoes_itinerario, menu);
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
