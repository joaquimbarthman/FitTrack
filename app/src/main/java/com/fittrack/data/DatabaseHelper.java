package com.fittrack.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.fittrack.model.Exercicio;
import com.fittrack.model.Objetivo;
import com.fittrack.model.Progresso;
import com.fittrack.model.Treino;
import com.fittrack.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fittrack.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABELA_USUARIO = "Usuario";
    private static final String TABELA_TREINO = "Treino";
    private static final String TABELA_EXERCICIO = "Exercicio";
    private static final String TABELA_OBJETIVO = "Objetivo";
    private static final String TABELA_PROGRESSO = "Progresso";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABELA_USUARIO + " (" +
                "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "senha TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABELA_TREINO + " (" +
                "idTreino INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nomeTreino TEXT NOT NULL," +
                "diaSemana TEXT NOT NULL," +
                "idUsuario INTEGER NOT NULL," +
                "FOREIGN KEY(idUsuario) REFERENCES " + TABELA_USUARIO + "(idUsuario) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + TABELA_EXERCICIO + " (" +
                "idExercicio INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nomeExercicio TEXT NOT NULL," +
                "series INTEGER NOT NULL," +
                "repeticoes INTEGER NOT NULL," +
                "peso REAL NOT NULL," +
                "idTreino INTEGER NOT NULL," +
                "FOREIGN KEY(idTreino) REFERENCES " + TABELA_TREINO + "(idTreino) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + TABELA_OBJETIVO + " (" +
                "idObjetivo INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tipoObjetivo TEXT NOT NULL," +
                "descricao TEXT," +
                "valorMeta TEXT," +
                "dataInicio TEXT NOT NULL," +
                "dataFim TEXT," +
                "status TEXT NOT NULL," +
                "idUsuario INTEGER NOT NULL," +
                "FOREIGN KEY(idUsuario) REFERENCES " + TABELA_USUARIO + "(idUsuario) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + TABELA_PROGRESSO + " (" +
                "idProgresso INTEGER PRIMARY KEY AUTOINCREMENT," +
                "dataRegistro TEXT NOT NULL," +
                "peso REAL," +
                "gordura REAL," +
                "cintura REAL," +
                "quadril REAL," +
                "peito REAL," +
                "notas TEXT," +
                "idUsuario INTEGER NOT NULL," +
                "FOREIGN KEY(idUsuario) REFERENCES " + TABELA_USUARIO + "(idUsuario) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_EXERCICIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_TREINO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OBJETIVO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_PROGRESSO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIO);
        onCreate(db);
    }

    public long inserirUsuario(Usuario usuario) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", usuario.getNome());
        values.put("email", usuario.getEmail());
        values.put("senha", usuario.getSenha());
        return db.insert(TABELA_USUARIO, null, values);
    }

    public boolean emailExiste(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT idUsuario FROM " + TABELA_USUARIO + " WHERE email = ?",
                new String[]{email}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    public Usuario autenticarUsuario(String email, String senha) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_USUARIO + " WHERE email = ? AND senha = ?",
                new String[]{email, senha}
        );
        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = cursorToUsuario(cursor);
        }
        cursor.close();
        return usuario;
    }

    public Usuario buscarUsuarioPorId(int idUsuario) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_USUARIO + " WHERE idUsuario = ?",
                new String[]{String.valueOf(idUsuario)}
        );
        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = cursorToUsuario(cursor);
        }
        cursor.close();
        return usuario;
    }

    public long inserirTreino(Treino treino) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomeTreino", treino.getNomeTreino());
        values.put("diaSemana", treino.getDiaSemana());
        values.put("idUsuario", treino.getIdUsuario());
        return db.insert(TABELA_TREINO, null, values);
    }

    public int atualizarTreino(Treino treino) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomeTreino", treino.getNomeTreino());
        values.put("diaSemana", treino.getDiaSemana());
        return db.update(TABELA_TREINO, values, "idTreino = ?", new String[]{String.valueOf(treino.getIdTreino())});
    }

    public int excluirTreino(int idTreino) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABELA_TREINO, "idTreino = ?", new String[]{String.valueOf(idTreino)});
    }

    public Treino buscarTreinoPorId(int idTreino) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_TREINO + " WHERE idTreino = ?",
                new String[]{String.valueOf(idTreino)}
        );
        Treino treino = null;
        if (cursor.moveToFirst()) {
            treino = cursorToTreino(cursor);
        }
        cursor.close();
        return treino;
    }

    public List<Treino> listarTreinosPorUsuario(int idUsuario) {
        List<Treino> treinos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_TREINO + " WHERE idUsuario = ? ORDER BY diaSemana, nomeTreino",
                new String[]{String.valueOf(idUsuario)}
        );
        while (cursor.moveToNext()) {
            treinos.add(cursorToTreino(cursor));
        }
        cursor.close();
        return treinos;
    }

    public long inserirExercicio(Exercicio exercicio) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomeExercicio", exercicio.getNomeExercicio());
        values.put("series", exercicio.getSeries());
        values.put("repeticoes", exercicio.getRepeticoes());
        values.put("peso", exercicio.getPeso());
        values.put("idTreino", exercicio.getIdTreino());
        return db.insert(TABELA_EXERCICIO, null, values);
    }

    public int atualizarExercicio(Exercicio exercicio) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomeExercicio", exercicio.getNomeExercicio());
        values.put("series", exercicio.getSeries());
        values.put("repeticoes", exercicio.getRepeticoes());
        values.put("peso", exercicio.getPeso());
        return db.update(TABELA_EXERCICIO, values, "idExercicio = ?", new String[]{String.valueOf(exercicio.getIdExercicio())});
    }

    public int excluirExercicio(int idExercicio) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABELA_EXERCICIO, "idExercicio = ?", new String[]{String.valueOf(idExercicio)});
    }

    public Exercicio buscarExercicioPorId(int idExercicio) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_EXERCICIO + " WHERE idExercicio = ?",
                new String[]{String.valueOf(idExercicio)}
        );
        Exercicio exercicio = null;
        if (cursor.moveToFirst()) {
            exercicio = cursorToExercicio(cursor);
        }
        cursor.close();
        return exercicio;
    }

    public List<Exercicio> listarExerciciosPorTreino(int idTreino) {
        List<Exercicio> exercicios = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_EXERCICIO + " WHERE idTreino = ? ORDER BY nomeExercicio",
                new String[]{String.valueOf(idTreino)}
        );
        while (cursor.moveToNext()) {
            exercicios.add(cursorToExercicio(cursor));
        }
        cursor.close();
        return exercicios;
    }

    public long inserirObjetivo(Objetivo objetivo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tipoObjetivo", objetivo.getTipoObjetivo());
        values.put("descricao", objetivo.getDescricao());
        values.put("valorMeta", objetivo.getValorMeta());
        values.put("dataInicio", objetivo.getDataInicio());
        values.put("dataFim", objetivo.getDataFim());
        values.put("status", objetivo.getStatus());
        values.put("idUsuario", objetivo.getIdUsuario());
        return db.insert(TABELA_OBJETIVO, null, values);
    }

    public int atualizarObjetivo(Objetivo objetivo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tipoObjetivo", objetivo.getTipoObjetivo());
        values.put("descricao", objetivo.getDescricao());
        values.put("valorMeta", objetivo.getValorMeta());
        values.put("dataInicio", objetivo.getDataInicio());
        values.put("dataFim", objetivo.getDataFim());
        values.put("status", objetivo.getStatus());
        return db.update(TABELA_OBJETIVO, values, "idObjetivo = ?", new String[]{String.valueOf(objetivo.getIdObjetivo())});
    }

    public int excluirObjetivo(int idObjetivo) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABELA_OBJETIVO, "idObjetivo = ?", new String[]{String.valueOf(idObjetivo)});
    }

    public Objetivo buscarObjetivoPorId(int idObjetivo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_OBJETIVO + " WHERE idObjetivo = ?",
                new String[]{String.valueOf(idObjetivo)}
        );
        Objetivo objetivo = null;
        if (cursor.moveToFirst()) {
            objetivo = cursorToObjetivo(cursor);
        }
        cursor.close();
        return objetivo;
    }

    public List<Objetivo> listarObjetivosPorUsuario(int idUsuario) {
        List<Objetivo> objetivos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_OBJETIVO + " WHERE idUsuario = ? ORDER BY dataInicio",
                new String[]{String.valueOf(idUsuario)}
        );
        while (cursor.moveToNext()) {
            objetivos.add(cursorToObjetivo(cursor));
        }
        cursor.close();
        return objetivos;
    }

    public long inserirProgresso(Progresso progresso) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dataRegistro", progresso.getDataRegistro());
        values.put("peso", progresso.getPeso());
        values.put("gordura", progresso.getGordura());
        values.put("cintura", progresso.getCintura());
        values.put("quadril", progresso.getQuadril());
        values.put("peito", progresso.getPeito());
        values.put("notas", progresso.getNotas());
        values.put("idUsuario", progresso.getIdUsuario());
        return db.insert(TABELA_PROGRESSO, null, values);
    }

    public int atualizarProgresso(Progresso progresso) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dataRegistro", progresso.getDataRegistro());
        values.put("peso", progresso.getPeso());
        values.put("gordura", progresso.getGordura());
        values.put("cintura", progresso.getCintura());
        values.put("quadril", progresso.getQuadril());
        values.put("peito", progresso.getPeito());
        values.put("notas", progresso.getNotas());
        return db.update(TABELA_PROGRESSO, values, "idProgresso = ?", new String[]{String.valueOf(progresso.getIdProgresso())});
    }

    public int excluirProgresso(int idProgresso) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABELA_PROGRESSO, "idProgresso = ?", new String[]{String.valueOf(idProgresso)});
    }

    public Progresso buscarProgressoPorId(int idProgresso) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_PROGRESSO + " WHERE idProgresso = ?",
                new String[]{String.valueOf(idProgresso)}
        );
        Progresso progresso = null;
        if (cursor.moveToFirst()) {
            progresso = cursorToProgresso(cursor);
        }
        cursor.close();
        return progresso;
    }

    public List<Progresso> listarProgressoPorUsuario(int idUsuario) {
        List<Progresso> progressoList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_PROGRESSO + " WHERE idUsuario = ? ORDER BY dataRegistro DESC",
                new String[]{String.valueOf(idUsuario)}
        );
        while (cursor.moveToNext()) {
            progressoList.add(cursorToProgresso(cursor));
        }
        cursor.close();
        return progressoList;
    }

    private Objetivo cursorToObjetivo(Cursor cursor) {
        return new Objetivo(
                cursor.getInt(cursor.getColumnIndexOrThrow("idObjetivo")),
                cursor.getString(cursor.getColumnIndexOrThrow("tipoObjetivo")),
                cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                cursor.getString(cursor.getColumnIndexOrThrow("valorMeta")),
                cursor.getString(cursor.getColumnIndexOrThrow("dataInicio")),
                cursor.getString(cursor.getColumnIndexOrThrow("dataFim")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario"))
        );
    }

    private Progresso cursorToProgresso(Cursor cursor) {
        return new Progresso(
                cursor.getInt(cursor.getColumnIndexOrThrow("idProgresso")),
                cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario")),
                cursor.getString(cursor.getColumnIndexOrThrow("dataRegistro")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("peso")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("gordura")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("cintura")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("quadril")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("peito")),
                cursor.getString(cursor.getColumnIndexOrThrow("notas"))
        );
    }

    private Usuario cursorToUsuario(Cursor cursor) {
        return new Usuario(
                cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario")),
                cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("senha"))
        );
    }

    private Treino cursorToTreino(Cursor cursor) {
        return new Treino(
                cursor.getInt(cursor.getColumnIndexOrThrow("idTreino")),
                cursor.getString(cursor.getColumnIndexOrThrow("nomeTreino")),
                cursor.getString(cursor.getColumnIndexOrThrow("diaSemana")),
                cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario"))
        );
    }

    private Exercicio cursorToExercicio(Cursor cursor) {
        return new Exercicio(
                cursor.getInt(cursor.getColumnIndexOrThrow("idExercicio")),
                cursor.getString(cursor.getColumnIndexOrThrow("nomeExercicio")),
                cursor.getInt(cursor.getColumnIndexOrThrow("series")),
                cursor.getInt(cursor.getColumnIndexOrThrow("repeticoes")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("peso")),
                cursor.getInt(cursor.getColumnIndexOrThrow("idTreino"))
        );
    }
}
