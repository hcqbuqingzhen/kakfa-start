package yun.core.start.kafka.auto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yun.core.start.kafka.client.AbstractIKafkaConsumer;
import yun.core.start.kafka.client.KafkaConsumerRegistrar;
import yun.core.start.kafka.client.KafkaProducerManager;
import yun.core.start.kafka.config.KafkaConsumerProperties;
import yun.core.start.kafka.config.KafkaServiceProperties;

import java.util.List;

@Configuration
@EnableConfigurationProperties({KafkaServiceProperties.class,KafkaConsumerProperties.class})
public class KafkaProducerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public KafkaProducerManager kafkaProducerManager(KafkaServiceProperties properties) {
        return new KafkaProducerManager(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaConsumerRegistrar kafkaConsumerRegistrar(KafkaServiceProperties properties, KafkaConsumerProperties consumerProperties, List<AbstractIKafkaConsumer<?>> kafkaConsumers) {
        return new KafkaConsumerRegistrar(properties,consumerProperties,kafkaConsumers);
    }
}
