package consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yun.core.start.kafka.config.KafkaServiceProperties;

@RequestMapping("/pull")
@RestController
public class PullController {

    @Autowired
    private KafkaServiceProperties kafkaServiceProperties;

    @GetMapping("/hello")
    public String hello() {
        //
        System.out.println(kafkaServiceProperties.getDefaultName());

        return "Hello, Kafka!";
    }
}
