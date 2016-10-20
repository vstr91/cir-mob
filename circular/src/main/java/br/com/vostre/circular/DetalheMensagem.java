package br.com.vostre.circular;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.DateUtils;
import br.com.vostre.circular.utils.MensagemList;
import br.com.vostre.circular.utils.MessageUtils;
import br.com.vostre.circular.utils.ModalMensagemListener;
import br.com.vostre.circular.utils.ToolbarUtils;

public class DetalheMensagem extends BaseActivity implements View.OnClickListener {

    TextView textViewTitulo;
    TextView textViewHora;
    TextView textViewDescricao;
    Mensagem mensagem;

    Menu menu;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_mensagem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(getBaseContext());

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

        textViewTitulo = (TextView) findViewById(R.id.textViewTituloMensagem);
        textViewHora = (TextView) findViewById(R.id.textViewDataMensagem);
        textViewDescricao = (TextView) findViewById(R.id.textViewDetalheMensagem);

        Bundle valores = getIntent().getExtras();
        int idMensagem = Integer.parseInt(valores.get("mensagem").toString());

        if(idMensagem >0){
            mensagem = new Mensagem();
            mensagem.setId(idMensagem);
            mensagem = mensagemDBHelper.carregar(getApplicationContext(), mensagem);

            String dataFormatada = DateUtils.converteDataParaPadraoBrasil(mensagem.getDataEnvio().getTime());

            textViewTitulo.setText(mensagem.getTitulo());
            textViewHora.setText("Enviada em "+dataFormatada);
            textViewDescricao.setText(Html.fromHtml(mensagem.getDescricao()));

            if(mensagem.getStatus() == 0){

                if(mensagemDBHelper.marcarLida(getApplicationContext(), mensagem) > 0){
                    mensagem.setStatus(5); // status 5 => lida
                }

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mensagens, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("id_mensagem", getMensagem().getId());
        super.onSaveInstanceState(outState);
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public void onClick(View v) {

        ToolbarUtils.onMenuItemClick(v, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

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

}
