package dtu.openhealth.integration.shared.service.omh

import dtu.openhealth.integration.shared.dto.OmhDTO

interface IOmhService {
    fun saveNewestOmhData(dto: OmhDTO)
}