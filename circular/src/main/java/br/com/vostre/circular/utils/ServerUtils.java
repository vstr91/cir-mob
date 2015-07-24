package br.com.vostre.circular.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Almir on 13/09/2014.
 */
public class ServerUtils extends AsyncTask<String, Void, Boolean> {

    Context context;
    ProgressDialog progressDialog;
    ServerUtilsListener listener;
    boolean isBackground;

    public ServerUtils(Context context, boolean isBackground){
        this.context = context;
        this.isBackground = isBackground;
    }

    public void setOnResultsListener(ServerUtilsListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        if(!isBackground){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Verificando conexão...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onServerUtilsResultsSucceeded(aBoolean);

        if(!isBackground){
            progressDialog.dismiss();
        }

    }

    @Override
    protected Boolean doInBackground(String... strings) {

        boolean exists = false;

        try {
            SocketAddress sockaddr = new InetSocketAddress(strings[0], Integer.parseInt(strings[1]));
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 20000;   // 2 seconds
            //System.out.println("SOCK: "+sockaddr);
            sock.connect(sockaddr, timeoutMs);
            exists = true;
        }catch(Exception e){
            //System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return exists;

    }
}
