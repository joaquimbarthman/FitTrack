package com.fittrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fittrack.data.DatabaseHelper;
import com.fittrack.model.Usuario;
import com.fittrack.util.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "fittrack_prefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";

    private EditText edtEmail;
    private EditText edtSenha;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        if (usuarioLogado()) {
            abrirMain();
            return;
        }

        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        Button btnEntrar = findViewById(R.id.btnEntrar);
        TextView txtCadastro = findViewById(R.id.txtIrCadastro);

        btnEntrar.setOnClickListener(v -> realizarLogin());
        txtCadastro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CadastroUsuarioActivity.class)));
    }

    private boolean usuarioLogado() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getInt(KEY_USER_ID, -1) != -1
                && preferences.getString(KEY_USER_EMAIL, null) != null;
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (!ValidationUtils.isNotEmpty(email)) {
            edtEmail.setError(getString(R.string.login_obrigatorio));
            edtEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            edtEmail.setError(getString(R.string.login_invalido));
            edtEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isNotEmpty(senha)) {
            edtSenha.setError(getString(R.string.login_obrigatorio));
            edtSenha.requestFocus();
            return;
        }

        Usuario usuario = databaseHelper.autenticarUsuario(email, senha);
        if (usuario == null) {
            Toast.makeText(this, R.string.login_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(KEY_USER_ID, usuario.getIdUsuario())
                .putString(KEY_USER_EMAIL, email)
                .apply();

        abrirMain();
    }

    private void abrirMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
