package dtu.openhealth.integration.kafka.producer

import dtu.openhealth.integration.shared.dto.OmhDTO

interface IKafkaProducerService {
    fun sendOmhData(omhDTO: OmhDTO)
}
