package com.databit.conectamovil2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactosViewHolder> {

    private List<Contactos> listaContactos;
    private OnContactoClickListener contactoClickListener;

    public interface OnContactoClickListener {
        void onContactoClick(Contactos contacto);
    }

    public ContactosAdapter(List<Contactos> listaContactos) {
        this.listaContactos = listaContactos;
    }

    public void setOnContactoClickListener(OnContactoClickListener listener) {
        this.contactoClickListener = listener;
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

        ContactosViewHolder viewHolder = new ContactosViewHolder(itemView);
        itemView.setOnClickListener(view -> {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && contactoClickListener != null) {
                contactoClickListener.onContactoClick(listaContactos.get(position));
            }
        });

        return viewHolder;
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