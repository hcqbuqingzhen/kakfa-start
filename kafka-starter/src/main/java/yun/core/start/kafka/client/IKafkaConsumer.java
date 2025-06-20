package yun.core.start.kafka.client;

public interface IKafkaConsumer<T> {
    /**
     * 接收到消息时的处理逻辑
     */
    void handleMessage(T message);

    String getTopic();

    String getGroupId();

    String getInstanceName();

    int getConcurrency();
}
