package dtu.openhealth.integration.kafka

import dtu.openhealth.integration.shared.dto.OmhDTO
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kafka.client.producer.KafkaWriteStream
import io.vertx.reactivex.core.Context
import io.vertx.reactivex.core.Vertx
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.RuntimeException


@ExtendWith(VertxExtension::class)
class ProducerMockTest {


    @Test
    fun testProduce(vertx: Vertx, testContext: VertxTestContext) {

        val omhDTO = OmhDTO(userId = "userIdOfUser")
        val stringKey = "omh-dto"
        val topic = "omh-data"
        val partition = 0

        val mock = MockProducer<String, OmhDTO>()
        val producer = KafkaWriteStream.create(vertx.delegate, mock)

        var sent = 0
        while (!producer.writeQueueFull()) {
            producer.write(ProducerRecord<String, OmhDTO>(topic, partition, 0L, stringKey, omhDTO))
            sent++
        }

        producer.drainHandler {
            testContext.verify {
                assertThat(Context.isOnVertxThread()).isTrue()
                assertThat(Context.isOnEventLoopThread()).isTrue()
                testContext.completeNow()
            }
        }

        for (i in 0 until sent / 2) {
            if (testContext.failed()) {
                mock.errorNext(RuntimeException())
            } else {
                mock.completeNext()
            }
        }
        if (testContext.failed()) {
            mock.errorNext(RuntimeException())
        } else {
            mock.completeNext()
        }
        assertThat(producer.writeQueueFull()).isFalse()
    }

}
