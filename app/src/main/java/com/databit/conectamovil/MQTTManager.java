package com.databit.conectamovil;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import android.text.TextUtils;
import android.util.Log;

public class MQTTManager {

    private MqttClient client;
    private DatabaseReference databaseReference;
    private String currentTopic;

    public MQTTManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void connect(String brokerUrl, String clientId, String topic) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client.connect(connectOptions);

            currentTopic = topic;
            subscribe(topic);
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

    public void publish(String message) {
        if (client != null && currentTopic != null) {
            try {
                if (client.isConnected()) {
                    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                    client.publish(currentTopic, mqttMessage);
                    saveMessageToFirebase(currentTopic, message);
                } else {
                    Log.e("MQTTManager", "MQTT Client not connected");
                    // Agrega más información de registro según sea necesario para entender el flujo de ejecución
                }
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e("MQTTManager", "Error publishing message: " + e.getMessage());
            }
        } else {
            Log.e("MQTTManager", "MQTT Client or topic is null");
            // Agrega más información de registro según sea necesario para entender el flujo de ejecución
        }
    }
    public void subscribe(String topic) {
        try {
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public boolean isConnected() {
        return client != null && client.isConnected();
    }
    private void saveMessageToFirebase(String topic, String message) {
        databaseReference.child("Chats").child(topic).push().setValue(message);
    }
}
