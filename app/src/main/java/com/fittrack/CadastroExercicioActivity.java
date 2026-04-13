package com.fittrack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fittrack.data.DatabaseHelper;
import com.fittrack.model.Exercicio;
import com.google.android.material.appbar.MaterialToolbar;

public class CadastroExercicioActivity extends AppCompatActivity {

    private EditText edtNomeExercicio;
    private EditText edtSeries;
    private EditText edtRepeticoes;
    private EditText edtPeso;
    private DatabaseHelper databaseHelper;
    private boolean modoEdicao;
    private int idExercicio;
    private int idTreino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_exercicio);

        databaseHelper = new DatabaseHelper(this);
        modoEdicao = getIntent().getBooleanExtra(MainActivity.EXTRA_EDITAR, false);
        idExercicio = getIntent().getIntExtra(ExerciciosActivity.EXTRA_EXERCICIO_ID, -1);
        idTreino = getIntent().getIntExtra(MainActivity.EXTRA_TREINO_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCadastroExercicio);
        toolbar.setTitle(modoEdicao ? R.string.editar_exercicio : R.string.adicionar_exercicio);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtNomeExercicio = findViewById(R.id.edtNomeExercicio);
        edtSeries = findViewById(R.id.edtSeries);
        edtRepeticoes = findViewById(R.id.edtRepeticoes);
        edtPeso = findViewById(R.id.edtPeso);
        Button btnSalvar = findViewById(R.id.btnSalvarExercicio);

        if (modoEdicao && idExercicio != -1) {
            preencherCampos();
        }

        btnSalvar.setOnClickListener(v -> salvarExercicio());
    }

    private void preencherCampos() {
        Exercicio exercicio = databaseHelper.buscarExercicioPorId(idExercicio);
        if (exercicio == null) {
            finish();
            return;
        }

        edtNomeExercicio.setText(exercicio.getNomeExercicio());
        edtSeries.setText(String.valueOf(exercicio.getSeries()));
        edtRepeticoes.setText(String.valueOf(exercicio.getRepeticoes()));
        edtPeso.setText(String.valueOf(exercicio.getPeso()));
    }

    private void salvarExercicio() {
        String nome = edtNomeExercicio.getText().toString().trim();
        String textoSeries = edtSeries.getText().toString().trim();
        String textoRepeticoes = edtRepeticoes.getText().toString().trim();
        String textoPeso = edtPeso.getText().toString().trim();

        if (nome.isEmpty() || textoSeries.isEmpty() || textoRepeticoes.isEmpty() || textoPeso.isEmpty()) {
            Toast.makeText(this, R.string.exercicio_obrigatorio, Toast.LENGTH_SHORT).show();
            return;
        }

        Exercicio exercicio = new Exercicio();
        exercicio.setNomeExercicio(nome);
        exercicio.setSeries(Integer.parseInt(textoSeries));
        exercicio.setRepeticoes(Integer.parseInt(textoRepeticoes));
        exercicio.setPeso(Double.parseDouble(textoPeso));
        exercicio.setIdTreino(idTreino);

        if (modoEdicao) {
            exercicio.setIdExercicio(idExercicio);
            databaseHelper.atualizarExercicio(exercicio);
            Toast.makeText(this, R.string.exercicio_atualizado, Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.inserirExercicio(exercicio);
            Toast.makeText(this, R.string.exercicio_salvo, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}
