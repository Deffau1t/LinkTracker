package backend.academy.bot.config;

import backend.academy.bot.dto.LinkUpdateDTO;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

/**
 * KafkaConsumerConfig - конфигурация Kafka для приема сообщений.
 */

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    /**
     * Метод для создания потребителя сообщений топика Kafka.
     * @return Объект типа ConsumerFactory, создающий потребителя сообщений.
     */
    @Bean
    public ConsumerFactory<String, LinkUpdateDTO> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bot-group");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            ErrorHandlingDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            ErrorHandlingDeserializer.class);

        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
            JsonDeserializer.class.getName());

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
            StringDeserializer.class.getName());

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
            LinkUpdateDTO.class.getName());

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Метод для создания потребителя сообщений топика Kafka.
     * @param consumerFactory - фабрика потребителей сообщений Kafka.
     * @param kafkaTemplate - шаблон Kafka для отправки сообщений.
     * @return Объект типа ConcurrentKafkaListenerContainerFactory,
     * который создает потребителя сообщений.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdateDTO>
    kafkaListenerContainerFactory(
        final ConsumerFactory<String, LinkUpdateDTO> consumerFactory,
        final KafkaTemplate<String, Object> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdateDTO>
            factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, _) -> new org.apache.kafka.common.TopicPartition(
                    record.topic() + ".DLT", record.partition()
                )
            );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            recoverer, new FixedBackOff(0L, 2)
        );
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    /**
     * Метод для создания шаблона Kafka для отправки сообщений.
     * @param pf - фабрика производителей сообщений Kafka.
     * @return Объект типа KafkaTemplate для отправки сообщений.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
        final ProducerFactory<String, Object> pf
    ) {
        return new KafkaTemplate<>(pf);
    }

    /**
     * Метод для создания фабрики производителей сообщений Kafka.
     * @return Объект типа ProducerFactory для создания фабрики
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new DefaultKafkaProducerFactory<>(props);
    }
}
