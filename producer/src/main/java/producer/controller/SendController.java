package producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import producer.dto.User;
import yun.core.start.kafka.client.KafkaProducerManager;

@RestController
@RequestMapping("/send")
public class SendController {

    @Autowired
    private KafkaProducerManager kafkaProducerManager;

    @GetMapping("/send")
    public String hello() {
        // 发送消息到Kafka
        KafkaTemplate<String, Object> template = kafkaProducerManager.getTemplate();
        User user = new User();
        user.setAge(100);
        user.setName("张三");
        template.send("test-topic", "key001"+System.currentTimeMillis(), user);
        return "Hello, Kafka Producer!";
    }
}
