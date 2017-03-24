package br.com.vostre.circular.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

/**
 * Created by Almir on 09/03/2015.
 */
public class FileUtils {

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public static void exportDatabase(String databaseName, String exportedDatabase, Activity activity) {
/*
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_STORAGE,
                    Constants.REQUEST_EXTERNAL_STORAGE
            );
        } else{
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                String state = Environment.getExternalStorageState();

                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    String currentDBPath = "//data//"+activity.getPackageName()+"//databases//"+databaseName+"";
                    String backupDBPath = exportedDatabase;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Toast.makeText(activity.getBaseContext(), "Banco exportado!", Toast.LENGTH_LONG).show();
                        MediaScannerConnection.scanFile(activity, new String[] { backupDB.getAbsolutePath() }, null, null);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity.getBaseContext(), "Problemas ao exportar banco! "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

*/
    }

    public static void escreverArquivo(String nomeArquivo, String texto) throws IOException {
        File sdCard = Environment.getExternalStorageDirectory();

        if(sdCard.canWrite()){
            File pastaAplicativo = new File(sdCard.getAbsolutePath()+"/mapa");
            pastaAplicativo.mkdirs();
            File arquivoDados = new File(pastaAplicativo, nomeArquivo);
            FileOutputStream fos = new FileOutputStream(arquivoDados);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.append(texto);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            fos.flush();
            fos.close();
        }

    }

}
