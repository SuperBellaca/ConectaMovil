package com.databit.conectamovil2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextApellido, editTextUsuario, editTextContrasenia;
    private Button btnActualizarDatos, btnCambiarContrasenia;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private ImageView ojo;

    private String contraseniaRegistrada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContrasenia = findViewById(R.id.editTextContrasenia);
        btnActualizarDatos = findViewById(R.id.btnActualizarDatos);
        btnCambiarContrasenia = findViewById(R.id.btnCambiarContrasenia);
        ojo = findViewById(R.id.ImageViewOjo);
        obtenerContraseniaRegistrada();
        cargarDatosUsuario();

        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion("¿Desea actualizar los datos?", true);
            }
        });

        btnCambiarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditarPerfilActivity", "Botón Cambiar Contraseña clickeado");
                mostrarDialogoConfirmacion("¿Desea cambiar la contraseña?", false);
            }
        });
        ojo.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                togglePasswordVisibility(v);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                togglePasswordVisibility(v);
                return true;
            }
            return false;
        });
    }

    public void togglePasswordVisibility(View view) {
        EditText editTextContrasenia = findViewById(R.id.editTextContrasenia);
        ImageView imgShowPassword = findViewById(R.id.ImageViewOjo);

        if (editTextContrasenia.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editTextContrasenia.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgShowPassword.setImageResource(R.drawable.ojo);
        } else {
            editTextContrasenia.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imgShowPassword.setImageResource(R.drawable.ojo);
        }

        editTextContrasenia.setSelection(editTextContrasenia.getText().length());

        if (editTextContrasenia.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editTextContrasenia.setText("");
            editTextContrasenia.append(contraseniaRegistrada);
        }
    }


    private void cargarDatosUsuario() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User usuario = dataSnapshot.getValue(User.class);

                    if (usuario != null) {

                        Log.d("EditarPerfilActivity", "Nombre: " + usuario.getNombre());
                        Log.d("EditarPerfilActivity", "Apellido: " + usuario.getApellido());
                        Log.d("EditarPerfilActivity", "Usuario: " + usuario.getUsuario());

                        editTextNombre.setText(usuario.getNombre());
                        editTextApellido.setText(usuario.getApellido());
                        editTextUsuario.setText(usuario.getUsuario());
                    }
                } else {
                    Log.d("EditarPerfilActivity", "No se encontraron datos para el usuario");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditarPerfilActivity.this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerContraseniaRegistrada() {
        userRef.child("contrasenia").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    contraseniaRegistrada = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditarPerfilActivity.this, "Error al obtener la contraseña registrada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion(String mensaje, final boolean esActualizacionDatos) {
        Log.d("EditarPerfilActivity", "Mostrar diálogo de confirmación");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar acción");
        builder.setMessage(mensaje);

        builder.setPositiveButton("Sí", (dialog, which) -> {
            if (esActualizacionDatos) {
                actualizarDatosUsuario();
            } else {
                cambiarContrasenia();
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void actualizarDatosUsuario() {
        String nuevoNombre = editTextNombre.getText().toString();
        String nuevoApellido = editTextApellido.getText().toString();
        String nuevoUsuario = editTextUsuario.getText().toString();

        userRef.child("nombre").setValue(nuevoNombre);
        userRef.child("apellido").setValue(nuevoApellido);
        userRef.child("usuario").setValue(nuevoUsuario);

        Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show();

        // Establecer el resultado y finalizar la actividad
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    private void cambiarContrasenia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Contraseña");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String contraseniaActual = input.getText().toString();

            new android.os.Handler().post(() -> {
                AuthCredential credential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), contraseniaActual);
                auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                    if (reauthTask.isSuccessful()) {
                        dialog.dismiss();
                        mostrarDialogoCambiarContrasenia();
                    } else {
                        Toast.makeText(EditarPerfilActivity.this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void mostrarDialogoCambiarContrasenia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Contraseña");

        final EditText nuevaContraseniaInput = new EditText(this);
        nuevaContraseniaInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(nuevaContraseniaInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String nuevaContrasenia = nuevaContraseniaInput.getText().toString();
            auth.getCurrentUser().updatePassword(nuevaContrasenia).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userRef.child("contrasenia").setValue(nuevaContrasenia);

                    contraseniaRegistrada = nuevaContrasenia;

                    editTextContrasenia.setText(nuevaContrasenia);

                    Toast.makeText(EditarPerfilActivity.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditarPerfilActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}