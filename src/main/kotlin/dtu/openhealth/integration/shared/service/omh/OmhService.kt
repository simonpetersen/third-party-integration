package dtu.openhealth.integration.shared.service.omh

import dtu.openhealth.integration.shared.dto.OmhDTO

interface OmhService {
    fun saveNewestOmhData(dto: OmhDTO)
}