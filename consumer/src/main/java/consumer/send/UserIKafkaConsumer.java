package consumer.send;

import consumer.dto.User;
import org.springframework.stereotype.Component;
import yun.core.start.kafka.annotate.KafkaConsumerBinding;
import yun.core.start.kafka.client.AbstractIKafkaConsumer;
import yun.core.start.kafka.client.IKafkaConsumer;

@Component
@KafkaConsumerBinding("consumer1")
public class UserIKafkaConsumer extends AbstractIKafkaConsumer<User> implements IKafkaConsumer<User> {
    @Override
    protected void processMessage(User message) {
        System.out.println(message.toString());
    }

}
