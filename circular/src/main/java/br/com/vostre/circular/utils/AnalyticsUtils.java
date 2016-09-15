package br.com.vostre.circular.utils;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.com.vostre.circular.R;

/**
 * Created by Almir on 03/08/2015.
 */
public class AnalyticsUtils extends Application {

    private Tracker tracker;

    public Tracker iniciaAnalytics(Context context){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        analytics.setLocalDispatchPeriod(60);
        //analytics.setDryRun(true);

        Tracker tracker = analytics.newTracker("UA-65884694-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        this.tracker = tracker;

        return this.tracker;
    }

    public void gravaAcaoEvento(String tela, String categoria, String acao, String rotulo, Tracker tracker){
        tracker.setScreenName(tela);
        tracker.send(new HitBuilders.EventBuilder().setCategory(categoria).setAction(acao).setLabel(rotulo).build());
    }

    public void gravaAcaoTempo(String tela, String categoria, String acao, String rotulo, Tracker tracker, String variavel, Long valor){
        tracker.setScreenName(tela);
        tracker.send(
                new HitBuilders.TimingBuilder()
                .setCategory(categoria)
                .setValue(valor).setVariable(variavel)
                .setLabel(rotulo)
                .build()
        );
    }

    public void gravaAcaoValor(String tela, String categoria, String acao, String rotulo, Tracker tracker, String variavel, String valor){
        tracker.setScreenName(tela);
        tracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(categoria)
                        //.setVariable(variavel)
                        .setLabel(rotulo)
                        .build()
        );
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }
}
