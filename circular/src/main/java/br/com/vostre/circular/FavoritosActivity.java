package br.com.vostre.circular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.google.android.gms.analytics.Tracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.vostre.circular.R;
import br.com.vostre.circular.utils.AnalyticsUtils;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.MessageUtils;
import br.com.vostre.circular.utils.ScreenPagerAdapter;
import br.com.vostre.circular.utils.ToolbarUtils;

public class FavoritosActivity extends BaseActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    public static Tracker tracker;
    BroadcastReceiver receiver;

    Menu menu;
    private ViewPager pager;
    private ScreenPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("Itinerários"));
        tabLayout.addTab(tabLayout.newTab().setText("Paradas"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(this);

        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        FragmentParadasFavoritas f = new FragmentParadasFavoritas();
        FragmentItinerariosFavoritos fi = new FragmentItinerariosFavoritos();

        pagerAdapter.addView(fi, 0);
        pagerAdapter.addView(f, 1);
        pagerAdapter.notifyDataSetChanged();



        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();

        AnalyticsUtils analyticsUtils = new AnalyticsUtils();
        tracker = analyticsUtils.getTracker();

        if(tracker == null){
            tracker = analyticsUtils.iniciaAnalytics(getApplicationContext());
        }

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        /*if(iniciaModoCamera){
            getMenuInflater().inflate(R.menu.realidade_aumentada, menu);
        } else{
            getMenuInflater().inflate(R.menu.main, menu);
        }*/

        this.menu = menu;

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
            /*case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;*/
            case R.id.icon_msg:
                intent = new Intent(this, Mensagens.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        ToolbarUtils.onMenuItemClick(v, this);

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
    protected void onResume() {
        super.onResume();

        if(menu != null){
            invalidateOptionsMenu();

            ToolbarUtils.atualizaBadge(MessageUtils.getQuantidadeMensagensNaoLidas(getApplicationContext()));
        }
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
