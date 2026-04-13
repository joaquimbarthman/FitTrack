package com.fittrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.R;
import com.fittrack.model.Treino;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    public interface OnTreinoActionListener {
        void onTreinoClick(Treino treino);
        void onEditarClick(Treino treino);
        void onExcluirClick(Treino treino);
    }

    private final List<Treino> treinos = new ArrayList<>();
    private final OnTreinoActionListener listener;

    public TreinoAdapter(OnTreinoActionListener listener) {
        this.listener = listener;
    }

    public void atualizarLista(List<Treino> novaLista) {
        treinos.clear();
        treinos.addAll(novaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TreinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treino, parent, false);
        return new TreinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreinoViewHolder holder, int position) {
        holder.bind(treinos.get(position));
    }

    @Override
    public int getItemCount() {
        return treinos.size();
    }

    class TreinoViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardTreino;
        private final TextView txtNomeTreino;
        private final TextView txtDiaSemana;
        private final ImageButton btnEditar;
        private final ImageButton btnExcluir;

        public TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTreino = itemView.findViewById(R.id.cardTreino);
            txtNomeTreino = itemView.findViewById(R.id.txtNomeTreino);
            txtDiaSemana = itemView.findViewById(R.id.txtDiaSemana);
            btnEditar = itemView.findViewById(R.id.btnEditarTreino);
            btnExcluir = itemView.findViewById(R.id.btnExcluirTreino);
        }

        public void bind(Treino treino) {
            txtNomeTreino.setText(treino.getNomeTreino());
            txtDiaSemana.setText(itemView.getContext().getString(R.string.dia_item, treino.getDiaSemana()));
            cardTreino.setOnClickListener(v -> listener.onTreinoClick(treino));
            btnEditar.setOnClickListener(v -> listener.onEditarClick(treino));
            btnExcluir.setOnClickListener(v -> listener.onExcluirClick(treino));
        }
    }
}
