package com.databit.conectamovil2;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class MQTTManager {

    private MqttClient client;
    private DatabaseReference databaseReference;

    public MQTTManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void connect(String brokerUrl, String clientId) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client.connect(connectOptions);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void publicarMensaje(String topic, Mensajes mensaje) {
        try {
            if (client != null && client.isConnected()) {
                String mensajeJson = convertirMensajeAJson(mensaje);
                MqttMessage mqttMessage = new MqttMessage(mensajeJson.getBytes());
                mqttMessage.setQos(0);
                client.publish(topic, mqttMessage);
            } else {
                Log.e("MQTTManager", "No se puede publicar el mensaje. Cliente MQTT no conectado.");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private String convertirMensajeAJson(Mensajes mensaje) {
        Gson gson = new Gson();
        return gson.toJson(mensaje);
    }


    public void suscribirseATema(String topic) {
        try {
            if (client != null && client.isConnected()) {
                client.subscribe(topic, 1);
            } else {
                Log.e("MQTTManager", "No se puede suscribir al tema. Cliente MQTT no conectado.");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}