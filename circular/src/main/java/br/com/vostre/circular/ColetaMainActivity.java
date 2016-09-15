package br.com.vostre.circular;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.utils.FileUtils;
import br.com.vostre.circular.utils.ToolbarUtils;


public class ColetaMainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawer;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coleta_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.logo_resumida_icon);

        drawer = (DrawerLayout) findViewById(R.id.container);
        NavigationView navView = (NavigationView) findViewById(R.id.nav);

        navView.setNavigationItemSelectedListener(this);

        navView.getMenu().getItem(1).setChecked(true);

        // --------------------------------------------------------

        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                drawerToggle.syncState();
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                drawerToggle.syncState();
            }

        };

        // --------------------------------------------------------

        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_coleta_main, menu);

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
                break;
            case R.id.icon_sobre:
                intent = new Intent(this, Sobre.class);
                startActivity(intent);
                break;*/
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        Intent i;

        switch (menuItem.getItemId()){
//            case R.id.nav_consulta:
//                menuItem.setChecked(true);
//                drawer.closeDrawers();
//                finish();
//                break;
//            case R.id.nav_cadastro:
//                menuItem.setChecked(true);
//                drawer.closeDrawers();
//                break;
            case R.id.opcoes:
                i = new Intent(this, Parametros.class);
                drawer.closeDrawers();
                startActivity(i);
                break;
            case R.id.sobre:
                i = new Intent(this, Sobre.class);
                drawer.closeDrawers();
                startActivity(i);
                break;
        }

        return true;
    }

    public void abreTelaMapa(View view){

        //FileUtils.exportDatabase("circular.db", "circularbkp.db", ColetaMainActivity.this);

        //Toast.makeText(ColetaMainActivity.this, "Banco de Dados Exportado", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, Mapa.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {

        ToolbarUtils.onMenuItemClick(v, this);

    }

}
