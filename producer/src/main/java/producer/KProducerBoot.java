package producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class KProducerBoot {
    public static void main(String[] args) {
        SpringApplication.run(KProducerBoot.class, args);
    }
}
