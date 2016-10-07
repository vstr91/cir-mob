package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;

/**
 * Created by Almir on 14/04/2014.
 */
public class HorarioItinerarioDBAdapter {

    private SQLiteDatabase database;
    private HorarioItinerarioDBHelper horarioItinerarioDBHelper;
    private String[] colunas = {horarioItinerarioDBHelper.ID, horarioItinerarioDBHelper.HORARIO, horarioItinerarioDBHelper.ITINERARIO,
            horarioItinerarioDBHelper.STATUS, horarioItinerarioDBHelper.OBS};
    private Context context;

    public HorarioItinerarioDBAdapter(Context context, SQLiteDatabase database){
        horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(HorarioItinerario horarioItinerario){
        Long retorno;
        ContentValues cv = new ContentValues();
        cv.put(horarioItinerarioDBHelper.ID, horarioItinerario.getId());
        cv.put(horarioItinerarioDBHelper.HORARIO, horarioItinerario.getHorario().getId());
        cv.put(horarioItinerarioDBHelper.ITINERARIO, horarioItinerario.getItinerario().getId());
        cv.put(horarioItinerarioDBHelper.DOMINGO, horarioItinerario.getDomingo());
        cv.put(horarioItinerarioDBHelper.SEGUNDA, horarioItinerario.getSegunda());
        cv.put(horarioItinerarioDBHelper.TERCA, horarioItinerario.getTerca());
        cv.put(horarioItinerarioDBHelper.QUARTA, horarioItinerario.getQuarta());
        cv.put(horarioItinerarioDBHelper.QUINTA, horarioItinerario.getQuinta());
        cv.put(horarioItinerarioDBHelper.SEXTA, horarioItinerario.getSexta());
        cv.put(horarioItinerarioDBHelper.SABADO, horarioItinerario.getSabado());
        cv.put(horarioItinerarioDBHelper.STATUS, horarioItinerario.getStatus());
        cv.put(horarioItinerarioDBHelper.OBS, horarioItinerario.getObs());

        if(database.update(HorarioItinerarioDBHelper.TABELA, cv,  horarioItinerarioDBHelper.ID+" = "+horarioItinerario.getId(), null) < 1){
            retorno = database.insert(HorarioItinerarioDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarInativos(){
        int retorno = database.delete(HorarioItinerarioDBHelper.TABELA, horarioItinerarioDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<HorarioItinerario> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, id_horario, id_itinerario, " +
                "domingo, segunda, terca, quarta, quinta, sexta, sabado, status, obs FROM "+horarioItinerarioDBHelper.TABELA, null);
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        List<HorarioItinerario> horariosItinerarios = new ArrayList<HorarioItinerario>();

        if(cursor.moveToFirst()){
            do{
                HorarioItinerario umHorarioItinerario = new HorarioItinerario();
                umHorarioItinerario.setId(cursor.getInt(0));

                Horario umHorario = new Horario();
                umHorario.setId(cursor.getInt(1));
                umHorario = horarioDBHelper.carregar(context, umHorario);

                umHorarioItinerario.setHorario(umHorario);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umHorarioItinerario.setItinerario(umItinerario);

                umHorarioItinerario.setDomingo(cursor.getInt(3));
                umHorarioItinerario.setSegunda(cursor.getInt(4));
                umHorarioItinerario.setTerca(cursor.getInt(5));
                umHorarioItinerario.setQuarta(cursor.getInt(6));
                umHorarioItinerario.setQuinta(cursor.getInt(7));
                umHorarioItinerario.setSexta(cursor.getInt(8));
                umHorarioItinerario.setSabado(cursor.getInt(9));

                umHorarioItinerario.setStatus(cursor.getInt(10));
                umHorarioItinerario.setObs(cursor.getString(11));

               horariosItinerarios.add(umHorarioItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return horariosItinerarios;
    }

    public HorarioItinerario carregar(HorarioItinerario horarioItinerario){

        Cursor cursor;

        if(horarioItinerario.getId() > 0){
            cursor = database.rawQuery("SELECT _id, id_horario, id_itinerario, " +
                    "domingo, segunda, terca, quarta, quinta, sexta, sabado, status, obs FROM "+horarioItinerarioDBHelper.TABELA
                    +" WHERE _id = ?", new String[]{String.valueOf(horarioItinerario.getId())});
        } else{
            cursor = database.rawQuery("SELECT _id, id_horario, id_itinerario, " +
                    "domingo, segunda, terca, quarta, quinta, sexta, sabado, status, obs FROM "+horarioItinerarioDBHelper.TABELA
                    +" WHERE id_horario = ? AND id_itinerario = ? ",
                    new String[]{String.valueOf(horarioItinerario.getHorario().getId()),
                            String.valueOf(horarioItinerario.getItinerario().getId())});
        }


        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);

        HorarioItinerario umHorarioItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umHorarioItinerario = new HorarioItinerario();
                umHorarioItinerario.setId(cursor.getInt(0));

                Horario umHorario = new Horario();
                umHorario.setId(cursor.getInt(1));
                umHorario = horarioDBHelper.carregar(context, umHorario);

                umHorarioItinerario.setHorario(umHorario);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                umHorarioItinerario.setItinerario(umItinerario);

                umHorarioItinerario.setDomingo(cursor.getInt(3));
                umHorarioItinerario.setSegunda(cursor.getInt(4));
                umHorarioItinerario.setTerca(cursor.getInt(5));
                umHorarioItinerario.setQuarta(cursor.getInt(6));
                umHorarioItinerario.setQuinta(cursor.getInt(7));
                umHorarioItinerario.setSexta(cursor.getInt(8));
                umHorarioItinerario.setSabado(cursor.getInt(9));

                umHorarioItinerario.setStatus(cursor.getInt(10));
                umHorarioItinerario.setObs(cursor.getString(11));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umHorarioItinerario;
    }

    public List<HorarioItinerario> listarTodosHorariosPorItinerario(Bairro bairroPartida, Bairro bairroDestino){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        Cursor cursor = database.rawQuery("SELECT h._id, hi.id_horario, h.status, hi.domingo, hi.segunda, hi.terca, " +
                        "hi.quarta, hi.quinta, hi.sexta, hi.sabado, hi.obs FROM "+horarioItinerarioDBHelper.TABELA
                        +" hi INNER JOIN "+HorarioDBHelper.TABELA+" h ON hi.id_horario = h._id LEFT JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario WHERE i.id_partida = ? AND i.id_destino = ? " +
                        "ORDER BY TIME(h.nome)",
                new String[]{String.valueOf(bairroPartida.getId()), String.valueOf(bairroDestino.getId())});
        List<HorarioItinerario> horarios = new ArrayList<HorarioItinerario>();

        if(cursor.moveToFirst()){
            do{
                HorarioItinerario umHorarioItinerario = new HorarioItinerario();
                umHorarioItinerario.setId(cursor.getInt(0));
                Horario umHorario = new Horario();

                umHorario.setId(cursor.getInt(1));
                umHorario = horarioDBHelper.carregar(context, umHorario);
                umHorarioItinerario.setHorario(umHorario);

                umHorarioItinerario.setStatus(cursor.getInt(2));
                umHorarioItinerario.setDomingo(cursor.getInt(3));
                umHorarioItinerario.setSegunda(cursor.getInt(4));
                umHorarioItinerario.setTerca(cursor.getInt(5));
                umHorarioItinerario.setQuarta(cursor.getInt(6));
                umHorarioItinerario.setQuinta(cursor.getInt(7));
                umHorarioItinerario.setSexta(cursor.getInt(8));
                umHorarioItinerario.setSabado(cursor.getInt(9));
                umHorarioItinerario.setObs(cursor.getString(10));
                horarios.add(umHorarioItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return horarios;
    }

    public List<HorarioItinerario> listarTodosHorariosPorItinerario(Itinerario itinerario){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        Cursor cursor = database.rawQuery("SELECT hi._id, hi.id_horario, hi.status, hi.domingo, hi.segunda, hi.terca, " +
                        "hi.quarta, hi.quinta, hi.sexta, hi.sabado, hi.obs FROM "+horarioItinerarioDBHelper.TABELA
                        +" hi INNER JOIN "+HorarioDBHelper.TABELA+" h ON hi.id_horario = h._id LEFT JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario WHERE i._id = ?" +
                        "ORDER BY TIME(h.nome)",
                new String[]{String.valueOf(itinerario.getId())});
        List<HorarioItinerario> horarios = new ArrayList<HorarioItinerario>();

        if(cursor.moveToFirst()){
            do{
                HorarioItinerario umHorarioItinerario = new HorarioItinerario();
                umHorarioItinerario.setId(cursor.getInt(0));
                Horario umHorario = new Horario();

                umHorario.setId(cursor.getInt(1));
                umHorario = horarioDBHelper.carregar(context, umHorario);
                umHorarioItinerario.setHorario(umHorario);

                umHorarioItinerario.setStatus(cursor.getInt(2));
                umHorarioItinerario.setDomingo(cursor.getInt(3));
                umHorarioItinerario.setSegunda(cursor.getInt(4));
                umHorarioItinerario.setTerca(cursor.getInt(5));
                umHorarioItinerario.setQuarta(cursor.getInt(6));
                umHorarioItinerario.setQuinta(cursor.getInt(7));
                umHorarioItinerario.setSexta(cursor.getInt(8));
                umHorarioItinerario.setSabado(cursor.getInt(9));
                umHorarioItinerario.setObs(cursor.getString(10));
                horarios.add(umHorarioItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return horarios;
    }

    public HorarioItinerario listarProximoHorarioItinerario(Bairro partida, Bairro destino, String hora, int diaDaSemana){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        String diaAtual = "";

        if(diaDaSemana == -1){
            switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
                case Calendar.SUNDAY:
                    diaAtual = "domingo";
                    break;
                case Calendar.MONDAY:
                    diaAtual = "segunda";
                    break;
                case Calendar.TUESDAY:
                    diaAtual = "terca";
                    break;
                case Calendar.WEDNESDAY:
                    diaAtual = "quarta";
                    break;
                case Calendar.THURSDAY:
                    diaAtual = "quinta";
                    break;
                case Calendar.FRIDAY:
                    diaAtual = "sexta";
                    break;
                case Calendar.SATURDAY:
                    diaAtual = "sabado";
                    break;
            }
        } else{
            switch(diaDaSemana){
                case Calendar.SUNDAY:
                    diaAtual = "domingo";
                    break;
                case Calendar.MONDAY:
                    diaAtual = "segunda";
                    break;
                case Calendar.TUESDAY:
                    diaAtual = "terca";
                    break;
                case Calendar.WEDNESDAY:
                    diaAtual = "quarta";
                    break;
                case Calendar.THURSDAY:
                    diaAtual = "quinta";
                    break;
                case Calendar.FRIDAY:
                    diaAtual = "sexta";
                    break;
                case Calendar.SATURDAY:
                    diaAtual = "sabado";
                    break;
            }
        }

        /*Cursor cursor = database.rawQuery("SELECT hi._id, hi.id_horario, hi.id_itinerario, hi.status FROM "+HorarioItinerarioDBHelper.TABELA+" hi LEFT JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario LEFT JOIN "
                        +HorarioDBHelper.TABELA+" h ON h._id = hi.id_horario WHERE i.id_partida = ? AND i.id_destino = ? " +
                        "AND TIME(h.nome) > ? AND "+diaAtual+" = -1 ORDER BY  TIME(h.nome) LIMIT 1",
                new String[]{String.valueOf(partida.getId()), String.valueOf(destino.getId()), hora});*/

        Cursor cursor = database.rawQuery("SELECT hi._id, hi.id_horario, hi.id_itinerario, hi.status, pit.destaque, " +
                        "CASE " +
                        "  WHEN pit.destaque = -1 THEN IFNULL((SELECT i2.valor FROM itinerario i2 WHERE i2.id_partida = ? AND i2.id_destino = ?), pit.valor)" +
                        "  ELSE pit.valor " +
                        "END AS 'valor', hi.obs " +
                        "FROM horario_itinerario hi LEFT JOIN" +
                        "       itinerario i ON i._id = hi.id_itinerario LEFT JOIN" +
                        "       parada_itinerario pit ON pit.id_itinerario = i._id LEFT JOIN" +
                        "       parada p ON p._id = pit.id_parada LEFT JOIN" +
                        "       horario h ON h._id = hi.id_horario " +
                        "WHERE (i.id_partida = ? OR (p.id_bairro = ? AND pit.ordem > 1)) AND (i.id_destino = ? OR (p.id_bairro = ? AND pit.ordem > 1)) " +
                        "AND TIME(h.nome) > ? AND "+diaAtual+" = -1 ORDER BY  TIME(h.nome) LIMIT 1",
                new String[]{String.valueOf(partida.getId()), String.valueOf(destino.getId()), String.valueOf(partida.getId()), String.valueOf(partida.getId()), String.valueOf(destino.getId()), String.valueOf(destino.getId()), hora});

        HorarioItinerario umHorarioItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umHorarioItinerario = new HorarioItinerario();
                umHorarioItinerario.setId(cursor.getInt(0));

                Horario umHorario = new Horario();
                umHorario.setId(cursor.getInt(1));
                umHorario = horarioDBHelper.carregar(context, umHorario);
                umHorarioItinerario.setHorario(umHorario);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                if(cursor.getInt(4) == -1){
                    umItinerario.setValor(cursor.getDouble(5));
                    umHorarioItinerario.setTrecho(true);
                } else{
                    umHorarioItinerario.setTrecho(false);
                }

                umHorarioItinerario.setItinerario(umItinerario);

                umHorarioItinerario.setStatus(cursor.getInt(3));
                umHorarioItinerario.setObs(cursor.getString(6));
                //horarios.add(umHorario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umHorarioItinerario;
    }

    public HorarioItinerario listarPrimeiroHorarioItinerario(Bairro partida, Bairro destino, Calendar dia){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(context);
        String diaAtual = "";

        switch(dia.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                diaAtual = "domingo";
                break;
            case Calendar.MONDAY:
                diaAtual = "segunda";
                break;
            case Calendar.TUESDAY:
                diaAtual = "terca";
                break;
            case Calendar.WEDNESDAY:
                diaAtual = "quarta";
                break;
            case Calendar.THURSDAY:
                diaAtual = "quinta";
                break;
            case Calendar.FRIDAY:
                diaAtual = "sexta";
                break;
            case Calendar.SATURDAY:
                diaAtual = "sabado";
                break;
        }

        /*Cursor cursor = database.rawQuery("SELECT hi._id, hi.id_horario, hi.id_itinerario, hi.status FROM "+HorarioItinerarioDBHelper.TABELA+" hi LEFT JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario LEFT JOIN "
                        +HorarioDBHelper.TABELA+" h ON h._id = hi.id_horario WHERE id_partida = ? AND id_destino = ? AND "+diaAtual+" = -1 " +
                        "ORDER BY TIME(h.nome) LIMIT 1",
                new String[]{String.valueOf(partida.getId()), String.valueOf(destino.getId())});*/

        Cursor cursor = database.rawQuery("SELECT hi._id, hi.id_horario, hi.id_itinerario, hi.status, pit.destaque, pit.valor, hi.obs " +
                        "FROM horario_itinerario hi LEFT JOIN " +
                        "     itinerario i ON i._id = hi.id_itinerario LEFT JOIN " +
                        "     parada_itinerario pit ON pit.id_itinerario = i._id LEFT JOIN" +
                        "       parada p ON p._id = pit.id_parada LEFT JOIN" +
                        "     horario h ON h._id = hi.id_horario " +
                        "WHERE (i.id_partida = ? OR (p.id_bairro = ? AND pit.ordem > 1)) AND (i.id_destino = ? OR (p.id_bairro = ? AND pit.ordem > 1)) AND "+diaAtual+" = -1" +
                        "                        ORDER BY TIME(h.nome) LIMIT 1",
                new String[]{String.valueOf(partida.getId()), String.valueOf(partida.getId()), String.valueOf(destino.getId()), String.valueOf(destino.getId())});

        HorarioItinerario umHorarioItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umHorarioItinerario = new HorarioItinerario();
                umHorarioItinerario.setId(cursor.getInt(0));

                Horario umHorario = new Horario();
                umHorario.setId(cursor.getInt(1));
                umHorario = horarioDBHelper.carregar(context, umHorario);
                umHorarioItinerario.setHorario(umHorario);

                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(2));
                umItinerario = itinerarioDBHelper.carregar(context, umItinerario);

                if(cursor.getInt(4) == -1){
                    umItinerario.setValor(cursor.getDouble(5));
                    umHorarioItinerario.setTrecho(true);
                } else{
                    umHorarioItinerario.setTrecho(false);
                }

                umHorarioItinerario.setItinerario(umItinerario);

                umHorarioItinerario.setStatus(cursor.getInt(3));
                umHorarioItinerario.setObs(cursor.getString(6));
                //horarios.add(umHorario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umHorarioItinerario;
    }

}
