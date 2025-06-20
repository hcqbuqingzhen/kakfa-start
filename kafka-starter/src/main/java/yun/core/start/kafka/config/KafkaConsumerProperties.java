package yun.core.start.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ConfigurationProperties(prefix = "yun.kafka-consumer")
public class KafkaConsumerProperties {

    private final Map<String, Consumer> consumers = new HashMap<>();

    public Map<String, Consumer> getConsumers() {
        return consumers;
    }
    public static class Consumer {
        private String instanceName;
        private String topic;
        private String groupId;
        private int concurrency = 1;

        public String getInstanceName() {
            return instanceName;
        }
        public void setInstanceName(String instanceName) {
            this.instanceName = instanceName;
        }
        public String getTopic() {
            return topic;
        }
        public void setTopic(String topic) {
            this.topic = topic;
        }
        public String getGroupId() {
            return groupId;
        }
        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
        public int getConcurrency() {
            return concurrency;
        }
        public void setConcurrency(int concurrency) {
            this.concurrency = concurrency;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Consumer consumer = (Consumer) o;
            return concurrency == consumer.concurrency && Objects.equals(instanceName, consumer.instanceName) && Objects.equals(topic, consumer.topic) && Objects.equals(groupId, consumer.groupId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(instanceName, topic, groupId, concurrency);
        }

        //toString
        @Override
        public String toString() {
            return "Consumer{" +
                   "instanceName='" + instanceName + '\'' +
                   ", topic='" + topic + '\'' +
                   ", groupId='" + groupId + '\'' +
                   ", concurrency=" + concurrency +
                   '}';
        }

    }
}
