package com.example.tiendav1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.tiendav1.R;


public class MqttActivity extends AppCompatActivity {

    private static final String BROKER_URL = "tcp://androidteststiqq.cloud.shiftr.io:1883";
    private static final String CLIENT_ID = "Lian";

    private String topic, message;
    private MqttHandler mqttHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);

        Button subscribeButton = findViewById(R.id.button2);
        Button messageButton = findViewById(R.id.button);
        Toast.makeText(this, "Handler", Toast.LENGTH_LONG).show();
        mqttHandler = new MqttHandler();
        Toast.makeText(this, "conectando", Toast.LENGTH_SHORT).show();
        mqttHandler.connect(BROKER_URL, CLIENT_ID, this);
        Toast.makeText(this, "creado mqtt", Toast.LENGTH_SHORT).show();
        topic = "Tema3";
        message = "Test de mensaje al tema 3 ";

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribeToTopic(topic);
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMessage(topic, message);
            }
        });
    }
    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();

    }
    private void publishMessage(String topic, String message){
        Toast.makeText(this, "Publishing message: " + message, Toast.LENGTH_SHORT).show();
        mqttHandler.publish(topic,message);
    }
    private void subscribeToTopic(String topic){
        Toast.makeText(this, "Subscribing to topic "+ topic, Toast.LENGTH_SHORT).show();
        mqttHandler.subscribe(topic);
    }



}

