package yun.core.start.kafka.client;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import yun.core.start.kafka.annotate.KafkaConsumerBinding;
import yun.core.start.kafka.config.KafkaConsumerProperties;
import yun.core.start.kafka.config.KafkaServiceProperties;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaConsumerRegistrar implements InitializingBean {
    private final KafkaServiceProperties kafkaServiceProperties;
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final List<AbstractIKafkaConsumer<?>> handlers;

    public KafkaConsumerRegistrar(KafkaServiceProperties kafkaServiceProperties,
                                  KafkaConsumerProperties kafkaConsumerProperties,
                                  List<AbstractIKafkaConsumer<?>> handlers) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.kafkaServiceProperties = kafkaServiceProperties;
        this.handlers = handlers;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, KafkaConsumerProperties.Consumer> consumerConfigMap = kafkaConsumerProperties.getConsumers();
        // 查找对应的处理器
        handlers.forEach(handler -> {
                KafkaConsumerBinding annotation = handler.getClass().getAnnotation(KafkaConsumerBinding.class);
                if (annotation == null) {
                    throw new IllegalStateException("缺少 @KafkaConsumer 注解：" + handler.getClass().getName());
                }
                String mapkey = annotation.value();
                // 获取消费者配置
                KafkaConsumerProperties.Consumer consumerConfig = consumerConfigMap.get(mapkey);
                if (consumerConfig == null) {
                    throw new IllegalStateException("配置中找不到消费者：" + mapkey);
                }
                //检验前三个配置,不能为空
                if (consumerConfig.getInstanceName() == null || consumerConfig.getTopic() == null || consumerConfig.getGroupId() == null) {
                    throw new IllegalStateException("消费者配置缺少必要参数：" + mapkey);
                }
                handler.setTopic(consumerConfig.getTopic());
                handler.setGroupId(consumerConfig.getGroupId());
                handler.setInstanceName(consumerConfig.getInstanceName());
                // 设置并发数
                if (consumerConfig.getConcurrency() <= 0) {
                    handler.setConcurrency(1);
                }else {
                    handler.setConcurrency(consumerConfig.getConcurrency());
                }

            });
        //循环注册
        handlers.forEach(this::registerListener);
    }

    /**
     * 注册消费者
     * @param handler
     * @param <T>
     */
    private <T> void registerListener(IKafkaConsumer<T> handler) {
        //消费者工厂
        ConsumerFactory<String, ?> factory = createConsumerFactory(handler);
        //监听器容器
        ContainerProperties containerProps = new ContainerProperties(handler.getTopic());
        containerProps.setGroupId(handler.getGroupId());
        // 将消息反序列化为 valueType 类型，并分发给对应 handler
        containerProps.setMessageListener((MessageListener<String, T>) record -> {
            handler.handleMessage(record.value());
        });
        ConcurrentMessageListenerContainer<String, ?> container = new ConcurrentMessageListenerContainer<>(factory, containerProps);
        container.setConcurrency(handler.getConcurrency());
        container.start();
    }

    public <T> ConsumerFactory<String, T> createConsumerFactory( IKafkaConsumer<T> handler) {

        Class<T> valueType = resolveGenericType(handler);

        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(valueType);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setRemoveTypeHeaders(true);

        Map<String, Object> props = new HashMap<>();

        KafkaServiceProperties.KafkaInstance kafkaInstance = kafkaServiceProperties.resolveKafkaInstance(handler.getInstanceName());

        if (kafkaInstance.getServerAddr() == null) {
            throw new IllegalArgumentException("Kafka instance server address cannot be null for instance: " + handler.getInstanceName());
        }
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaInstance.getServerAddr());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, handler.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> resolveGenericType(IKafkaConsumer<T> handler) {
        Type[] interfaces = handler.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getRawType() instanceof Class<?> rawClass &&
                        IKafkaConsumer.class.isAssignableFrom(rawClass)) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    if (actualType instanceof Class<?>) {
                        return (Class<T>) actualType;
                    }
                }
            }
        }
        throw new IllegalStateException("无法解析 KafkaConsumerHandler 的泛型类型: " + handler.getClass());
    }
}
