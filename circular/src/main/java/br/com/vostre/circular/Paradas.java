package br.com.vostre.circular;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.AnimaUtils;
import br.com.vostre.circular.utils.ListviewComFiltro;
import br.com.vostre.circular.utils.ListviewComFiltroListener;
import br.com.vostre.circular.utils.ParadaList;
import br.com.vostre.circular.utils.ToolbarUtils;

public class Paradas extends BaseActivity implements ListviewComFiltroListener, View.OnClickListener {

    ParadaList adapterParada;
    ListView listaParadas;

    LocalDBHelper localDBHelper;
    BairroDBHelper bairroDBHelper;
    ParadaDBHelper paradaDBHelper;

    Button btnCidade;
    Button btnBairro;

    Local localEscolhido;
    Bairro bairroEscolhido;

    ListviewComFiltro lista;
    ListviewComFiltro listaPartidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paradas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Estado> estadosDb = new ArrayList<Estado>();

        localDBHelper = new LocalDBHelper(getBaseContext());
        bairroDBHelper = new BairroDBHelper(getBaseContext());
        paradaDBHelper = new ParadaDBHelper(getBaseContext());

        listaParadas = (ListView) findViewById(R.id.listViewParadas);

        btnCidade = (Button) findViewById(R.id.btnCidade);
        btnBairro = (Button) findViewById(R.id.btnBairro);

        btnCidade.setOnClickListener(this);
        btnBairro.setOnClickListener(this);
        btnBairro.setEnabled(false);

        listaParadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Parada parada = adapterParada.getItem(i);

                Intent intent = new Intent(getBaseContext(), ParadaDetalhe.class);
                intent.putExtra("id_parada", parada.getId());
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.paradas, menu);

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

                btnCidade.setText(localEscolhido.getNome() + "\r\n" + estado);

                // Testa se retornou apenas um resultado. Em caso positivo, ja segue o preenchimento do restante do formulario,
                // tanto partida quanto destino
                if(dados.size() == 1){
                    Bairro bairro = (Bairro) dados.get(0);
                    bairroEscolhido = bairro;
                    btnBairro.setText(bairro.getNome());
                    btnBairro.setEnabled(true);
                    carregaParadasBairro(bairroEscolhido);
                } else{
                    btnBairro.setEnabled(true);
                    btnBairro.setText("Escolha o Bairro");
                    AnimaUtils.animaBotao(btnBairro);
                }
                break;
            case "bairro":
                Bairro bairro = (Bairro) result;
                bairroEscolhido = bairro;
                btnBairro.setText(bairro.getNome());
                btnBairro.setEnabled(true);
                carregaParadasBairro(bairroEscolhido);
                break;
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnCidade:
                LocalDBHelper localDBHelper = new LocalDBHelper(getBaseContext());

                List<Local> locais = localDBHelper.listarTodosVinculados(getBaseContext());

                lista = new ListviewComFiltro();
                lista.setDados(locais);
                lista.setTipoObjeto("local");
                lista.setOnDismissListener(this);
                lista.show(getSupportFragmentManager(), "lista");
                break;
            case R.id.btnBairro:
                List<Bairro> bairros = bairroDBHelper.listarPartidaPorItinerario(getBaseContext(), localEscolhido);

                listaPartidas = new ListviewComFiltro();
                listaPartidas.setDados(bairros);
                listaPartidas.setTipoObjeto("bairro");
                listaPartidas.setOnDismissListener(this);
                listaPartidas.show(getSupportFragmentManager(), "listaPartida");
                break;
            default:
                ToolbarUtils.onMenuItemClick(view, this);
                break;
        }

    }

    @Override
    protected void onPause() {

        if(lista != null && lista.isVisible()){
            lista.dismiss();
        }

        if(listaPartidas != null && listaPartidas.isVisible()){
            listaPartidas.dismiss();
        }

        super.onPause();
    }

    public void carregaParadasBairro(Bairro bairro){
        List<Parada> listParadas = paradaDBHelper.listarTodosComItinerarioPorBairro(getBaseContext(), bairro);
        adapterParada = new ParadaList(Paradas.this,
                R.layout.listview_paradas, listParadas);

        if(!adapterParada.isEmpty()){
            exibeCampos();
        }

        listaParadas.setAdapter(adapterParada);
    }

    public void ocultaCampos(){
        listaParadas.setVisibility(View.INVISIBLE);
    }

    public void exibeCampos(){
        listaParadas.setVisibility(View.VISIBLE);
    }

}