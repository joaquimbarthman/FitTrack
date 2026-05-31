package com.fittrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.R;
import com.fittrack.model.Objetivo;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ObjetivoAdapter extends RecyclerView.Adapter<ObjetivoAdapter.ObjetivoViewHolder> {

    public interface OnObjetivoActionListener {
        void onEditarClick(Objetivo objetivo);
        void onExcluirClick(Objetivo objetivo);
    }

    private final List<Objetivo> objetivos = new ArrayList<>();
    private final OnObjetivoActionListener listener;

    public ObjetivoAdapter(OnObjetivoActionListener listener) {
        this.listener = listener;
    }

    public void atualizarLista(List<Objetivo> novaLista) {
        objetivos.clear();
        objetivos.addAll(novaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ObjetivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objetivo, parent, false);
        return new ObjetivoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjetivoViewHolder holder, int position) {
        holder.bind(objetivos.get(position));
    }

    @Override
    public int getItemCount() {
        return objetivos.size();
    }

    class ObjetivoViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardObjetivo;
        private final TextView txtTipoObjetivo;
        private final TextView txtMeta;
        private final TextView txtPeriodo;
        private final TextView txtStatus;
        private final ImageButton btnEditar;
        private final ImageButton btnExcluir;

        public ObjetivoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardObjetivo = itemView.findViewById(R.id.cardObjetivo);
            txtTipoObjetivo = itemView.findViewById(R.id.txtTipoObjetivo);
            txtMeta = itemView.findViewById(R.id.txtMetaObjetivo);
            txtPeriodo = itemView.findViewById(R.id.txtPeriodoObjetivo);
            txtStatus = itemView.findViewById(R.id.txtStatusObjetivo);
            btnEditar = itemView.findViewById(R.id.btnEditarObjetivo);
            btnExcluir = itemView.findViewById(R.id.btnExcluirObjetivo);
        }

        public void bind(Objetivo objetivo) {
            txtTipoObjetivo.setText(objetivo.getTipoObjetivo());
            txtMeta.setText(itemView.getContext().getString(R.string.valor_meta_item, objetivo.getValorMeta()));
            txtPeriodo.setText(itemView.getContext().getString(R.string.periodo_item, objetivo.getDataInicio(), objetivo.getDataFim() != null ? objetivo.getDataFim() : itemView.getContext().getString(R.string.sem_data_fim)));
            txtStatus.setText(objetivo.getStatus());
            btnEditar.setOnClickListener(v -> listener.onEditarClick(objetivo));
            btnExcluir.setOnClickListener(v -> listener.onExcluirClick(objetivo));
        }
    }
}
