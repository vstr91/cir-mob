package br.com.vostre.circular.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import br.com.vostre.circular.AtualizarDados;
import br.com.vostre.circular.MainActivity;
import br.com.vostre.circular.R;

/**
 * Created by Almir on 24/03/2016.
 */
public class NotificacaoUtils {

    public static void criaNotificacao(Class anterior, Class destino, Context context, String titulo, String mensagem, int id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.icon_2016_transparente);
        builder.setContentTitle(titulo);
        builder.setContentText(mensagem);
        builder.setAutoCancel(true);
        //Notification notification = builder.build();

        Intent backIntent = new Intent(context, anterior);
        backIntent.putExtra("flagVerifica", false);
        Intent intent = new Intent(context, destino);

        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
        stackBuilder.addParentStack(anterior);
        stackBuilder.addNextIntent(intent);

        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent =
                PendingIntent.getActivities(context, 200, new Intent[]{backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(id, builder.build());
    }

    public static void removeNotificacao(Context ctx, int id) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(id);
    }
    
}
