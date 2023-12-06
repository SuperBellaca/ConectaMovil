package com.databit.conectamovil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private CardView cardView;
    private Button btnIngresar;
    private EditText correo, contra;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LoginActivity", "onCreate called");
        setContentView(R.layout.activity_login);

        cardView = findViewById(R.id.cardView);
        btnIngresar = findViewById(R.id.btnIngresar);
        correo = findViewById(R.id.editTxTCorreo);
        contra = findViewById(R.id.editTxTContra);
        mAuth = FirebaseAuth.getInstance();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correoUser = correo.getText().toString().trim();
                String contraUser = contra.getText().toString().trim();

                if (correoUser.isEmpty() || contraUser.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Ingresar los datos", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(correoUser, contraUser);
                }
            }
            private void loginUser(String correoUser, String contraUser) {
                mAuth.signInWithEmailAndPassword(correoUser, contraUser)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Guardar el estado de sesión en SharedPreferences
                                saveSessionState(true);

                                // Finalizar esta actividad y abrir MainActivity
                                finish();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "La contraseña o el correo es erróneo", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show());
            }
        });
        cardView.setVisibility(View.VISIBLE);
    }
    private void saveSessionState(boolean isActive) {
        // Obtener una referencia a SharedPreferences
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);

        // Obtener un editor de SharedPreferences para realizar cambios
        SharedPreferences.Editor editor = preferences.edit();

        // Almacenar el estado de la sesión (activo o inactivo)
        editor.putBoolean("is_active", isActive);

        // Aplicar los cambios
        editor.apply();
    }

    private void hideCardView() {
        cardView.setVisibility(View.INVISIBLE);
    }
    public void onButtonClick(View view) {
        hideCardView();
    }
    public void onCrearCuentaClick(View view) {
        Intent intent = new Intent(this, CuentaActivity.class);
        startActivity(intent);
    }
    private boolean isSessionActive() {
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        return preferences.getBoolean("is_active", false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isSessionActive()) {
            // Si la sesión está activa, ir directamente a MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
