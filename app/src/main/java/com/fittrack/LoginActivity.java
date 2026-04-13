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

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "fittrack_prefs";
    public static final String KEY_USER_ID = "user_id";

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
        return preferences.getInt(KEY_USER_ID, -1) != -1;
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, R.string.login_obrigatorio, Toast.LENGTH_SHORT).show();
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
                .apply();

        abrirMain();
    }

    private void abrirMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
