package dtu.openhealth.integration.kafka.consumer.property

object KafkaConsumerProperties {
    const val BOOTSTRAP_SERVERS = "localhost:9092"
    const val STRING_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer"
    const val OMH_DESERIALIZER = "dtu.openhealth.integration.shared.util.serialization.OmhDTODeserializer"
    const val GROUP_ID = "my_group"
    const val AUTO_OFFSET_RESET = "earliest"
    const val ENABLE_AUTO_COMMIT = "false"
    const val TOPIC = "omh-data"
}
