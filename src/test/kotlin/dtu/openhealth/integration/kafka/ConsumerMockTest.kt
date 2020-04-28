package dtu.openhealth.integration.kafka

import dtu.openhealth.integration.shared.dto.OmhDTO
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kafka.client.consumer.KafkaReadStream
import io.vertx.reactivex.core.Vertx
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.TopicPartition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


@ExtendWith(VertxExtension::class)
class ConsumerMockTest {

    @Test
    fun testConsume(vertx: Vertx, testContext: VertxTestContext) {

        val omhDTO = OmhDTO(userId = "userIdOfUser")
        val stringKey = "omh-dto"
        val topic = "omh-data"
        val partition = 0

        val mock = MockConsumer<String, OmhDTO>(OffsetResetStrategy.EARLIEST)
        val consumer: KafkaReadStream<String, OmhDTO> = KafkaReadStream.create(vertx.delegate, mock)
        consumer.handler { record: ConsumerRecord<String, OmhDTO> ->
            testContext.verify {
                assertThat(topic).isEqualTo(record.topic())
                assertThat(partition).isEqualTo(record.partition())
                assertThat(stringKey).isEqualTo(record.key())
                assertThat(omhDTO).isEqualTo(record.value())
            }
            testContext.completeNow()
        }
        consumer.subscribe(Collections.singleton(topic)) {
            mock.schedulePollTask {
                mock.rebalance(Collections.singletonList(TopicPartition(topic, partition)))
                mock.addRecord(ConsumerRecord(topic, partition, 0L, stringKey, omhDTO))
                mock.seek(TopicPartition(topic, partition), 0L)
            }
        }
    }

    @Test
    fun testBatch(vertx: Vertx, testContext: VertxTestContext) {
        val omhDTO = OmhDTO(userId = "userIdOfUser")
        val stringKey = "omh-dto"
        val topic = "omh-data"
        val partition = 0
        val maxNumberOfMessageToSend = 10
        val mock = MockConsumer<String, OmhDTO>(OffsetResetStrategy.EARLIEST)
        val consumer: KafkaReadStream<String, OmhDTO> = KafkaReadStream.create(vertx.delegate, mock)
        val count = AtomicInteger()
        consumer.handler { record: ConsumerRecord<String, OmhDTO> ->
            val messagesConsumed = count.getAndIncrement()
            if (messagesConsumed < maxNumberOfMessageToSend) {
                testContext.verify {
                    assertThat(topic).isEqualTo(record.topic())
                    assertThat(partition).isEqualTo(record.partition())
                    assertThat("$stringKey-$messagesConsumed").isEqualTo(record.key())
                    assertThat(omhDTO).isEqualTo(record.value())
                }
                if (messagesConsumed == maxNumberOfMessageToSend - 1) {
                    testContext.completeNow()
                }
            }
        }
        consumer.subscribe(Collections.singleton(topic)) {
            mock.schedulePollTask {
                mock.rebalance(Collections.singletonList(TopicPartition(topic, partition)))
                mock.seek(TopicPartition(topic, partition), 0)
                for (i in 0 until maxNumberOfMessageToSend) {
                    mock.addRecord(ConsumerRecord(topic, partition, i.toLong(), "$stringKey-$i", omhDTO))
                }
            }
        }
    }

}
