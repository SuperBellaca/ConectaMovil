package com.databit.conectamovil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private FirebaseStorage storage;
    private ImageView imageViewPerfil;
    private TextView Correo;
    private TextView Nombre;
    private TextView Apellido;
    private TextView Usuario;
    private Button btnCambiarFoto;

    private Button btnEditarPerfil;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        Correo = findViewById(R.id.txtCorreo);
        Nombre = findViewById(R.id.txtNombre);
        Apellido = findViewById(R.id.txtApellido);
        Usuario = findViewById(R.id.txtUsuario);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        imageViewPerfil = findViewById(R.id.imageViewPerfil);

        // Cargar datos del perfil
        cargarDatosPerfil();

        // Configurar listener para el botón de cambiar foto de perfil
        btnCambiarFoto.setOnClickListener(view -> seleccionarNuevaFoto());

        btnEditarPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
            startActivity(intent);
        });
    }


    private void cargarDatosPerfil() {
        DatabaseReference usuarioReference = databaseReference.child("users").child(userId);
        usuarioReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User usuario = snapshot.getValue(User.class);

                    // Mostrar datos en la interfaz de usuario
                    if (usuario != null) {
                        Correo.setText(usuario.getEmail());
                        Nombre.setText(usuario.getNombre());
                        Apellido.setText(usuario.getApellido());
                        Usuario.setText(usuario.getUsuario());

                        // Verificar si el usuario ha cambiado la foto de perfil
                        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
                            // El usuario ha cambiado la foto de perfil, cargar desde Firebase Storage
                            cargarImagenDesdeStorage(usuario.getUrlFotoPerfil());
                        } else {
                            // El usuario no ha cambiado la foto de perfil, cargar la imagen preestablecida
                            imageViewPerfil.setImageResource(R.drawable.padoru);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores en la lectura de datos
                Log.e("PerfilActivity", "Error al cargar datos del perfil: " + error.getMessage());
            }
        });
    }

    private void cargarImagenDesdeStorage(String imageUrl) {
        // Limpiar la caché de Picasso para la URL anterior
        Picasso.get().invalidate(imageUrl);

        // Puedes usar una biblioteca de carga de imágenes como Picasso
        Picasso.get().load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageViewPerfil, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Éxito al cargar la imagen desde Storage
                        Log.d("PerfilActivity", "Imagen cargada exitosamente desde Storage");
                    }

                    @Override
                    public void onError(Exception e) {
                        // Manejar errores al cargar la imagen desde Storage
                        Log.e("PerfilActivity", "Error al cargar la imagen desde Storage: " + e.getMessage());

                        // Si hay un error, puedes cargar la imagen predeterminada o realizar alguna otra acción
                        imageViewPerfil.setImageResource(R.drawable.padoru);
                    }
                });
    }

    private void seleccionarNuevaFoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            subirNuevaFoto(filePath);
        }
    }

    private void subirNuevaFoto(Uri filePath) {
        // Obtener la referencia en Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("perfil_imagenes/" + userId + "/imagen.jpg");

        // Subir la imagen
        UploadTask uploadTask = imageRef.putFile(filePath);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Imagen subida exitosamente
            Toast.makeText(this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();

            // Obtener la URL de descarga y actualizar la referencia en Firebase Realtime Database
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Actualizar la referencia en Firebase Realtime Database con la URL de la imagen
                DatabaseReference usuarioReference = databaseReference.child("users").child(userId);
                usuarioReference.child("urlFotoPerfil").setValue(uri.toString());

                // Mostrar la nueva imagen en la interfaz de usuario usando Picasso
                Picasso.get().load(uri).into(imageViewPerfil, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Éxito al cargar la nueva imagen
                    }

                    @Override
                    public void onError(Exception e) {
                        // Manejar errores al cargar la nueva imagen
                        Log.e("PerfilActivity", "Error al cargar la nueva imagen: " + e.getMessage());
                    }
                });
            }).addOnFailureListener(e -> {
                // Manejar errores en la obtención de la URL de descarga
                Log.e("PerfilActivity", "Error al obtener la URL de descarga: " + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            // Manejar errores en la subida de la imagen
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            Log.e("PerfilActivity", "Error al subir la imagen: " + e.getMessage());
        });
    }
}