package com.databit.conectamovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ListaContactosActivity extends AppCompatActivity {

    private List<Contactos> listaCompletaContactos;
    private List<Contactos> listaFiltradaContactos;
    private ContactosAdapter contactosAdapter;
    private RecyclerView recyclerView;
    private EditText editTextBuscar;

    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        btn1 = findViewById(R.id.btnAniadirContacto);
        btn1.setOnClickListener(view -> {
            Intent btn1Intent = new Intent(ListaContactosActivity.this, AgregarContactoActivity.class);
            startActivity(btn1Intent);
        });
        // Inicializar las listas
        listaCompletaContactos = new ArrayList<>();
        listaFiltradaContactos = new ArrayList<>();

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Configurar el adaptador
        contactosAdapter = new ContactosAdapter(listaFiltradaContactos);
        recyclerView.setAdapter(contactosAdapter);

        // Configurar el EditText de búsqueda
        editTextBuscar = findViewById(R.id.editTextBuscar);
        editTextBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtrarListaContactos(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        obtenerListaContactosDesdeFirebase();
    }

    private void filtrarListaContactos(String textoBusqueda) {
        listaFiltradaContactos.clear();

        for (Contactos contacto : listaCompletaContactos) {
            if (contacto.getUsuario().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                listaFiltradaContactos.add(contacto);
            }
        }

        contactosAdapter.notifyDataSetChanged();
    }

    private void obtenerListaContactosDesdeFirebase() {
        DatabaseReference contactosRef = FirebaseDatabase.getInstance().getReference("contactos");

        contactosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaCompletaContactos.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contactos contacto = snapshot.getValue(Contactos.class);

                    if (contacto != null) {
                        listaCompletaContactos.add(contacto);
                    }
                }
                listaFiltradaContactos.clear();
                listaFiltradaContactos.addAll(listaCompletaContactos);
                contactosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ListaContactosActivity", "Error al obtener la lista de contactos", databaseError.toException());
            }
        });
    }
}
