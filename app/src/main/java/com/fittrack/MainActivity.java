package com.fittrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.adapter.TreinoAdapter;
import com.fittrack.data.DatabaseHelper;
import com.fittrack.model.Treino;
import com.fittrack.model.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TreinoAdapter.OnTreinoActionListener {

    public static final String EXTRA_TREINO_ID = "extra_treino_id";
    public static final String EXTRA_TREINO_NOME = "extra_treino_nome";
    public static final String EXTRA_EDITAR = "extra_editar";

    private DatabaseHelper databaseHelper;
    private TreinoAdapter treinoAdapter;
    private TextView txtBoasVindas;
    private TextView txtEmpty;
    private int idUsuarioLogado;

    private final ActivityResultLauncher<Intent> treinoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> carregarTreinos());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        idUsuarioLogado = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getInt(LoginActivity.KEY_USER_ID, -1);

        if (idUsuarioLogado == -1) {
            redirecionarLogin();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        txtBoasVindas = findViewById(R.id.txtBoasVindas);
        txtEmpty = findViewById(R.id.txtEmptyTreinos);
        RecyclerView recyclerView = findViewById(R.id.recyclerTreinos);
        FloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionarTreino);

        treinoAdapter = new TreinoAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(treinoAdapter);

        fabAdicionar.setOnClickListener(v ->
                treinoLauncher.launch(new Intent(this, CadastroTreinoActivity.class)));

        carregarCabecalho();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTreinos();
    }

    private void carregarCabecalho() {
        Usuario usuario = databaseHelper.buscarUsuarioPorId(idUsuarioLogado);
        String nome = usuario != null ? usuario.getNome() : getString(R.string.usuario_inicial);
        txtBoasVindas.setText(getString(R.string.ola_usuario, nome));
    }

    private void carregarTreinos() {
        List<Treino> treinos = databaseHelper.listarTreinosPorUsuario(idUsuarioLogado);
        treinoAdapter.atualizarLista(treinos);
        txtEmpty.setVisibility(treinos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void redirecionarLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences preferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
            preferences.edit().remove(LoginActivity.KEY_USER_ID).apply();
            redirecionarLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTreinoClick(Treino treino) {
        Intent intent = new Intent(this, ExerciciosActivity.class);
        intent.putExtra(EXTRA_TREINO_ID, treino.getIdTreino());
        intent.putExtra(EXTRA_TREINO_NOME, treino.getNomeTreino());
        startActivity(intent);
    }

    @Override
    public void onEditarClick(Treino treino) {
        Intent intent = new Intent(this, CadastroTreinoActivity.class);
        intent.putExtra(EXTRA_EDITAR, true);
        intent.putExtra(EXTRA_TREINO_ID, treino.getIdTreino());
        treinoLauncher.launch(intent);
    }

    @Override
    public void onExcluirClick(Treino treino) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.acao_excluir)
                .setMessage(R.string.confirmar_exclusao_treino)
                .setPositiveButton(R.string.acao_excluir, (dialog, which) -> {
                    databaseHelper.excluirTreino(treino.getIdTreino());
                    Toast.makeText(this, R.string.treino_excluido, Toast.LENGTH_SHORT).show();
                    carregarTreinos();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}
