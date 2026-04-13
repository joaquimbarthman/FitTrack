package com.fittrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fittrack.R;
import com.fittrack.model.Exercicio;

import java.util.ArrayList;
import java.util.List;

public class ExercicioAdapter extends RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder> {

    public interface OnExercicioActionListener {
        void onEditarClick(Exercicio exercicio);
        void onExcluirClick(Exercicio exercicio);
    }

    private final List<Exercicio> exercicios = new ArrayList<>();
    private final OnExercicioActionListener listener;

    public ExercicioAdapter(OnExercicioActionListener listener) {
        this.listener = listener;
    }

    public void atualizarLista(List<Exercicio> novaLista) {
        exercicios.clear();
        exercicios.addAll(novaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercicio, parent, false);
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        holder.bind(exercicios.get(position));
    }

    @Override
    public int getItemCount() {
        return exercicios.size();
    }

    class ExercicioViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtNomeExercicio;
        private final TextView txtSeries;
        private final TextView txtRepeticoes;
        private final TextView txtPeso;
        private final ImageButton btnEditar;
        private final ImageButton btnExcluir;

        public ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeExercicio = itemView.findViewById(R.id.txtNomeExercicio);
            txtSeries = itemView.findViewById(R.id.txtSeries);
            txtRepeticoes = itemView.findViewById(R.id.txtRepeticoes);
            txtPeso = itemView.findViewById(R.id.txtPeso);
            btnEditar = itemView.findViewById(R.id.btnEditarExercicio);
            btnExcluir = itemView.findViewById(R.id.btnExcluirExercicio);
        }

        public void bind(Exercicio exercicio) {
            txtNomeExercicio.setText(exercicio.getNomeExercicio());
            txtSeries.setText(itemView.getContext().getString(R.string.series_item, exercicio.getSeries()));
            txtRepeticoes.setText(itemView.getContext().getString(R.string.repeticoes_item, exercicio.getRepeticoes()));
            txtPeso.setText(itemView.getContext().getString(R.string.peso_item, exercicio.getPeso()));
            btnEditar.setOnClickListener(v -> listener.onEditarClick(exercicio));
            btnExcluir.setOnClickListener(v -> listener.onExcluirClick(exercicio));
        }
    }
}
