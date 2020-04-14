package dtu.openhealth.integration.kafka.publisher.property

object KafkaProducerProperties {
    const val BOOTSTRAP_SERVERS = "localhost:9092"
    const val STRING_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer"
    const val OMH_SERIALIZER = "dtu.openhealth.integration.shared.util.serialization.OmhDTOSerializer"
    const val ACKS = "1"
    const val TOPIC = "omh-data"
}
