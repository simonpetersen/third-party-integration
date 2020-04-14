package dtu.openhealth.integration.kafka.publisher

import dtu.openhealth.integration.shared.dto.OmhDTO

interface KafkaProducerService {
    fun sendOmhData(omhDTO: OmhDTO)
}
