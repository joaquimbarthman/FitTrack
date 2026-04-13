package com.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.adapter.ExercicioAdapter;
import com.fittrack.data.DatabaseHelper;
import com.fittrack.model.Exercicio;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExerciciosActivity extends AppCompatActivity implements ExercicioAdapter.OnExercicioActionListener {

    public static final String EXTRA_EXERCICIO_ID = "extra_exercicio_id";

    private DatabaseHelper databaseHelper;
    private ExercicioAdapter exercicioAdapter;
    private TextView txtEmpty;
    private int idTreino;

    private final ActivityResultLauncher<Intent> exercicioLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> carregarExercicios());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercicios);

        databaseHelper = new DatabaseHelper(this);
        idTreino = getIntent().getIntExtra(MainActivity.EXTRA_TREINO_ID, -1);
        String nomeTreino = getIntent().getStringExtra(MainActivity.EXTRA_TREINO_NOME);

        MaterialToolbar toolbar = findViewById(R.id.toolbarExercicios);
        toolbar.setTitle(nomeTreino == null ? getString(R.string.exercicios_treino) : nomeTreino);
        toolbar.setNavigationOnClickListener(v -> finish());

        txtEmpty = findViewById(R.id.txtEmptyExercicios);
        RecyclerView recyclerView = findViewById(R.id.recyclerExercicios);
        FloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionarExercicio);

        exercicioAdapter = new ExercicioAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(exercicioAdapter);

        fabAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroExercicioActivity.class);
            intent.putExtra(MainActivity.EXTRA_TREINO_ID, idTreino);
            exercicioLauncher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarExercicios();
    }

    private void carregarExercicios() {
        List<Exercicio> exercicios = databaseHelper.listarExerciciosPorTreino(idTreino);
        exercicioAdapter.atualizarLista(exercicios);
        txtEmpty.setVisibility(exercicios.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEditarClick(Exercicio exercicio) {
        Intent intent = new Intent(this, CadastroExercicioActivity.class);
        intent.putExtra(MainActivity.EXTRA_EDITAR, true);
        intent.putExtra(EXTRA_EXERCICIO_ID, exercicio.getIdExercicio());
        intent.putExtra(MainActivity.EXTRA_TREINO_ID, idTreino);
        exercicioLauncher.launch(intent);
    }

    @Override
    public void onExcluirClick(Exercicio exercicio) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.acao_excluir)
                .setMessage(R.string.confirmar_exclusao_exercicio)
                .setPositiveButton(R.string.acao_excluir, (dialog, which) -> {
                    databaseHelper.excluirExercicio(exercicio.getIdExercicio());
                    Toast.makeText(this, R.string.exercicio_excluido, Toast.LENGTH_SHORT).show();
                    carregarExercicios();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}
