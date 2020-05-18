package dtu.openhealth.integration.shared.service.mock

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.dto.OmhDTO

class MockKafkaProducerService: IKafkaProducerService {
    override fun sendOmhData(omhDTO: OmhDTO) {
        println("Received data: $omhDTO")
    }
}
