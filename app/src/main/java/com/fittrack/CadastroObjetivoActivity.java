package com.fittrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fittrack.data.DatabaseHelper;
import com.fittrack.data.FirebaseRepository;
import com.fittrack.model.Objetivo;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.fittrack.util.ValidationUtils;

public class CadastroObjetivoActivity extends AppCompatActivity {

    private EditText edtTipoObjetivo;
    private EditText edtDescricaoObjetivo;
    private EditText edtValorMeta;
    private EditText edtDataInicio;
    private EditText edtDataFim;
    private MaterialAutoCompleteTextView autoStatus;
    private DatabaseHelper databaseHelper;
    private boolean modoEdicao;
    private int idObjetivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_objetivo);

        databaseHelper = new DatabaseHelper(this);
        modoEdicao = getIntent().getBooleanExtra(ObjetivosActivity.EXTRA_EDITAR_OBJETIVO, false);
        idObjetivo = getIntent().getIntExtra(ObjetivosActivity.EXTRA_OBJETIVO_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCadastroObjetivo);
        toolbar.setTitle(modoEdicao ? R.string.editar_objetivo : R.string.adicionar_objetivo);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtTipoObjetivo = findViewById(R.id.edtTipoObjetivo);
        edtDescricaoObjetivo = findViewById(R.id.edtDescricaoObjetivo);
        edtValorMeta = findViewById(R.id.edtValorMeta);
        edtDataInicio = findViewById(R.id.edtDataInicio);
        edtDataFim = findViewById(R.id.edtDataFim);
        autoStatus = findViewById(R.id.autoStatusObjetivo);
        Button btnSalvar = findViewById(R.id.btnSalvarObjetivo);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.status_opcoes)
        );
        autoStatus.setAdapter(statusAdapter);

        if (modoEdicao && idObjetivo != -1) {
            preencherCampos();
        }

        btnSalvar.setOnClickListener(v -> salvarObjetivo());

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
            }
            return false;
        });
    }

    private void preencherCampos() {
        Objetivo objetivo = databaseHelper.buscarObjetivoPorId(idObjetivo);
        if (objetivo == null) {
            finish();
            return;
        }

        edtTipoObjetivo.setText(objetivo.getTipoObjetivo());
        edtDescricaoObjetivo.setText(objetivo.getDescricao());
        edtValorMeta.setText(objetivo.getValorMeta());
        edtDataInicio.setText(objetivo.getDataInicio());
        edtDataFim.setText(objetivo.getDataFim());
        autoStatus.setText(objetivo.getStatus(), false);
    }

    private void salvarObjetivo() {
        String tipoObjetivo = edtTipoObjetivo.getText().toString().trim();
        String descricao = edtDescricaoObjetivo.getText().toString().trim();
        String valorMeta = edtValorMeta.getText().toString().trim();
        String dataInicio = edtDataInicio.getText().toString().trim();
        String dataFim = edtDataFim.getText().toString().trim();
        String status = autoStatus.getText().toString().trim();

        if (!ValidationUtils.isNotEmpty(tipoObjetivo)) {
            edtTipoObjetivo.setError(getString(R.string.objetivo_obrigatorio));
            edtTipoObjetivo.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(dataInicio)) {
            edtDataInicio.setError(getString(R.string.objetivo_obrigatorio));
            edtDataInicio.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(status)) {
            autoStatus.setError(getString(R.string.objetivo_obrigatorio));
            autoStatus.requestFocus();
            return;
        }

        if (ValidationUtils.isNotEmpty(valorMeta) && !ValidationUtils.isPositiveDouble(valorMeta)) {
            edtValorMeta.setError(getString(R.string.objetivo_obrigatorio));
            edtValorMeta.requestFocus();
            return;
        }

        Objetivo objetivo = new Objetivo();
        objetivo.setTipoObjetivo(tipoObjetivo);
        objetivo.setDescricao(descricao);
        objetivo.setValorMeta(valorMeta);
        objetivo.setDataInicio(dataInicio);
        objetivo.setDataFim(dataFim);
        objetivo.setStatus(status);

        FirebaseRepository firebaseRepository = new FirebaseRepository(this);
        if (modoEdicao) {
            objetivo.setIdObjetivo(idObjetivo);
            databaseHelper.atualizarObjetivo(objetivo);
            firebaseRepository.salvarObjetivo(objetivo, new FirebaseRepository.ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // sincronizado
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CadastroObjetivoActivity.this, R.string.erro_firebase, Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, R.string.objetivo_atualizado, Toast.LENGTH_SHORT).show();
        } else {
            int idUsuario = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                    .getInt(LoginActivity.KEY_USER_ID, -1);
            objetivo.setIdUsuario(idUsuario);
            long id = databaseHelper.inserirObjetivo(objetivo);
            objetivo.setIdObjetivo((int) id);
            firebaseRepository.salvarObjetivo(objetivo, new FirebaseRepository.ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // sincronizado
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CadastroObjetivoActivity.this, R.string.erro_firebase, Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, R.string.objetivo_salvo, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}
