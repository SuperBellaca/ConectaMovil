package com.databit.conectamovil2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "AndroidFirebase";
    private DatabaseReference databaseReference;
    private MQTTManager mqttManager;

    Button btn1;
    Button btn2;
    Button btn3;
    Button btnCerrarSesion;
    Button btnLimpiarMensajes;
    Button btnEnviarMensaje;
    EditText edtMensaje;
    TextView txtMensajes;

    private String usuarioActualId;  // ID del usuario actual, deberías establecer esto al autenticar al usuario
    private String receptorId;  // ID del usuario con el que estás interactuando

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mqttManager = new MQTTManager();
        mqttManager.connect(BROKER_URL, CLIENT_ID);

        if (savedInstanceState == null) {
            checkSessionAndRedirect();
        }

        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btnMain);
        btn2 = findViewById(R.id.btnContactos);
        btn3 = findViewById(R.id.btnPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnLimpiarMensajes = findViewById(R.id.btnLimpiarMensajes);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        edtMensaje = findViewById(R.id.edtMensaje);
        txtMensajes = findViewById(R.id.txtMensajes);

        btn1.setOnClickListener(view -> {
            Intent btn1Intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(btn1Intent);
        });

        btn2.setOnClickListener(view -> {
            Intent btn2Intent = new Intent(MainActivity.this, ListaContactosActivity.class);
            startActivity(btn2Intent);
        });

        btn3.setOnClickListener(view -> {
            Intent btn3Intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(btn3Intent);
        });

        btnCerrarSesion.setOnClickListener(view -> showLogoutDialog());

        btnLimpiarMensajes.setOnClickListener(view -> limpiarMensajes());

        btnEnviarMensaje.setOnClickListener(view -> enviarMensaje());


        usuarioActualId = "JeJ6bNREK6RBU0raUM7IB7WWeGB2";
        receptorId = "8X9TdBMfVuflaPjThWTiefrD0Ew2";


        String temaConversacion = "conversacion_" + usuarioActualId + "_" + receptorId;
        mqttManager.suscribirseATema(temaConversacion);
    }

    private void checkSessionAndRedirect() {
        if (!isSessionActive()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void limpiarMensajes() {
        txtMensajes.setText("");
    }

    private void displayMessage(String message) {
        txtMensajes.append(message + "\n");
    }

    private void saveSessionState(boolean isActive) {
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_active", isActive);
        editor.apply();
    }

    private void enviarMensaje() {
        String mensajeTexto = edtMensaje.getText().toString().trim();

        if (!TextUtils.isEmpty(mensajeTexto)) {
            // Crea un objeto Mensajes y conviértelo a JSON
            Mensajes mensaje = new Mensajes(new User(), new User(), mensajeTexto);  // Ajusta según tu lógica
            String temaConversacion = "conversacion_" + usuarioActualId + "_" + receptorId;
            mqttManager.publicarMensaje(temaConversacion, mensaje);

            edtMensaje.setText("");
        } else {
            Toast.makeText(this, "Ingrese un mensaje antes de enviar", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSessionActive() {
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        return preferences.getBoolean("is_active", false);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar Sesión");
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            saveSessionState(false);
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSessionAndRedirect();
    }

    @Override
    protected void onDestroy() {
        mqttManager.disconnect();
        super.onDestroy();
    }
}
