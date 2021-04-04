package com.example.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaAgendaAdapter extends RecyclerView.Adapter<ListaAgendaAdapter.ViewHolder> {
    private ArrayList<Asunto> dataset;
    private Context context;
    private Asunto asunto;

    public ListaAgendaAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListaAgendaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaAgendaAdapter.ViewHolder holder, int position) {
        asunto = dataset.get(position);
        holder.fecha.setText(asunto.getFecha());
        holder.hora.setText(asunto.getHora());
        holder.asunto.setText(asunto.getAsunto());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarAsuntos(ArrayList<Asunto> listaAsuntos) {
        dataset.clear();
        dataset.addAll(listaAsuntos);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fecha, hora, asunto;
        private CardView tarjeta;

        public ViewHolder(View itemView) {
            super(itemView);

            fecha = itemView.findViewById(R.id.fechaTextView);
            hora = itemView.findViewById(R.id.horaTextView);
            asunto = itemView.findViewById(R.id.asuntoTextView);
            tarjeta = itemView.findViewById(R.id.tarjeta);
        }
    }
}
