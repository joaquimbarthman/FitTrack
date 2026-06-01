package com.fittrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.adapter.ObjetivoAdapter;
import com.fittrack.data.DatabaseHelper;
import com.fittrack.data.FirebaseRepository;
import com.fittrack.model.Objetivo;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ObjetivosActivity extends AppCompatActivity implements ObjetivoAdapter.OnObjetivoActionListener {

    public static final String EXTRA_OBJETIVO_ID = "extra_objetivo_id";
    public static final String EXTRA_EDITAR_OBJETIVO = "extra_editar_objetivo";

    private DatabaseHelper databaseHelper;
    private ObjetivoAdapter objetivoAdapter;
    private TextView txtEmptyObjetivos;
    private int idUsuarioLogado;

    private final ActivityResultLauncher<Intent> objetivoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> carregarObjetivos());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivos);

        databaseHelper = new DatabaseHelper(this);
        idUsuarioLogado = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getInt(LoginActivity.KEY_USER_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbarObjetivos);
        toolbar.setTitle(R.string.objetivos);
        toolbar.setNavigationOnClickListener(v -> finish());

        txtEmptyObjetivos = findViewById(R.id.txtEmptyObjetivos);
        RecyclerView recyclerView = findViewById(R.id.recyclerObjetivos);
        objetivoAdapter = new ObjetivoAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(objetivoAdapter);

        FloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionarObjetivo);
        fabAdicionar.setOnClickListener(v -> abrirCadastroObjetivo(false, -1));

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_objetivos);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_objetivos) {
                return true;
            } else if (itemId == R.id.nav_progresso) {
                startActivity(new Intent(this, ProgressoActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_logout) {
                fazerLogout();
                return true;
            }
            return false;
        });

        carregarObjetivos();
    }

    private void fazerLogout() {
        SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        preferences.edit()
                .remove(LoginActivity.KEY_USER_ID)
                .remove(LoginActivity.KEY_USER_EMAIL)
                .apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void carregarObjetivos() {
        FirebaseRepository firebaseRepository = new FirebaseRepository(this);
        firebaseRepository.listarObjetivos(new FirebaseRepository.ResultCallback<List<Objetivo>>() {
            @Override
            public void onSuccess(List<Objetivo> result) {
                objetivoAdapter.atualizarLista(result);
                txtEmptyObjetivos.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                List<Objetivo> objetivos = databaseHelper.listarObjetivosPorUsuario(idUsuarioLogado);
                objetivoAdapter.atualizarLista(objetivos);
                txtEmptyObjetivos.setVisibility(objetivos.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void abrirCadastroObjetivo(boolean editar, int idObjetivo) {
        Intent intent = new Intent(this, CadastroObjetivoActivity.class);
        intent.putExtra(EXTRA_EDITAR_OBJETIVO, editar);
        if (editar) {
            intent.putExtra(EXTRA_OBJETIVO_ID, idObjetivo);
        }
        objetivoLauncher.launch(intent);
    }

    @Override
    public void onEditarClick(Objetivo objetivo) {
        abrirCadastroObjetivo(true, objetivo.getIdObjetivo());
    }

    @Override
    public void onExcluirClick(Objetivo objetivo) {
        int resultado = databaseHelper.excluirObjetivo(objetivo.getIdObjetivo());
        if (resultado > 0) {
            FirebaseRepository firebaseRepository = new FirebaseRepository(this);
            firebaseRepository.excluirObjetivo(objetivo.getIdObjetivo(), new FirebaseRepository.ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // sincronização concluída
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ObjetivosActivity.this, R.string.erro_firebase, Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, R.string.objetivo_excluido, Toast.LENGTH_SHORT).show();
            carregarObjetivos();
        }
    }
}
