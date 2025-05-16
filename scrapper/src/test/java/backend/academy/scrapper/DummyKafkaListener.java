// file: src/test/java/backend/academy/scrapper/DummyKafkaListener.java
package backend.academy.scrapper;

import backend.academy.scrapper.dto.LinkUpdateDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

@Component
public class DummyKafkaListener {

    private static CountDownLatch latch;
    private static BlockingQueue<LinkUpdateDTO> queue;

    public static void setLatchAndQueue(CountDownLatch l, BlockingQueue<LinkUpdateDTO> q) {
        latch = l;
        queue = q;
    }

    @KafkaListener(topics = "kafka-1", groupId = "test-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void handle(LinkUpdateDTO dto) {
        if (queue != null) queue.add(dto);
        if (latch != null) latch.countDown();
    }
}
