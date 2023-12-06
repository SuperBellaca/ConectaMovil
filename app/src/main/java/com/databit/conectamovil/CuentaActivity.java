package com.databit.conectamovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CuentaActivity extends AppCompatActivity {

    Button btnRegistrar;
    EditText editTxTPassword, editTxTEmail, editTxTNombre, editTxTApellido, editTxTUsuario;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mUsersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        TextView txtVolverLogin = findViewById(R.id.txtVolverLogin);

        txtVolverLogin.setOnClickListener(view -> {
            Intent intent = new Intent(CuentaActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        editTxTNombre = findViewById(R.id.editTxTNombre);
        editTxTEmail = findViewById(R.id.editTxTEmail);
        editTxTPassword = findViewById(R.id.editTxTPassword);
        editTxTApellido = findViewById(R.id.editTxTApellido);
        editTxTUsuario = findViewById(R.id.editTxTUsuario);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mDatabase.getReference("users");

        btnRegistrar.setOnClickListener(v -> {
            Log.d("CuentaActivity", "Botón Registrar clickeado");
            String nameUser = editTxTNombre.getText().toString().trim();
            String lastnameUser = editTxTApellido.getText().toString().trim();
            String nicknameUser = editTxTUsuario.getText().toString().trim();
            String emailUser = editTxTEmail.getText().toString().trim();
            String passUser = editTxTPassword.getText().toString().trim();
            String urlFotoPerfil="@drawable/padoru";

            if (nameUser.isEmpty() || emailUser.isEmpty() || passUser.isEmpty()) {
                Toast.makeText(CuentaActivity.this, "Complete los datos", Toast.LENGTH_SHORT).show();
            } else {
                if (!isValidEmail(emailUser)) {
                    Toast.makeText(CuentaActivity.this, "Formato de correo electrónico no válido", Toast.LENGTH_SHORT).show();
                } else if (!isValidName(nameUser) || !isValidName(lastnameUser)) {
                    Toast.makeText(CuentaActivity.this, "El nombre y el apellido solo pueden contener letras", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(nameUser, lastnameUser, nicknameUser, emailUser, passUser, urlFotoPerfil);
                }
            }
        });    }

    private void registerUser(String nameUser, String lastnameUser, String nicknameUser, String emailUser, String passUser, String urlFotoPerfil) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();
                    User user = new User(id, nameUser, lastnameUser, nicknameUser, emailUser, passUser,urlFotoPerfil);

                    mUsersReference.child(id).setValue(user).addOnSuccessListener(unused -> {
                        Toast.makeText(CuentaActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                        Log.d("CuentaActivity", "Usuario registrado con éxito");
                        Intent intent = new Intent(CuentaActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(CuentaActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toast.makeText(CuentaActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    Log.e("CuentaActivity", "Error al registrar: " + task.getException().getMessage());
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@gmail\\.com+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidName(String name) {
        String namePattern = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(namePattern);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}
