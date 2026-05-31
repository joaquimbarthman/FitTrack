package com.fittrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.R;
import com.fittrack.model.Progresso;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ProgressoAdapter extends RecyclerView.Adapter<ProgressoAdapter.ProgressoViewHolder> {

    public interface OnProgressoActionListener {
        void onEditarClick(Progresso progresso);
        void onExcluirClick(Progresso progresso);
    }

    private final List<Progresso> progressoList = new ArrayList<>();
    private final OnProgressoActionListener listener;

    public ProgressoAdapter(OnProgressoActionListener listener) {
        this.listener = listener;
    }

    public void atualizarLista(List<Progresso> novaLista) {
        progressoList.clear();
        progressoList.addAll(novaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProgressoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progresso, parent, false);
        return new ProgressoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressoViewHolder holder, int position) {
        holder.bind(progressoList.get(position));
    }

    @Override
    public int getItemCount() {
        return progressoList.size();
    }

    class ProgressoViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardProgresso;
        private final TextView txtDataRegistro;
        private final TextView txtPeso;
        private final TextView txtDetalhes;
        private final ImageButton btnEditar;
        private final ImageButton btnExcluir;

        public ProgressoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProgresso = itemView.findViewById(R.id.cardProgresso);
            txtDataRegistro = itemView.findViewById(R.id.txtDataRegistro);
            txtPeso = itemView.findViewById(R.id.txtPesoProgresso);
            txtDetalhes = itemView.findViewById(R.id.txtDetalhesProgresso);
            btnEditar = itemView.findViewById(R.id.btnEditarProgresso);
            btnExcluir = itemView.findViewById(R.id.btnExcluirProgresso);
        }

        public void bind(Progresso progresso) {
            txtDataRegistro.setText(progresso.getDataRegistro());
            txtPeso.setText(itemView.getContext().getString(R.string.peso_item_progresso, progresso.getPeso()));
            txtDetalhes.setText(itemView.getContext().getString(R.string.medidas_item_progresso,
                    progresso.getGordura(), progresso.getCintura(), progresso.getQuadril(), progresso.getPeito()));
            btnEditar.setOnClickListener(v -> listener.onEditarClick(progresso));
            btnExcluir.setOnClickListener(v -> listener.onExcluirClick(progresso));
        }
    }
}
