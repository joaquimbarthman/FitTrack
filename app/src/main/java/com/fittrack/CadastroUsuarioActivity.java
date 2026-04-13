package com.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fittrack.data.DatabaseHelper;
import com.fittrack.model.Usuario;
import com.google.android.material.appbar.MaterialToolbar;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtSenha;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCadastroUsuario);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtNome = findViewById(R.id.edtNomeCadastro);
        edtEmail = findViewById(R.id.edtEmailCadastro);
        edtSenha = findViewById(R.id.edtSenhaCadastro);
        Button btnCadastrar = findViewById(R.id.btnCadastrarUsuario);
        TextView txtEntrar = findViewById(R.id.txtIrLogin);

        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());
        txtEntrar.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void cadastrarUsuario() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, R.string.usuario_obrigatorio, Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.emailExiste(email)) {
            Toast.makeText(this, R.string.email_existente, Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        databaseHelper.inserirUsuario(usuario);

        Toast.makeText(this, R.string.cadastro_sucesso, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
