package br.com.vostre.circular;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.HorarioItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaItinerarioDBHelper;
import br.com.vostre.circular.utils.HorarioList;
import br.com.vostre.circular.utils.ToolbarUtils;

public class TodosHorarios extends BaseActivity implements View.OnClickListener {

    private TextView txtVia;
    private TextView txtObs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_todos_horarios);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lista = (ListView) findViewById(R.id.listViewHorarios);
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(getBaseContext());
        HorarioItinerarioDBHelper horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(getBaseContext());
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());
        ParadaItinerarioDBHelper paradaItinerarioDBHelper = new ParadaItinerarioDBHelper(getBaseContext());
        BairroDBHelper bairroDBHelper = new BairroDBHelper(getBaseContext());

        Bundle valores = getIntent().getExtras();
        Itinerario itinerario = new Itinerario();
        itinerario.setId(valores.getInt("itinerario"));

        itinerario = itinerarioDBHelper.carregar(getBaseContext(), itinerario);

        TextView txtPartida = (TextView) findViewById(R.id.textViewTodosHorarios);
        TextView txtDestino = (TextView) findViewById(R.id.textViewItinerarioBairroPartida);

        txtVia = (TextView) findViewById(R.id.textViewVia);
        txtObs = (TextView) findViewById(R.id.textViewObs);

        String hora = "";

        if(valores.get("hora") != null){
            hora = valores.get("hora").toString();
        }

        List<ParadaItinerario> paradasItinerario = paradaItinerarioDBHelper
                .listaParadasDestaquePorItinerario(getBaseContext(), itinerario);

        if(paradasItinerario.size() > 0){

            int cont = 0;
            String via = "";

            for(ParadaItinerario umaParada : paradasItinerario){

                if(cont == 0){
                    via = via.concat("Via "+umaParada.getParada().getBairro().getLocal().getNome());
                    cont++;
                } else{
                    via = via.concat(", "+umaParada.getParada().getBairro().getLocal().getNome());
                }

            }

            txtVia.setText(via);

        } else{
            txtVia.setVisibility(View.GONE);
        }

        if(itinerario.getObservacao() != null &&
                !itinerario.getObservacao().equals("") &&
                !itinerario.getObservacao().equals("null")){
            txtObs.setText("("+itinerario.getObservacao()+")");
        } else{
            txtObs.setVisibility(View.GONE);
        }

        if(itinerario.getPartida().getLocal().getId() !=
                itinerario.getDestino().getLocal().getId()){
            txtDestino.setText(itinerario.getPartida().getLocal().getNome()+" x "
                    +itinerario.getDestino().getLocal().getNome());
        } else{
            txtDestino.setText(itinerario.getPartida().getNome()+" x "
                    +itinerario.getDestino().getNome());
        }

        List<HorarioItinerario> todosHorariosItinerario = horarioItinerarioDBHelper
                .listarTodosHorariosPorItinerario(getBaseContext(), itinerario);

        HorarioList adapterHorarios =
                new HorarioList(TodosHorarios.this, android.R.layout.simple_spinner_dropdown_item, todosHorariosItinerario, hora);

        adapterHorarios.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        lista.setAdapter(adapterHorarios);

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

        ToolbarUtils.onMenuItemClick(v, this);

    }

}
