package yun.core.start.kafka.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public  abstract class AbstractIKafkaConsumer<T> implements IKafkaConsumer<T> {
    private static final Logger log =  LoggerFactory.getLogger(AbstractIKafkaConsumer.class);
    protected String topic;
    protected String groupId;
    protected String instanceName;
    protected int concurrency = 1;
    //json格式化
    private static final ObjectMapper objectMapper  = new ObjectMapper();

    public AbstractIKafkaConsumer() {

    }

    @Override
    public void handleMessage(T message) {
        try {
            processMessage(message);
        } catch (Exception e) {
            log.error("消费消息错误{}", message, e);
        }
    }

    // 钩子方法，默认实现可重写
    protected void preProcess(T  message) {
        // 通用预处理逻辑（可空）
    }

    // 抽象方法，必须由子类实现消息处理逻辑
    protected abstract void processMessage(T message);

    protected void postProcess(T message) {
        // 通用后置逻辑（如日志记录等）
    }

    public String getTopic() {
        return topic;
    }

    public String getGroupId() {
        return groupId;
    }
    public String getInstanceName() {
        return instanceName;
    }
    public int getConcurrency() {
        return concurrency;
    }

    //set方法
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }
}
