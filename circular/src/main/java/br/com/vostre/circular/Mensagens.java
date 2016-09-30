package br.com.vostre.circular;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.Constants;
import br.com.vostre.circular.utils.MensagemList;
import br.com.vostre.circular.utils.MessageService;
import br.com.vostre.circular.utils.ModalCadastroListener;
import br.com.vostre.circular.utils.ModalCadastroMensagem;
import br.com.vostre.circular.utils.ModalCadastroParada;
import br.com.vostre.circular.utils.ModalMensagemListener;
import br.com.vostre.circular.utils.NotificacaoUtils;
import br.com.vostre.circular.utils.ScreenPagerAdapter;
import br.com.vostre.circular.utils.SendMessageService;
import br.com.vostre.circular.utils.ServiceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.utils.ToolbarUtils;

public class Mensagens extends BaseActivity implements View.OnClickListener, ModalCadastroListener, TabLayout.OnTabSelectedListener {

    Menu menu;
    BroadcastReceiver receiver;

    FloatingActionButton fabNova;

    private ViewPager pager;
    private ScreenPagerAdapter pagerAdapter;

    FragmentMensagensEnviadas me;
    FragmentMensagensRecebidas mr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("Recebidas"));
        tabLayout.addTab(tabLayout.newTab().setText("Enviadas"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mr = new FragmentMensagensRecebidas();
        me = new FragmentMensagensEnviadas();

        pagerAdapter.addView(mr, 0);
        pagerAdapter.addView(me, 1);
        pagerAdapter.notifyDataSetChanged();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NotificacaoUtils.removeNotificacao(getBaseContext(), Constants.ID_NOTIFICACAO_MSG);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                Integer qtdMensagens = extras.getInt("mensagens");

                if(qtdMensagens != null){

                    mr.atualizaLista();

                }

            }
        };

        fabNova = (FloatingActionButton) findViewById(R.id.fabNova);

        fabNova.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mensagens, menu);
        return true;

//        this.menu = menu;
//        ToolbarUtils.preparaMenuMensagem(menu, this, this);
//
//        return super.onCreateOptionsMenu(menu);

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
    protected void onResume() {
        super.onResume();
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
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case R.id.fabNova:
                ModalCadastroMensagem modalCadastroMensagem = new ModalCadastroMensagem();
                modalCadastroMensagem.setListener(this);

                modalCadastroMensagem.show(getSupportFragmentManager(), "modalMensagem");
                break;
            default:
                ToolbarUtils.onMenuItemClick(v, this);
                break;
        }
    }

    @Override
    public void onModalCadastroDismissed(int resultado) {

        Intent serviceIntent = new Intent(getBaseContext(), SendMessageService.class);

        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ServiceUtils serviceUtils = new ServiceUtils();

        if(!serviceUtils.isMyServiceRunning(SendMessageService.class, manager)){
            stopService(serviceIntent);
            startService(serviceIntent);
        }

        me.atualizaLista();

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
