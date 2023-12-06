package com.databit.conectamovil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactosViewHolder> {

    private List<Contactos> listaContactos;

    public ContactosAdapter(List<Contactos> listaContactos) {
        this.listaContactos = listaContactos;
    }

    public static class ContactosViewHolder extends RecyclerView.ViewHolder {
        public TextView usuarioTextView;
        public TextView correoTextView;

        public ContactosViewHolder(View itemView) {
            super(itemView);
            usuarioTextView = itemView.findViewById(R.id.usuarioTextView);
            correoTextView = itemView.findViewById(R.id.correoTextView);
        }
    }

    @NonNull
    @Override
    public ContactosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contactos, parent, false);
        return new ContactosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactosViewHolder holder, int position) {
        Contactos contacto = listaContactos.get(position);
        holder.usuarioTextView.setText(contacto.getUsuario());
        holder.correoTextView.setText(contacto.getCorreo());
    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }
}
