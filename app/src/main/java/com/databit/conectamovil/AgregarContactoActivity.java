package com.databit.conectamovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AgregarContactoActivity extends AppCompatActivity {

    private EditText editTextUsuario;
    private EditText editTextCorreo;
    private Button btnAgregarContacto;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        // Inicializar la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        btnAgregarContacto = findViewById(R.id.btnAgregarContacto);

        btnAgregarContacto.setOnClickListener(view -> {
            // Obtener usuario y correo de EditText
            String usuario = editTextUsuario.getText().toString().trim();
            String correo = editTextCorreo.getText().toString().trim();

            // Verificar si el usuario y el correo ya existen
            verificarExistenciaUsuarioYCorreo(usuario, correo);
        });
    }

    private void verificarExistenciaUsuarioYCorreo(final String usuario, final String correo) {
        DatabaseReference usersReference = databaseReference.child("users");

        // Consultar Firebase para verificar si el usuario ya existe en "users"
        usersReference.orderByChild("usuario").equalTo(usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // El usuario ya existe en "users", verificar el correo
                    verificarExistenciaCorreo(usuario, correo);
                } else {
                    // El usuario no existe en "users"
                    Toast.makeText(AgregarContactoActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                    Log.d("AgregarContactoActivity", "El usuario no existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores
                Log.e("AgregarContactoActivity", "verificarExistenciaUsuarioYCorreo - onCancelled: " + error.getMessage());
            }
        });
    }

    private void verificarExistenciaCorreo(final String usuario, final String correo) {
        DatabaseReference usersReference = databaseReference.child("users");

        // Consultar Firebase para verificar si el correo ya existe en "users"
        usersReference.orderByChild("email").equalTo(correo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // El correo ya existe en "users", permitir agregar contacto
                    agregarContacto(usuario, correo);
                } else {
                    // El correo no existe en "users"
                    Toast.makeText(AgregarContactoActivity.this, "El correo no existe", Toast.LENGTH_SHORT).show();
                    Log.d("AgregarContactoActivity", "El correo no existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores
                Log.e("AgregarContactoActivity", "verificarExistenciaCorreo - onCancelled: " + error.getMessage());
            }
        });
    }

    private void agregarContacto(final String usuario, final String correo) {
        // Verificar si el contacto ya existe en "contactos"
        DatabaseReference contactosReference = databaseReference.child("contactos");
        contactosReference.orderByChild("correo").equalTo(correo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // El contacto ya existe en "contactos"
                    Toast.makeText(AgregarContactoActivity.this, "El contacto ya existe", Toast.LENGTH_SHORT).show();
                    Log.d("AgregarContactoActivity", "El contacto ya existe");
                } else {
                    // El contacto no existe en "contactos", agregarlo
                    String key = databaseReference.child("contactos").push().getKey();
                    Contactos contacto = new Contactos(key, usuario, correo);
                    databaseReference.child("contactos").child(key).setValue(contacto);

                    Toast.makeText(AgregarContactoActivity.this, "Contacto agregado correctamente", Toast.LENGTH_SHORT).show();
                    Log.d("AgregarContactoActivity", "Contacto agregado correctamente");

                    // Opcionalmente, puedes finalize la actividad o navegar a otra pantalla
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores
                Log.e("AgregarContactoActivity", "agregarContacto - onCancelled: " + error.getMessage());
            }
        });
    }
}
