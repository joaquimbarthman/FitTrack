package com.fittrack;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.fittrack.data.DatabaseHelper;
import com.fittrack.data.FirebaseRepository;
import com.fittrack.model.Progresso;
import com.google.android.material.appbar.MaterialToolbar;
import com.fittrack.util.ValidationUtils;

public class CadastroProgressoActivity extends AppCompatActivity {

    private EditText edtDataRegistro;
    private EditText edtPeso;
    private EditText edtGordura;
    private EditText edtCintura;
    private EditText edtQuadril;
    private EditText edtPeito;
    private EditText edtNotas;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private DatabaseHelper databaseHelper;
    private boolean modoEdicao;
    private int idProgresso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_progresso);

        databaseHelper = new DatabaseHelper(this);
        modoEdicao = getIntent().getBooleanExtra(ProgressoActivity.EXTRA_EDITAR_PROGRESSO, false);
        idProgresso = getIntent().getIntExtra(ProgressoActivity.EXTRA_PROGRESSO_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCadastroProgresso);
        toolbar.setTitle(modoEdicao ? R.string.editar_progresso : R.string.adicionar_progresso);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtDataRegistro = findViewById(R.id.edtDataRegistro);
        edtPeso = findViewById(R.id.edtPesoProgresso);
        edtGordura = findViewById(R.id.edtGordura);
        edtCintura = findViewById(R.id.edtCintura);
        edtQuadril = findViewById(R.id.edtQuadril);
        edtPeito = findViewById(R.id.edtPeito);
        edtNotas = findViewById(R.id.edtNotas);
        Button btnSalvar = findViewById(R.id.btnSalvarProgresso);

        setupDateField(edtDataRegistro);

        if (modoEdicao && idProgresso != -1) {
            preencherCampos();
        }

        btnSalvar.setOnClickListener(v -> salvarProgresso());
    }

    private void preencherCampos() {
        Progresso progresso = databaseHelper.buscarProgressoPorId(idProgresso);
        if (progresso == null) {
            finish();
            return;
        }

        edtDataRegistro.setText(progresso.getDataRegistro());
        edtPeso.setText(String.valueOf(progresso.getPeso()));
        edtGordura.setText(String.valueOf(progresso.getGordura()));
        edtCintura.setText(String.valueOf(progresso.getCintura()));
        edtQuadril.setText(String.valueOf(progresso.getQuadril()));
        edtPeito.setText(String.valueOf(progresso.getPeito()));
        edtNotas.setText(progresso.getNotas());
    }

    private void setupDateField(EditText editText) {
        editText.setOnClickListener(v -> showDatePicker(editText));
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker(editText);
            }
        });
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        String currentValue = editText.getText().toString().trim();
        if (!currentValue.isEmpty()) {
            try {
                calendar.setTime(dateFormat.parse(currentValue));
            } catch (ParseException ignored) {
            }
        }

        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> editText.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void salvarProgresso() {
        String dataRegistro = edtDataRegistro.getText().toString().trim();
        String textoPeso = edtPeso.getText().toString().trim();
        String textoGordura = edtGordura.getText().toString().trim();
        String textoCintura = edtCintura.getText().toString().trim();
        String textoQuadril = edtQuadril.getText().toString().trim();
        String textoPeito = edtPeito.getText().toString().trim();
        String notas = edtNotas.getText().toString().trim();

        if (!ValidationUtils.isNotEmpty(dataRegistro)) {
            edtDataRegistro.setError(getString(R.string.progresso_obrigatorio));
            edtDataRegistro.requestFocus();
            return;
        }

        if (!ValidationUtils.isPositiveDouble(textoPeso)) {
            edtPeso.setError(getString(R.string.progresso_obrigatorio));
            edtPeso.requestFocus();
            return;
        }

        Progresso progresso = new Progresso();
        progresso.setDataRegistro(dataRegistro);
        progresso.setPeso(Double.parseDouble(textoPeso));
        progresso.setGordura(textoGordura.isEmpty() ? 0 : Double.parseDouble(textoGordura));
        progresso.setCintura(textoCintura.isEmpty() ? 0 : Double.parseDouble(textoCintura));
        progresso.setQuadril(textoQuadril.isEmpty() ? 0 : Double.parseDouble(textoQuadril));
        progresso.setPeito(textoPeito.isEmpty() ? 0 : Double.parseDouble(textoPeito));
        progresso.setNotas(notas);

        FirebaseRepository firebaseRepository = new FirebaseRepository(this);
        if (modoEdicao) {
            progresso.setIdProgresso(idProgresso);
            databaseHelper.atualizarProgresso(progresso);
            firebaseRepository.salvarProgresso(progresso, new FirebaseRepository.ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // sincronizado
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CadastroProgressoActivity.this, R.string.erro_firebase, Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, R.string.progresso_atualizado, Toast.LENGTH_SHORT).show();
        } else {
            int idUsuario = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                    .getInt(LoginActivity.KEY_USER_ID, -1);
            progresso.setIdUsuario(idUsuario);
            long id = databaseHelper.inserirProgresso(progresso);
            progresso.setIdProgresso((int) id);
            firebaseRepository.salvarProgresso(progresso, new FirebaseRepository.ResultCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // sincronizado
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CadastroProgressoActivity.this, R.string.erro_firebase, Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this, R.string.progresso_salvo, Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}
