package io.github.hero821.example.springboot.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author li.qiang
 */
//@RestController
@RequestMapping("kafka")
public class KafKaProducer {
    private final Logger logger = LoggerFactory.getLogger(KafKaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafKaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @RequestMapping("send")
    public String send(String name) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("TestTopic", "name", name);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("producer - 消息发送成功:" + result.toString());
            }

            @Override
            public void onFailure(Throwable e) {
                logger.error("producer - 消息发送失败:" + e.getMessage());
            }
        });
        return "ok";
    }
}
