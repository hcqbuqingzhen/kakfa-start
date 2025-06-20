package yun.core.start.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ConfigurationProperties(prefix = "yun.kafka-service")
public class KafkaServiceProperties {

    private String defaultName;

    private final Map<String, KafkaInstance> instances = new HashMap<>();


    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public Map<String, KafkaInstance> getInstances() {
        return instances;
    }

    /**
     * 解析 alias 对应的 KafkaInstance
     */
    public KafkaServiceProperties.KafkaInstance resolveKafkaInstance(String name) {
        KafkaInstance instance = instances.get(name);
        if (instance == null) {
            throw new IllegalArgumentException("Kafka 实例 [" + name + "] 未配置");
        }
        return instance;
    }

    /**
     * 获取默认 alias：先用配置 defaultName，否则用第一个实例 alias
     */
    public String resolveDefaultAlias() {
        if (StringUtils.hasText(defaultName)) {
            return defaultName;
        }
        return instances.keySet().stream()
                .filter(StringUtils::hasText)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available alias found in Kafka instances"));
    }
    public static class KafkaInstance {
        private String serverAddr;
        private String username;
        private String password;

        // Getter/Setter

        public String getServerAddr() { return serverAddr; }
        public void setServerAddr(String serverAddr) { this.serverAddr = serverAddr; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            KafkaInstance that = (KafkaInstance) o;
            return Objects.equals(serverAddr, that.serverAddr) && Objects.equals(username, that.username) && Objects.equals(password, that.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serverAddr, username, password);
        }
    }
}
