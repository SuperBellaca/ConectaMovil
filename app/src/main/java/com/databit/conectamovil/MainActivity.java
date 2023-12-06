package com.databit.conectamovil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn1;
    Button btn2;
    Button btn3;
    Button btnCerrarSesion;
    Button btnLimpiarMensajes;
    Button btnEnviarMensaje;  // Nuevo botón para enviar mensajes
    EditText edtMensaje;      // Nuevo campo de texto para escribir mensajes
    TextView txtMensajes;

    private static final String BROKER_URL = "tcp://192.168.1.102:1883";
    private static final String CLIENT_ID = "your_client_id";
    private static final String USER_ID = "ylr4lsscDMTVm6fSA9o8XOOMkkl2";  // ID del usuario actual
    private static final String OTHER_USER_ID = "xCkSKl1TfIUQidX0ELIOuAWG4f92";  // ID del otro usuario
    private DatabaseReference databaseReference;
    private MQTTManager mqttManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mqttManager = new MQTTManager();
        mqttManager.connect(BROKER_URL, CLIENT_ID, generateTopic(OTHER_USER_ID));

        if (savedInstanceState == null) {
            checkSessionAndRedirect();
        }

        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btnMain);
        btn2 = findViewById(R.id.btnContactos);
        btn3 = findViewById(R.id.btnPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnLimpiarMensajes = findViewById(R.id.btnLimpiarMensajes);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje); // Agregado
        edtMensaje = findViewById(R.id.edtMensaje); // Agregado
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

        btnEnviarMensaje.setOnClickListener(view -> enviarMensaje()); // Agregado
    }
    private void saveMessageToFirebase(String topic, String userId, String message) {
        // Guarda el mensaje en la base de datos
        databaseReference.child("Chats").child(topic).child(userId).push().setValue(message);
    }

    private void enviarMensaje() {
        String mensaje = edtMensaje.getText().toString().trim();
        if (!TextUtils.isEmpty(mensaje)) {
            String topic = generateTopic(OTHER_USER_ID);

            if (mqttManager != null && mqttManager.isConnected()) {
                mqttManager.publish(mensaje);
                displayMessage("Tú: " + mensaje);
                saveMessageToFirebase(topic, USER_ID, mensaje);
                edtMensaje.setText("");
            } else {
                Log.e("MainActivity", "MQTT Manager not connected");
                // Agrega más información de registro según sea necesario para entender el flujo de ejecución
            }
        } else {
            Log.e("MainActivity", "Mensaje vacío");
            // Agrega más información de registro según sea necesario para entender el flujo de ejecución
        }
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

    private String generateTopic(String otherUserId) {
        List<String> userIds = Arrays.asList(USER_ID, otherUserId);
        Collections.sort(userIds);
        return TextUtils.join("_", userIds);
    }

}
