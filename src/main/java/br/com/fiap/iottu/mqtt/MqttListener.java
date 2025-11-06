package br.com.fiap.iottu.mqtt;

import br.com.fiap.iottu.antenna.AntennaService;
import br.com.fiap.iottu.dto.AntenasPayloadDTO;
import br.com.fiap.iottu.dto.MotorcyclesPayloadDTO;
import br.com.fiap.iottu.motorcycle.MotorcycleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@ConditionalOnProperty(prefix = "mqtt", name = "enabled", havingValue = "true")
public class MqttListener {

    private static final Logger log = LoggerFactory.getLogger(MqttListener.class);
    private static final String MQTT_TOPIC_DATA = "fiap/iot/moto";

    @Autowired
    private IMqttClient mqttClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MotorcycleService motorcycleService;

    @Autowired
    private AntennaService antenaService;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            log.info("Subscribing to MQTT topic: {}", MQTT_TOPIC_DATA);
            mqttClient.subscribe(MQTT_TOPIC_DATA, 1, this::handleMqttMessage);
        } catch (MqttException e) {
            log.error("Error subscribing to MQTT topic: {}", MQTT_TOPIC_DATA, e);
        }
    }

    private void handleMqttMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        log.info("Received MQTT message from topic {}: {}", topic, payload);

        try {
            JsonNode rootNode = objectMapper.readTree(payload);

            if (rootNode.has("motos")) {
                MotorcyclesPayloadDTO motorcyclesPayload = objectMapper.treeToValue(rootNode, MotorcyclesPayloadDTO.class);
                motorcycleService.processMotorcyclesData(motorcyclesPayload.getMotos());
                log.info("Successfully processed {} motorcycle(s).", motorcyclesPayload.getMotos().size());
            } else if (rootNode.has("antenas")) {
                AntenasPayloadDTO antenasPayload = objectMapper.treeToValue(rootNode, AntenasPayloadDTO.class);
                antenaService.processAntennasData(antenasPayload.getAntenas());
                log.info("Successfully processed {} antenna(s).", antenasPayload.getAntenas().size());
            } else {
                log.warn("Received MQTT message with unrecognized payload structure: {}", payload);
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing MQTT message: {}", payload, e);
        } catch (Exception e) {
            log.error("Error processing MQTT data: {}", payload, e);
        }
    }
}
