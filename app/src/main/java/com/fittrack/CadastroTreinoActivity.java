package com.fittrack;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fittrack.data.DatabaseHelper;
import com.fittrack.model.Treino;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class CadastroTreinoActivity extends AppCompatActivity {

    private EditText edtNomeTreino;
    private MaterialAutoCompleteTextView autoDiaSemana;
    private DatabaseHelper databaseHelper;
    private boolean modoEdicao;
    private int idTreino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_treino);

        databaseHelper = new DatabaseHelper(this);
        modoEdicao = getIntent().getBooleanExtra(MainActivity.EXTRA_EDITAR, false);
        idTreino = getIntent().getIntExtra(MainActivity.EXTRA_TREINO_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCadastroTreino);
        toolbar.setTitle(modoEdicao ? R.string.editar_treino : R.string.adicionar_treino);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtNomeTreino = findViewById(R.id.edtNomeTreino);
        autoDiaSemana = findViewById(R.id.autoDiaSemana);
        Button btnSalvar = findViewById(R.id.btnSalvarTreino);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.dias_semana)
        );
        autoDiaSemana.setAdapter(adapter);

        if (modoEdicao && idTreino != -1) {
            preencherCampos();
        }

        btnSalvar.setOnClickListener(v -> salvarTreino());
    }

    private void preencherCampos() {
        Treino treino = databaseHelper.buscarTreinoPorId(idTreino);
        if (treino == null) {
            finish();
            return;
        }
        edtNomeTreino.setText(treino.getNomeTreino());
        autoDiaSemana.setText(treino.getDiaSemana(), false);
    }

    private void salvarTreino() {
        String nomeTreino = edtNomeTreino.getText().toString().trim();
        String diaSemana = autoDiaSemana.getText().toString().trim();

        if (nomeTreino.isEmpty() || diaSemana.isEmpty()) {
            Toast.makeText(this, R.string.treino_obrigatorio, Toast.LENGTH_SHORT).show();
            return;
        }

        Treino treino = new Treino();
        treino.setNomeTreino(nomeTreino);
        treino.setDiaSemana(diaSemana);

        if (modoEdicao) {
            treino.setIdTreino(idTreino);
            databaseHelper.atualizarTreino(treino);
            Toast.makeText(this, R.string.treino_atualizado, Toast.LENGTH_SHORT).show();
        } else {
            int idUsuario = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                    .getInt(LoginActivity.KEY_USER_ID, -1);
            treino.setIdUsuario(idUsuario);
            databaseHelper.inserirTreino(treino);
            Toast.makeText(this, R.string.treino_salvo, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}
