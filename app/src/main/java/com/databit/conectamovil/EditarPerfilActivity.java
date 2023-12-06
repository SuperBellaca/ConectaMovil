package com.databit.conectamovil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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

    private String contraseniaRegistrada; // Variable para almacenar la contraseña registrada en la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Obtener la instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Obtener el ID del usuario actualmente autenticado
        String userId = auth.getCurrentUser().getUid();

        // Crear una referencia a la instancia del usuario en Firebase
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Inicializar los componentes de la interfaz de usuario
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContrasenia = findViewById(R.id.editTextContrasenia);
        btnActualizarDatos = findViewById(R.id.btnActualizarDatos);
        btnCambiarContrasenia = findViewById(R.id.btnCambiarContrasenia);
        ojo = findViewById(R.id.ImageViewOjo);
        // Obtener la contraseña registrada al iniciar la actividad
        obtenerContraseniaRegistrada();

        // Cargar datos del usuario en los EditText como hints
        cargarDatosUsuario();

        // Configurar el botón para actualizar datos
        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion("¿Desea actualizar los datos?", true);
            }
        });

        // Configurar el botón para cambiar la contraseña
        btnCambiarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditarPerfilActivity", "Botón Cambiar Contraseña clickeado");
                mostrarDialogoConfirmacion("¿Desea cambiar la contraseña?", false);
            }
        });
        ojo.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Cuando se presiona el ícono del ojo, muestra la contraseña
                togglePasswordVisibility(v);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // Cuando se libera el ícono del ojo, oculta la contraseña
                togglePasswordVisibility(v);
                return true;
            }
            return false;
        });
    }

    public void togglePasswordVisibility(View view) {
        EditText editTextContrasenia = findViewById(R.id.editTextContrasenia);
        ImageView imgShowPassword = findViewById(R.id.ImageViewOjo); // Corregido el ID

        if (editTextContrasenia.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Cambiar a contraseña oculta
            editTextContrasenia.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgShowPassword.setImageResource(R.drawable.ojo);
        } else {
            // Cambiar a contraseña visible
            editTextContrasenia.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imgShowPassword.setImageResource(R.drawable.ojo);
        }

        // Mover el cursor al final del texto
        editTextContrasenia.setSelection(editTextContrasenia.getText().length());

        // Si la contraseña está visible, establecer el texto sin asteriscos
        if (editTextContrasenia.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editTextContrasenia.setText(""); // Limpiar el texto actual
            editTextContrasenia.append(contraseniaRegistrada); // Agregar la nueva contraseña
        }
    }


    private void cargarDatosUsuario() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User usuario = dataSnapshot.getValue(User.class);

                    if (usuario != null) {
                        // Registro de depuración para verificar los datos obtenidos
                        Log.d("EditarPerfilActivity", "Nombre: " + usuario.getNombre());
                        Log.d("EditarPerfilActivity", "Apellido: " + usuario.getApellido());
                        Log.d("EditarPerfilActivity", "Usuario: " + usuario.getUsuario());

                        // Establecer los valores en los EditText
                        editTextNombre.setText(usuario.getNombre());
                        editTextApellido.setText(usuario.getApellido());
                        editTextUsuario.setText(usuario.getUsuario());
                        // Puedes agregar más campos según sea necesario
                    }
                } else {
                    // Registro de depuración para verificar si no hay datos
                    Log.d("EditarPerfilActivity", "No se encontraron datos para el usuario");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores en la lectura de datos
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
                // Manejar errores en la lectura de datos
                Toast.makeText(EditarPerfilActivity.this, "Error al obtener la contraseña registrada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion(String mensaje, final boolean esActualizacionDatos) {
        Log.d("EditarPerfilActivity", "Mostrar diálogo de confirmación");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar acción");
        builder.setMessage(mensaje);

        // Agregar botón positivo (Sí, confirmar)
        builder.setPositiveButton("Sí", (dialog, which) -> {
            if (esActualizacionDatos) {
                actualizarDatosUsuario();
            } else {
                cambiarContrasenia();
            }
        });

        // Agregar botón negativo (No, cancelar)
        builder.setNegativeButton("No", (dialog, which) -> {
            // No hacer nada, simplemente cerrar el cuadro de diálogo
            dialog.dismiss();
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void actualizarDatosUsuario() {
        String nuevoNombre = editTextNombre.getText().toString();
        String nuevoApellido = editTextApellido.getText().toString();
        String nuevoUsuario = editTextUsuario.getText().toString();

        // Actualizar los campos correspondientes en la base de datos
        userRef.child("nombre").setValue(nuevoNombre);
        userRef.child("apellido").setValue(nuevoApellido);
        userRef.child("usuario").setValue(nuevoUsuario);
        // Puedes agregar más campos según sea necesario

        // Aquí puedes mostrar un mensaje de éxito o manejar el resultado de otra manera
        Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void cambiarContrasenia() {
        // Obtener la contraseña actual del usuario mediante un cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Contraseña");

        // Cambiado a un EditText final para que pueda ser referenciado en el método onComplete
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String contraseniaActual = input.getText().toString();

            // Utilizar un Handler para realizar operaciones en el hilo principal
            new android.os.Handler().post(() -> {
                // Verificar la contraseña actual antes de cambiarla
                AuthCredential credential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), contraseniaActual);
                auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                    if (reauthTask.isSuccessful()) {
                        // Contraseña actual verificada, ahora se puede cambiar
                        dialog.dismiss();
                        mostrarDialogoCambiarContrasenia();
                    } else {
                        // Contraseña actual incorrecta
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

        // EditText para la nueva contraseña
        final EditText nuevaContraseniaInput = new EditText(this);
        nuevaContraseniaInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(nuevaContraseniaInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Obtener la nueva contraseña del EditText
            String nuevaContrasenia = nuevaContraseniaInput.getText().toString();

            // Actualizar la contraseña en Firebase Authentication
            auth.getCurrentUser().updatePassword(nuevaContrasenia).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Contraseña actualizada exitosamente

                    // También actualiza la contraseña en Firebase Realtime Database
                    userRef.child("contrasenia").setValue(nuevaContrasenia);

                    // Actualizar la variable contraseniaRegistrada con la nueva contraseña
                    contraseniaRegistrada = nuevaContrasenia;

                    // Actualizar el EditText con la nueva contraseña
                    editTextContrasenia.setText(nuevaContrasenia);

                    Toast.makeText(EditarPerfilActivity.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al actualizar la contraseña
                    Toast.makeText(EditarPerfilActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}