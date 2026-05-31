package com.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.adapter.ProgressoAdapter;
import com.fittrack.data.DatabaseHelper;
import com.fittrack.data.FirebaseRepository;
import com.fittrack.model.Progresso;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ProgressoActivity extends AppCompatActivity implements ProgressoAdapter.OnProgressoActionListener {

    public static final String EXTRA_PROGRESSO_ID = "extra_progresso_id";
    public static final String EXTRA_EDITAR_PROGRESSO = "extra_editar_progresso";

    private DatabaseHelper databaseHelper;
    private ProgressoAdapter progressoAdapter;
    private TextView txtEmptyProgresso;
    private int idUsuarioLogado;

    private final ActivityResultLauncher<Intent> progressoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> carregarProgresso());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progresso);

        databaseHelper = new DatabaseHelper(this);
        idUsuarioLogado = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getInt(LoginActivity.KEY_USER_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbarProgresso);
        toolbar.setTitle(R.string.progresso);
        toolbar.setNavigationOnClickListener(v -> finish());

        txtEmptyProgresso = findViewById(R.id.txtEmptyProgresso);
        RecyclerView recyclerView = findViewById(R.id.recyclerProgresso);
        progressoAdapter = new ProgressoAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(progressoAdapter);

        FloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionarProgresso);
        fabAdicionar.setOnClickListener(v -> abrirCadastroProgresso(false, -1));

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_progresso);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_objetivos) {
                startActivity(new Intent(this, ObjetivosActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_progresso) {
                return true;
            }
            return false;
        });

        carregarProgresso();
    }

    private void carregarProgresso() {
        FirebaseRepository firebaseRepository = new FirebaseRepository(this);
        firebaseRepository.listarProgresso(new FirebaseRepository.ResultCallback<List<Progresso>>() {
            @Override
            public void onSuccess(List<Progresso> result) {
                progressoAdapter.atualizarLista(result);
                txtEmptyProgresso.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                List<Progresso> progresso = databaseHelper.listarProgressoPorUsuario(idUsuarioLogado);
                progressoAdapter.atualizarLista(progresso);
                txtEmptyProgresso.setVisibility(progresso.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void abrirCadastroProgresso(boolean editar, int idProgresso) {
        Intent intent = new Intent(this, CadastroProgressoActivity.class);
        intent.putExtra(EXTRA_EDITAR_PROGRESSO, editar);
        if (editar) {
            intent.putExtra(EXTRA_PROGRESSO_ID, idProgresso);
        }
        progressoLauncher.launch(intent);
    }

    @Override
    public void onEditarClick(Progresso progresso) {
        abrirCadastroProgresso(true, progresso.getIdProgresso());
    }

    @Override
    public void onExcluirClick(Progresso progresso) {
        int resultado = databaseHelper.excluirProgresso(progresso.getIdProgresso());
        if (resultado > 0) {
            FirebaseRepository firebaseRepository = new FirebaseRepository(this);
            firebaseRepository.excluirProgresso(progresso.getIdProgresso(), new FirebaseRepository.ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // sincronizado
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ProgressoActivity.this, R.string.erro_firebase, Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, R.string.progresso_excluido, Toast.LENGTH_SHORT).show();
            carregarProgresso();
        }
    }
}
