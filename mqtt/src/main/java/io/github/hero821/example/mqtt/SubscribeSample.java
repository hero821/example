package io.github.hero821.example.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SubscribeSample {
    public static void main(String[] args) throws Exception {
        String broker = "tcp://192.168.3.4:1883";
        String topic = "mytopic/1";
        String clientId = "SubscribeSample";
        int qos = 0;

        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
        client.setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                System.out.println("connectionLost: " + cause.getMessage());
            }

            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("topic: " + topic);
                System.out.println("Qos: " + message.getQos());
                System.out.println("message content: " + new String(message.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("deliveryComplete: " + token.isComplete());
            }
        });
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        System.out.println("Connecting to broker: " + broker);
        client.connect(options);
        System.out.println("Connected");
        System.out.println("Subscribing message ...");
        client.subscribe(topic, qos);
    }
}
