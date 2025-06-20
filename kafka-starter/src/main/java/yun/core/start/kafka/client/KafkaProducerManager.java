package yun.core.start.kafka.client;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import yun.core.start.kafka.config.KafkaServiceProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Lazy
public class KafkaProducerManager {
    private final KafkaServiceProperties kafkaProps;
    private final Map<String, KafkaTemplate<String, Object>> templateCache = new ConcurrentHashMap<>();

    public KafkaProducerManager( KafkaServiceProperties kafkaProps) {
        this.kafkaProps = kafkaProps;
    }

    /**
     * 无参：默认实例 + JsonSerializer
     * 默认获取第一个实例的 KafkaTemplate
     */
    public KafkaTemplate<String, Object> getTemplate() {
        String alias = kafkaProps.resolveDefaultAlias();
        return getTemplate(alias, JsonSerializer.class);
    }

    /**
     * 指定实例 alias，默认 JsonSerializer
     */
    public  KafkaTemplate<String, Object> getTemplate(String alias) {
        return getTemplate(alias,  JsonSerializer.class);
    }

    /**
     * 指定实例 alias + 序列化器 class
     */
    public KafkaTemplate<String, Object> getTemplate(String alias, Class<? extends Serializer> serializerClass) {
        if (!StringUtils.hasText(alias)) {
            throw new IllegalArgumentException("alias cannot be null or empty");
        }

        String key = alias + "#" + serializerClass.getName();

        return templateCache.computeIfAbsent(key, k -> {
            KafkaServiceProperties.KafkaInstance instance = kafkaProps.resolveKafkaInstance(alias);
            Map<String, Object> props = new HashMap<>();

            // 基础配置
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, instance.getServerAddr());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClass);
            // 默认消费者会读这个头 禁用
            props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

            // 如果有用户名密码，配置 SASL（假设是 SASL_PLAIN，示例）
            if (StringUtils.hasText(instance.getUsername()) && StringUtils.hasText(instance.getPassword())) {
                props.put("security.protocol", "SASL_PLAINTEXT");
                props.put("sasl.mechanism", "PLAIN");
                String jaasCfg = String.format(
                        "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                        instance.getUsername(), instance.getPassword());
                props.put("sasl.jaas.config", jaasCfg);
            }

            ProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);
            return new KafkaTemplate<String, Object>(factory);
        });
    }
}
