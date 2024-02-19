package io.github.hero821.example.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PublishSample {
    public static void main(String[] args) throws Exception {
        String broker = "tcp://192.168.3.4:1883";
        String topic = "mytopic/1";
        String clientId = "PublishSample";
        String content = "Hello MQTT";
        int qos = 0;

        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        System.out.println("Connecting to broker: " + broker);
        client.connect(options);
        System.out.println("Connected");
        System.out.println("Publishing message: " + content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        client.publish(topic, message);
        System.out.println("Message published");
        client.disconnect();
        System.out.println("Disconnected");
        System.exit(0);
    }
}
