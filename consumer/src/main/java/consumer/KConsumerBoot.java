package consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class KConsumerBoot {

    public static void main(String[] args) {
        SpringApplication.run(KConsumerBoot.class, args);
    }
}
