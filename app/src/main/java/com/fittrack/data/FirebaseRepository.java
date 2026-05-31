package com.fittrack.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fittrack.LoginActivity;
import com.fittrack.model.Exercicio;
import com.fittrack.model.Objetivo;
import com.fittrack.model.Progresso;
import com.fittrack.model.Treino;
import com.fittrack.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository {

    private static final String TAG = "FirebaseRepository";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_TREINOS = "treinos";
    private static final String COLLECTION_EXERCICIOS = "exercicios";
    private static final String COLLECTION_OBJETIVOS = "objetivos";
    private static final String COLLECTION_PROGRESSO = "progresso";

    private final FirebaseFirestore firestore;
    private final String userDocumentId;

    public interface ResultCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public FirebaseRepository(Context context) {
        FirebaseApp.initializeApp(context);
        firestore = FirebaseFirestore.getInstance();
        userDocumentId = getUserDocumentId(context);
    }

    private String getUserDocumentId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String email = preferences.getString(LoginActivity.KEY_USER_EMAIL, null);
        if (email == null) {
            return null;
        }
        return encodeEmail(email);
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    private boolean userPathAvailable() {
        return userDocumentId != null;
    }

    private DocumentReference document(String collection, String id) {
        return firestore.collection(COLLECTION_USERS)
                .document(userDocumentId)
                .collection(collection)
                .document(id);
    }

    private void saveData(String collection, String id, Map<String, Object> data, ResultCallback<Void> callback) {
        if (!userPathAvailable()) {
            callback.onFailure(new IllegalStateException("Usuário não autenticado para Firebase."));
            return;
        }
        document(collection, id)
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    private void deleteData(String collection, String id, ResultCallback<Void> callback) {
        if (!userPathAvailable()) {
            callback.onFailure(new IllegalStateException("Usuário não autenticado para Firebase."));
            return;
        }
        document(collection, id)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    private void listData(String collection, ResultCallback<List<Map<String, Object>>> callback) {
        if (!userPathAvailable()) {
            callback.onFailure(new IllegalStateException("Usuário não autenticado para Firebase."));
            return;
        }
        firestore.collection(COLLECTION_USERS)
                .document(userDocumentId)
                .collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Map<String, Object> value = snapshot.getData();
                        if (value != null) {
                            value.put("id", snapshot.getId());
                            list.add(value);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void salvarUsuario(Usuario usuario, ResultCallback<Void> callback) {
        if (!userPathAvailable()) {
            callback.onFailure(new IllegalStateException("Usuário não autenticado para Firebase."));
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("nome", usuario.getNome());
        data.put("email", usuario.getEmail());
        firestore.collection(COLLECTION_USERS)
                .document(encodeEmail(usuario.getEmail()))
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void salvarTreino(Treino treino, ResultCallback<Void> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("nomeTreino", treino.getNomeTreino());
        data.put("diaSemana", treino.getDiaSemana());
        saveData(COLLECTION_TREINOS, String.valueOf(treino.getIdTreino()), data, callback);
    }

    public void excluirTreino(int idTreino, ResultCallback<Void> callback) {
        deleteData(COLLECTION_TREINOS, String.valueOf(idTreino), callback);
    }

    public void listarTreinos(ResultCallback<List<Treino>> callback) {
        listData(COLLECTION_TREINOS, new ResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                List<Treino> treinos = new ArrayList<>();
                for (Map<String, Object> item : result) {
                    Treino treino = new Treino();
                    treino.setIdTreino(Integer.parseInt((String) item.get("id")));
                    treino.setNomeTreino((String) item.get("nomeTreino"));
                    treino.setDiaSemana((String) item.get("diaSemana"));
                    treinos.add(treino);
                }
                callback.onSuccess(treinos);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void salvarExercicio(Exercicio exercicio, ResultCallback<Void> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("nomeExercicio", exercicio.getNomeExercicio());
        data.put("series", exercicio.getSeries());
        data.put("repeticoes", exercicio.getRepeticoes());
        data.put("peso", exercicio.getPeso());
        data.put("idTreino", exercicio.getIdTreino());
        saveData(COLLECTION_EXERCICIOS, String.valueOf(exercicio.getIdExercicio()), data, callback);
    }

    public void excluirExercicio(int idExercicio, ResultCallback<Void> callback) {
        deleteData(COLLECTION_EXERCICIOS, String.valueOf(idExercicio), callback);
    }

    public void listarExercicios(int idTreino, ResultCallback<List<Exercicio>> callback) {
        if (!userPathAvailable()) {
            callback.onFailure(new IllegalStateException("Usuário não autenticado para Firebase."));
            return;
        }
        firestore.collection(COLLECTION_USERS)
                .document(userDocumentId)
                .collection(COLLECTION_EXERCICIOS)
                .whereEqualTo("idTreino", idTreino)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Exercicio> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Map<String, Object> item = snapshot.getData();
                        if (item == null) continue;
                        Exercicio exercicio = new Exercicio();
                        exercicio.setIdExercicio(Integer.parseInt(snapshot.getId()));
                        exercicio.setNomeExercicio((String) item.get("nomeExercicio"));
                        Number seriesValue = (Number) item.get("series");
                        Number repeticoesValue = (Number) item.get("repeticoes");
                        Number pesoValue = (Number) item.get("peso");
                        Number idTreinoValue = (Number) item.get("idTreino");
                        exercicio.setSeries(seriesValue != null ? seriesValue.intValue() : 0);
                        exercicio.setRepeticoes(repeticoesValue != null ? repeticoesValue.intValue() : 0);
                        exercicio.setPeso(pesoValue != null ? pesoValue.doubleValue() : 0);
                        exercicio.setIdTreino(idTreinoValue != null ? idTreinoValue.intValue() : 0);
                        list.add(exercicio);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void salvarObjetivo(Objetivo objetivo, ResultCallback<Void> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("tipoObjetivo", objetivo.getTipoObjetivo());
        data.put("descricao", objetivo.getDescricao());
        data.put("valorMeta", objetivo.getValorMeta());
        data.put("dataInicio", objetivo.getDataInicio());
        data.put("dataFim", objetivo.getDataFim());
        data.put("status", objetivo.getStatus());
        saveData(COLLECTION_OBJETIVOS, String.valueOf(objetivo.getIdObjetivo()), data, callback);
    }

    public void excluirObjetivo(int idObjetivo, ResultCallback<Void> callback) {
        deleteData(COLLECTION_OBJETIVOS, String.valueOf(idObjetivo), callback);
    }

    public void listarObjetivos(ResultCallback<List<Objetivo>> callback) {
        listData(COLLECTION_OBJETIVOS, new ResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                List<Objetivo> objetivos = new ArrayList<>();
                for (Map<String, Object> item : result) {
                    Objetivo objetivo = new Objetivo();
                    objetivo.setIdObjetivo(Integer.parseInt((String) item.get("id")));
                    objetivo.setTipoObjetivo((String) item.get("tipoObjetivo"));
                    objetivo.setDescricao((String) item.get("descricao"));
                    objetivo.setValorMeta((String) item.get("valorMeta"));
                    objetivo.setDataInicio((String) item.get("dataInicio"));
                    objetivo.setDataFim((String) item.get("dataFim"));
                    objetivo.setStatus((String) item.get("status"));
                    objetivos.add(objetivo);
                }
                callback.onSuccess(objetivos);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void salvarProgresso(Progresso progresso, ResultCallback<Void> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("dataRegistro", progresso.getDataRegistro());
        data.put("peso", progresso.getPeso());
        data.put("gordura", progresso.getGordura());
        data.put("cintura", progresso.getCintura());
        data.put("quadril", progresso.getQuadril());
        data.put("peito", progresso.getPeito());
        data.put("notas", progresso.getNotas());
        saveData(COLLECTION_PROGRESSO, String.valueOf(progresso.getIdProgresso()), data, callback);
    }

    public void excluirProgresso(int idProgresso, ResultCallback<Void> callback) {
        deleteData(COLLECTION_PROGRESSO, String.valueOf(idProgresso), callback);
    }

    public void listarProgresso(ResultCallback<List<Progresso>> callback) {
        listData(COLLECTION_PROGRESSO, new ResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                List<Progresso> progressoList = new ArrayList<>();
                for (Map<String, Object> item : result) {
                    Progresso progresso = new Progresso();
                    progresso.setIdProgresso(Integer.parseInt((String) item.get("id")));
                    progresso.setDataRegistro((String) item.get("dataRegistro"));
                    progresso.setPeso((Double) item.get("peso"));
                    progresso.setGordura((Double) item.get("gordura"));
                    progresso.setCintura((Double) item.get("cintura"));
                    progresso.setQuadril((Double) item.get("quadril"));
                    progresso.setPeito((Double) item.get("peito"));
                    progresso.setNotas((String) item.get("notas"));
                    progressoList.add(progresso);
                }
                callback.onSuccess(progressoList);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
