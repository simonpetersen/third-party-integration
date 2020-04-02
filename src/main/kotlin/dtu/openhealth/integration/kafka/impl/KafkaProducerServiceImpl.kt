package dtu.openhealth.integration.kafka.impl

import dtu.openhealth.integration.kafka.KafkaProducerService
import dtu.openhealth.integration.kafka.property.KafkaProducerProperties
import dtu.openhealth.integration.shared.dto.OmhDTO
import io.vertx.reactivex.kafka.client.producer.KafkaProducer
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord
import io.vertx.reactivex.core.Vertx
import java.util.HashMap

class KafkaProducerServiceImpl(vertx: Vertx) : KafkaProducerService {

    private var producer: KafkaProducer<String, OmhDTO>
    init {
        val config: MutableMap<String, String> = HashMap()
        config["bootstrap.servers"] = KafkaProducerProperties.BOOTSTRAP_SERVERS
        config["key.serializer"] = KafkaProducerProperties.STRING_SERIALIZER
        config["value.serializer"] = KafkaProducerProperties.OMH_SERIALIZER
        config["acks"] = KafkaProducerProperties.ACKS
        producer = KafkaProducer.create(vertx, config)
    }
    override fun sendOmhData(omhDTO: OmhDTO) {
        producer.send(KafkaProducerRecord.create(KafkaProducerProperties.TOPIC, omhDTO))
    }
}
