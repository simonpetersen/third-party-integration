package dtu.openhealth.integration.kafka.producer

import dtu.openhealth.integration.shared.dto.OmhDTO

interface KafkaProducerService {
    fun sendOmhData(omhDTO: OmhDTO)
}
