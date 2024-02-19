package io.github.hero821.example.springboot.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author li.qiang
 */
//@Component
public class KafKaConsumer {
    private final Logger logger = LoggerFactory.getLogger(KafKaConsumer.class);

    @KafkaListener(topics = {"TestTopic"})
    public void receive(ConsumerRecord<?, ?> record) {
        logger.info("consumer - 消息被消费 --- key:" + record.key());
        logger.info("consumer - 消息被消费 --- value:" + record.value().toString());
    }
}
