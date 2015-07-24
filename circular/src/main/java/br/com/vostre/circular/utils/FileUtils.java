package br.com.vostre.circular.utils;

import android.app.Activity;
import android.os.Environment;

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

    public static void exportDatabse(String databaseName, String exportedDatabase, Activity activity) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
