package dtu.openhealth.integration.shared.service.mock

import dtu.openhealth.integration.kafka.publisher.KafkaProducerService
import dtu.openhealth.integration.shared.dto.OmhDTO

class MockKafkaProducerService: KafkaProducerService {
    override fun sendOmhData(omhDTO: OmhDTO) {
        println("Received data: $omhDTO")
    }
}
