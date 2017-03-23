package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;

/**
 * Created by Almir on 14/04/2014.
 */
public class ItinerarioDBAdapter {

    private SQLiteDatabase database;
    private ItinerarioDBHelper itinerarioDBHelper;
    private Context context;

    public ItinerarioDBAdapter(Context context, SQLiteDatabase database){
        itinerarioDBHelper = new ItinerarioDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(Itinerario itinerario){
        Long retorno;
        ContentValues cv = new ContentValues();
        cv.put(itinerarioDBHelper.ID, itinerario.getId());
        cv.put(itinerarioDBHelper.PARTIDA, itinerario.getPartida().getId());
        cv.put(itinerarioDBHelper.DESTINO, itinerario.getDestino().getId());
        cv.put(itinerarioDBHelper.VALOR, itinerario.getValor());
        cv.put(itinerarioDBHelper.STATUS, itinerario.getStatus());
        cv.put(itinerarioDBHelper.EMPRESA, itinerario.getEmpresa().getId());
        cv.put(itinerarioDBHelper.OBSERVACAO, itinerario.getObservacao());

        if(database.update(ItinerarioDBHelper.TABELA, cv,  itinerarioDBHelper.ID+" = "+itinerario.getId(), null) < 1){
            retorno = database.insert(ItinerarioDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletarInativos(){
        int retorno = database.delete(ItinerarioDBHelper.TABELA, itinerarioDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<Itinerario> listarTodos(){
        Cursor cursor = database.rawQuery("SELECT _id, id_partida, id_destino, valor, status, id_empresa, observacao FROM "+itinerarioDBHelper.TABELA, null);
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);
        List<Itinerario> itinerarios = new ArrayList<Itinerario>();

        if(cursor.moveToFirst()){
            do{
                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(0));

                Bairro bairroPartida = new Bairro();
                bairroPartida.setId(cursor.getInt(1));
                bairroPartida = bairroDBHelper.carregar(context, bairroPartida);

                umItinerario.setPartida(bairroPartida);

                Bairro bairroDestino = new Bairro();
                bairroDestino.setId(cursor.getInt(2));
                bairroDestino = bairroDBHelper.carregar(context, bairroDestino);

                umItinerario.setDestino(bairroDestino);

                umItinerario.setValor(cursor.getFloat(3));
                umItinerario.setStatus(cursor.getInt(4));

                Empresa empresa = new Empresa();
                empresa.setId(cursor.getInt(5));

                empresa = empresaDBHelper.carregar(context, empresa);
                umItinerario.setEmpresa(empresa);

                umItinerario.setObservacao(cursor.getString(6));

                itinerarios.add(umItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return itinerarios;
    }

    public List<Itinerario> listarTodosPorPartida(Itinerario itinerario){
        Cursor cursor = database.rawQuery("SELECT _id, id_partida, id_destino, valor, status, id_empresa, observacao FROM "+itinerarioDBHelper.TABELA
                +"WHERE id_partida = ?", new String[]{String.valueOf(itinerario.getPartida().getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);
        List<Itinerario> itinerarios = new ArrayList<Itinerario>();

        if(cursor.moveToFirst()){
            do{
                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(0));

                Bairro bairroPartida = new Bairro();
                bairroPartida.setId(cursor.getInt(1));
                bairroPartida = bairroDBHelper.carregar(context, bairroPartida);

                umItinerario.setPartida(bairroPartida);

                Bairro bairroDestino = new Bairro();
                bairroDestino.setId(cursor.getInt(2));
                bairroDestino = bairroDBHelper.carregar(context, bairroDestino);

                umItinerario.setDestino(bairroDestino);

                umItinerario.setValor(cursor.getFloat(3));
                umItinerario.setStatus(cursor.getInt(4));

                Empresa empresa = new Empresa();
                empresa.setId(cursor.getInt(5));

                empresa = empresaDBHelper.carregar(context, empresa);
                umItinerario.setEmpresa(empresa);

                umItinerario.setObservacao(cursor.getString(6));

                itinerarios.add(umItinerario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return itinerarios;
    }

    public Itinerario carregar(Itinerario itinerario){
        Cursor cursor = database.rawQuery("SELECT _id, id_partida, id_destino, valor, status, id_empresa, observacao FROM "+itinerarioDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(itinerario.getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        Itinerario umItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(0));

                Bairro bairroPartida = new Bairro();
                bairroPartida.setId(cursor.getInt(1));
                bairroPartida = bairroDBHelper.carregar(context, bairroPartida);

                umItinerario.setPartida(bairroPartida);

                Bairro bairroDestino = new Bairro();
                bairroDestino.setId(cursor.getInt(2));
                bairroDestino = bairroDBHelper.carregar(context, bairroDestino);

                umItinerario.setDestino(bairroDestino);

                umItinerario.setValor(cursor.getFloat(3));
                umItinerario.setStatus(cursor.getInt(4));

                Empresa empresa = new Empresa();
                empresa.setId(cursor.getInt(5));

                empresa = empresaDBHelper.carregar(context, empresa);
                umItinerario.setEmpresa(empresa);

                umItinerario.setObservacao(cursor.getString(6));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umItinerario;
    }

    public Itinerario carregarPorPartidaEDestino(Itinerario itinerario){
        Cursor cursor = database.rawQuery("SELECT _id, id_partida, id_destino, valor, status, id_empresa, observacao FROM "+itinerarioDBHelper.TABELA
                        +" WHERE id_partida = ? AND id_destino = ?",
                new String[]{String.valueOf(itinerario.getPartida().getId()), String.valueOf(itinerario.getDestino().getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        Itinerario umItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(0));

                Bairro bairroPartida = new Bairro();
                bairroPartida.setId(cursor.getInt(1));
                bairroPartida = bairroDBHelper.carregar(context, bairroPartida);

                umItinerario.setPartida(bairroPartida);

                Bairro bairroDestino = new Bairro();
                bairroDestino.setId(cursor.getInt(2));
                bairroDestino = bairroDBHelper.carregar(context, bairroDestino);

                umItinerario.setDestino(bairroDestino);

                umItinerario.setValor(cursor.getFloat(3));
                umItinerario.setStatus(cursor.getInt(4));

                Empresa empresa = new Empresa();
                empresa.setId(cursor.getInt(5));

                empresa = empresaDBHelper.carregar(context, empresa);
                umItinerario.setEmpresa(empresa);

                umItinerario.setObservacao(cursor.getString(6));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umItinerario;
    }

    public List<HorarioItinerario> listarTodosPorParada(Parada parada, String hora){
        //Cursor cursor = database.rawQuery("SELECT i._id, i.id_partida, i.id_destino, i.valor, i.status FROM "+ ParadaItinerarioDBHelper.TABELA
        //        +" pit LEFT JOIN "+ParadaDBHelper.TABELA+" p ON p._id = pit.id_parada LEFT JOIN "+
        //        itinerarioDBHelper.TABELA+" i ON i._id = pit.id_itinerario WHERE p._id = ?", new String[]{String.valueOf(parada.getId())});

        String diaAtual = "";
        String diaSeguinte = "";

        switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                diaAtual = "domingo";
                diaSeguinte = "segunda";
                break;
            case Calendar.MONDAY:
                diaAtual = "segunda";
                diaSeguinte = "terca";
                break;
            case Calendar.TUESDAY:
                diaAtual = "terca";
                diaSeguinte = "quarta";
                break;
            case Calendar.WEDNESDAY:
                diaAtual = "quarta";
                diaSeguinte = "quinta";
                break;
            case Calendar.THURSDAY:
                diaAtual = "quinta";
                diaSeguinte = "sexta";
                break;
            case Calendar.FRIDAY:
                diaAtual = "sexta";
                diaSeguinte = "sabado";
                break;
            case Calendar.SATURDAY:
                diaAtual = "sabado";
                diaSeguinte = "domingo";
                break;
        }

        /*String query = "SELECT i._id, i.id_partida, i.id_destino, i.valor, i.status," +
                "                IFNULL(( " +
                "                SELECT h._id" +
                "                FROM horario_itinerario hi LEFT JOIN" +
                "                     horario h ON h._id = hi.id_horario" +
                "                WHERE hi.id_itinerario = i._id" +
                "                AND TIME(h.nome) > ? AND "+diaAtual+" = -1" +
                "                LIMIT 1" +
                "                )," +
                "                (" +
                "                SELECT h._id" +
                "                FROM horario_itinerario hi LEFT JOIN" +
                "                horario h ON h._id = hi.id_horario" +
                "                WHERE hi.id_itinerario = i._id AND "+diaSeguinte+" = -1" +
                "                ORDER BY h._id LIMIT 1" +
                "                )" +
                "                ) AS 'id_proximo_horario', i.id_empresa, i.observacao" +
                "                FROM parada_itinerario pit LEFT JOIN parada p ON p._id = pit.id_parada LEFT JOIN" +
                "                itinerario i ON i._id = pit.id_itinerario WHERE p._id = ? AND p._id" +
                "                NOT IN ( SELECT pit2.id_parada FROM parada_itinerario pit2 WHERE pit2.id_itinerario = i._id" +
                "                ORDER BY ordem DESC LIMIT 1 )" +
                "ORDER BY " +
                "    IFNULL(( " +
                "                SELECT 0" +
                "                FROM horario_itinerario hi LEFT JOIN" +
                "                     horario h ON h._id = hi.id_horario" +
                "                WHERE hi.id_itinerario = i._id" +
                "                AND TIME(h.nome) > ? AND "+diaAtual+" = -1" +
                "                LIMIT 1" +
                "                )," +
                "                1" +
                "                ), h._id";*/

        String query = "SELECT i._id, i.id_partida, i.id_destino, i.valor, i.status," +
                "       IFNULL(" +
                "   ( " +
                "                SELECT hi.id_horario" +
                "                FROM horario_itinerario hi INNER JOIN" +
                "                     horario h ON h._id = hi.id_horario" +
                "                WHERE hi.id_itinerario = i._id AND " +
                "                      TIME(h.nome) > ? AND " +
                "                      "+diaAtual+" = -1" +
                "                ORDER BY h._id LIMIT 1" +
                "                )," +
                "                (" +
                "                 SELECT hi.id_horario" +
                "                 FROM horario_itinerario hi INNER JOIN" +
                "                      horario h ON h._id = hi.id_horario" +
                "                 WHERE hi.id_itinerario = i._id AND "+diaSeguinte+" = -1" +
                "                 ORDER BY h._id LIMIT 1" +
                "                )" +
                "              ) AS 'id_proximo_horario', i.id_empresa, i.observacao" +
                "                                FROM parada_itinerario pit INNER JOIN parada p ON p._id = pit.id_parada LEFT JOIN" +
                "                                itinerario i ON i._id = pit.id_itinerario WHERE p._id = ? AND p._id" +
                "                                NOT IN ( SELECT pit2.id_parada FROM parada_itinerario pit2 WHERE pit2.id_itinerario = i._id" +
                "                                ORDER BY ordem DESC LIMIT 1 ) AND id_proximo_horario != '' " +
                "                ORDER BY " +
                "                    IFNULL(( " +
                "                                SELECT 0" +
                "                                FROM horario_itinerario hi LEFT JOIN" +
                "                                     horario h ON h._id = hi.id_horario" +
                "                                WHERE hi.id_itinerario = i._id" +
                "                                AND TIME(h.nome) > ? AND "+diaAtual+" = -1" +
                "                                LIMIT 1" +
                "                                )," +
                "                                1" +
                "                                ), id_proximo_horario";

        Cursor cursor = database.rawQuery(query, new String[]{hora, String.valueOf(parada.getId()), hora});

        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        HorarioItinerarioDBHelper hiDBHelper = new HorarioItinerarioDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);
        List<HorarioItinerario> itinerarios = new ArrayList<HorarioItinerario>();

        if(cursor.moveToFirst()){
            do{

                // PROVISORIO - Filtrando apenas itinerários que possuem horário cadastrado -  ## passar filtro para consulta do banco ##
                //if(cursor.getInt(5) > 0){
                Itinerario umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(0));

                Bairro bairroPartida = new Bairro();
                bairroPartida.setId(cursor.getInt(1));
                bairroPartida = bairroDBHelper.carregar(context, bairroPartida);

                umItinerario.setPartida(bairroPartida);

                Bairro bairroDestino = new Bairro();
                bairroDestino.setId(cursor.getInt(2));
                bairroDestino = bairroDBHelper.carregar(context, bairroDestino);

                umItinerario.setDestino(bairroDestino);

                umItinerario.setValor(cursor.getFloat(3));
                umItinerario.setStatus(cursor.getInt(4));

                Horario horario = new Horario();
                horario.setId(cursor.getInt(5));

                horario = horarioDBHelper.carregar(context, horario);

                Empresa empresa = new Empresa();
                empresa.setId(cursor.getInt(6));

                empresa = empresaDBHelper.carregar(context, empresa);
                umItinerario.setEmpresa(empresa);

                umItinerario.setObservacao(cursor.getString(7));

                //horario = horario == null ? new Horario() : horario;

                HorarioItinerario umHorarioItinerario = new HorarioItinerario();
                umHorarioItinerario.setHorario(horario);
                umHorarioItinerario.setItinerario(umItinerario);

                umHorarioItinerario = hiDBHelper.carregar(context, umHorarioItinerario);

                itinerarios.add(umHorarioItinerario);
                //}

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return itinerarios;
    }

    public List<HorarioItinerario> listarOutrasOpcoesItinerario(Itinerario itinerario, String hora, int diaDaSemana){
        //Cursor cursor = database.rawQuery("SELECT i._id, i.id_partida, i.id_destino, i.valor, i.status FROM "+ ParadaItinerarioDBHelper.TABELA
        //        +" pit LEFT JOIN "+ParadaDBHelper.TABELA+" p ON p._id = pit.id_parada LEFT JOIN "+
        //        itinerarioDBHelper.TABELA+" i ON i._id = pit.id_itinerario WHERE p._id = ?", new String[]{String.valueOf(parada.getId())});

        String diaAtual = "";
        String diaSeguinte = "";

        if(diaDaSemana == -1){
            diaDaSemana = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        }



        switch(diaDaSemana){
            case Calendar.SUNDAY:
                diaAtual = "domingo";
                diaSeguinte = "segunda";
                break;
            case Calendar.MONDAY:
                diaAtual = "segunda";
                diaSeguinte = "terca";
                break;
            case Calendar.TUESDAY:
                diaAtual = "terca";
                diaSeguinte = "quarta";
                break;
            case Calendar.WEDNESDAY:
                diaAtual = "quarta";
                diaSeguinte = "quinta";
                break;
            case Calendar.THURSDAY:
                diaAtual = "quinta";
                diaSeguinte = "sexta";
                break;
            case Calendar.FRIDAY:
                diaAtual = "sexta";
                diaSeguinte = "sabado";
                break;
            case Calendar.SATURDAY:
                diaAtual = "sabado";
                diaSeguinte = "domingo";
                break;
        }

        /*String query = "SELECT i._id, i.id_partida, i.id_destino, i.valor, i.status," +
                "                IFNULL(( " +
                "                SELECT h._id" +
                "                FROM horario_itinerario hi LEFT JOIN" +
                "                     horario h ON h._id = hi.id_horario" +
                "                WHERE hi.id_itinerario = i._id" +
                "                AND TIME(h.nome) > ?" +
                "                LIMIT 1" +
                "                )," +
                "                (" +
                "                SELECT h._id" +
                "                FROM horario_itinerario hi LEFT JOIN" +
                "                horario h ON h._id = hi.id_horario" +
                "                WHERE hi.id_itinerario = i._id" +
                "                LIMIT 1" +
                "                )" +
                "                ) AS 'id_proximo_horario', i.id_empresa, i.observacao" +
                "                FROM itinerario i WHERE i.id_partida = ? AND i.id_destino = ? AND i._id <> ?" +
                "ORDER BY " +
                "    IFNULL(( " +
                "                SELECT h._id" +
                "                FROM horario_itinerario hi LEFT JOIN" +
                "                     horario h ON h._id = hi.id_horario" +
                "                WHERE hi.id_itinerario = i._id" +
                "                AND TIME(h.nome) > ?" +
                "                LIMIT 1" +
                "                )," +
                "                -1000" +
                "                )";*/

        String query = "SELECT DISTINCT i._id, i.id_partida, i.id_destino, i.valor, i.status," +
                "       IFNULL(" +
                "    ( " +
                "                 SELECT h._id" +
                "                 FROM horario_itinerario hi LEFT JOIN" +
                "                      horario h ON h._id = hi.id_horario" +
                "                 WHERE hi.id_itinerario = i._id AND " +
                "           TIME(h.nome) > ? AND " +
                diaAtual+" = -1" +
                "                 LIMIT 1" +
                "                )," +
                "                (" +
                "                  SELECT h._id" +
                "                  FROM horario_itinerario hi LEFT JOIN" +
                "                       horario h ON h._id = hi.id_horario" +
                "                  WHERE hi.id_itinerario = i._id" +
                "                  AND "+diaSeguinte+" = -1" +
                "                  ORDER BY h._id" +
                "                  LIMIT 1" +
                "                 )" +
                "            ) AS 'id_proximo_horario', i.id_empresa, i.observacao" +
                "            FROM itinerario i LEFT JOIN" +
                "                 parada_itinerario pit ON pit.id_itinerario = i._id LEFT JOIN" +
                "                 parada p ON p._id = pit.id_parada " +
                "WHERE (i.id_partida = ? OR (p.id_bairro = ? AND pit.ordem > 1)) AND " +
                "                 (i.id_destino = ? OR (p.id_bairro = ? AND pit.ordem > 1)) AND " +
                "                 i._id <> ?" +
                "           ORDER BY" +
                "                 IFNULL(" +
                "                          (" +
                "                           SELECT h._id" +
                "                           FROM horario_itinerario hi LEFT JOIN" +
                "                                horario h ON h._id = hi.id_horario" +
                "                           WHERE hi.id_itinerario = i._id" +
                "                           AND TIME(h.nome) > ?" +
                "    AND "+diaAtual+" = -1" +
                "                           LIMIT 1" +
                "                          )," +
                "                          -1000" +
                "                     )";

        Cursor cursor = database.rawQuery(query, new String[]{hora, String.valueOf(itinerario.getPartida().getId()),
                String.valueOf(itinerario.getPartida().getId()),
                String.valueOf(itinerario.getDestino().getId()), String.valueOf(itinerario.getDestino().getId()),
                String.valueOf(itinerario.getId()), hora});

        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);
        List<HorarioItinerario> itinerarios = new ArrayList<HorarioItinerario>();

        if(cursor.moveToFirst()){
            do{

                // PROVISORIO - Filtrando apenas itinerários que possuem horário cadastrado -  ## passar filtro para consulta do banco ##
                if(cursor.getInt(5) > 0){
                    Itinerario umItinerario = new Itinerario();
                    umItinerario.setId(cursor.getInt(0));

                    Bairro bairroPartida = new Bairro();
                    bairroPartida.setId(cursor.getInt(1));
                    bairroPartida = bairroDBHelper.carregar(context, bairroPartida);

                    umItinerario.setPartida(bairroPartida);

                    Bairro bairroDestino = new Bairro();
                    bairroDestino.setId(cursor.getInt(2));
                    bairroDestino = bairroDBHelper.carregar(context, bairroDestino);

                    umItinerario.setDestino(bairroDestino);

                    umItinerario.setValor(cursor.getFloat(3));
                    umItinerario.setStatus(cursor.getInt(4));

                    Horario horario = new Horario();
                    horario.setId(cursor.getInt(5));

                    horario = horarioDBHelper.carregar(context, horario);

                    Empresa empresa = new Empresa();
                    empresa.setId(cursor.getInt(6));

                    empresa = empresaDBHelper.carregar(context, empresa);
                    umItinerario.setEmpresa(empresa);

                    umItinerario.setObservacao(cursor.getString(7));

                    //horario = horario == null ? new Horario() : horario;

                    HorarioItinerario umHorarioItinerario = new HorarioItinerario();
                    umHorarioItinerario.setHorario(horario);
                    umHorarioItinerario.setItinerario(umItinerario);

                    itinerarios.add(umHorarioItinerario);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return itinerarios;
    }

    public Itinerario checarReverso(Bairro partida, Bairro destino){
        Cursor cursor = database.rawQuery("SELECT _id, id_partida, id_destino, valor, status, id_empresa, observacao FROM "+itinerarioDBHelper.TABELA
                +" WHERE id_partida = ? AND id_destino = ?", new String[]{String.valueOf(destino.getId()), String.valueOf(partida.getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        Itinerario umItinerario = null;

        if(cursor.moveToFirst()){
            do{
                umItinerario = new Itinerario();
                umItinerario.setId(cursor.getInt(0));

                Bairro bairroPartida = new Bairro();
                bairroPartida.setId(cursor.getInt(1));
                bairroPartida = bairroDBHelper.carregar(context, bairroPartida);

                umItinerario.setPartida(bairroPartida);

                Bairro bairroDestino = new Bairro();
                bairroDestino.setId(cursor.getInt(2));
                bairroDestino = bairroDBHelper.carregar(context, bairroDestino);

                umItinerario.setDestino(bairroDestino);

                umItinerario.setValor(cursor.getFloat(3));
                umItinerario.setStatus(cursor.getInt(4));

                Empresa empresa = new Empresa();
                empresa.setId(cursor.getInt(5));

                empresa = empresaDBHelper.carregar(context, empresa);
                umItinerario.setEmpresa(empresa);

                umItinerario.setObservacao(cursor.getString(6));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umItinerario;
    }

    public double listarValorTrecho(HorarioItinerario horarioItinerario){
        Cursor cursor = database.rawQuery("SELECT pit.valor FROM "+ParadaItinerarioDBHelper.TABELA+" pit INNER JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = pit.id_itinerario INNER JOIN "
                        +ParadaDBHelper.TABELA+" p ON p._id = pit.id_parada"
                        +" WHERE destaque = -1 AND i._id = ? AND p.id_bairro = ?",
                new String[]{String.valueOf(horarioItinerario.getItinerario().getId()),
                        String.valueOf(horarioItinerario.getItinerario().getDestino().getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        Double valor = null;

        if(cursor.moveToFirst()){
            do{
                valor = cursor.getDouble(0);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return valor;
    }

    public double listarValorTrecho(HorarioItinerario horarioItinerario, Itinerario itinerarioTrecho){
        Cursor cursor = database.rawQuery("SELECT pit.valor FROM "+ParadaItinerarioDBHelper.TABELA+" pit INNER JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = pit.id_itinerario INNER JOIN "
                        +ParadaDBHelper.TABELA+" p ON p._id = pit.id_parada"
                        +" WHERE destaque = -1 AND i._id = ? AND p.id_bairro = ?",
                new String[]{String.valueOf(horarioItinerario.getItinerario().getId()),
                        String.valueOf(itinerarioTrecho.getDestino().getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        Double valor = null;

        if(cursor.moveToFirst()){
            do{
                valor = cursor.getDouble(0);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return valor;
    }

    public double listarValorTrechoInvertido(Itinerario itinerario){
        Cursor cursor = database.rawQuery("SELECT pit.valor FROM "+ParadaItinerarioDBHelper.TABELA+" pit INNER JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = pit.id_itinerario INNER JOIN "
                        +ParadaDBHelper.TABELA+" p ON p._id = pit.id_parada"
                        +" WHERE destaque = -1 AND i.id_partida = ? AND p.id_bairro = ?",
                new String[]{String.valueOf(itinerario.getDestino().getId()),
                        String.valueOf(itinerario.getPartida().getId())});
        BairroDBHelper bairroDBHelper = new BairroDBHelper(context);
        EmpresaDBHelper empresaDBHelper = new EmpresaDBHelper(context);

        Double valor = null;

        if(cursor.moveToFirst()){
            do{
                valor = cursor.getDouble(0);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return valor;
    }

}